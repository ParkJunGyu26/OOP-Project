package com.example.kau_oop_project.repository

import android.util.Log
import com.example.kau_oop_project.data.model.ContentType
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.data.model.PostContent
import com.example.kau_oop_project.data.model.Reply
import kotlinx.coroutines.tasks.await
import java.util.*

class PostRepository {

    private val database = FirebaseDatabase.getInstance()
    private val postsRef = database.getReference("posts")
    private val repliesRef = database.getReference("replies")
    private val userRef=database.getReference("user")

    suspend fun uploadPost(title: String, tag: String, content: String, uid: String) {
        withContext(Dispatchers.IO) {
            // Firebase가 생성한 고유 ID
            val databaseId = postsRef.push().key ?: return@withContext

            // timestamp를 현재 시간으로 설정
            val writetenTime = System.currentTimeMillis()

            // 내용 입력란을 줄 단위로 분리하여 PostContent 리스트 생성
            val postContents = content.lines().map {
                PostContent(type = ContentType.TEXT, content = it)
            }
            // 새로운 Post 객체 생성
            val newPost = Post(
                postId = databaseId,
                postTag = tag,
                postTitle = title,
                postRecommendCount = 0,
                postViewCount = 0,
                postAuthorId = uid,
                postContent = postContents,
                postReplyIdList = emptyList(),
                postTimeStamp = writetenTime
            )

            // Firebase에 새로운 Post 저장
            postsRef.child(databaseId).setValue(newPost).await()
        }
    }

    suspend fun retrievePosts(user: String?, tags: List<String>?, input: String?): List<Post> {
        return withContext(Dispatchers.IO) {
            // Firebase에서 게시글을 가져옴
            val snapshot = postsRef.get().await()
            Log.d("PostRepository", "$user $tags $input")
            // 게시글을 가져온 후 필터링
            snapshot.children.mapNotNull { postSnapshot ->
                postSnapshot.getValue(Post::class.java)
            }.filter { post ->
                // user 필터링 (user가 null이 아니면 해당 userId와 일치해야 함)
                val isUserMatch = user?.let { post.postAuthorId == it } ?: true

                // tags 필터링 (searchTags가 null이 아니면 해당 태그와 일치해야 함)
                val isTagMatch = tags?.let { tags ->
                    tags.contains(post.postTag)
                } ?: true

                // searchInput 필터링 (searchInput이 null이 아니면 해당 값이 포함되어야 함)
                val isSearchInputMatch = input?.let { input ->
                    post.postTitle.contains(input, ignoreCase = true) ||
                            post.postContent.any { it.content.contains(input, ignoreCase = true) }
                } ?: true
                Log.d("PostRepository", "$isUserMatch $isTagMatch $isSearchInputMatch")
                // 모든 필터 조건을 만족하는 게시글만 반환
                isUserMatch && isTagMatch && isSearchInputMatch
            }.sortedByDescending { it.postTimeStamp } // 최신 글이 위로 오도록 정렬
        }
    }

    // 단일 게시글 조회 함수
    suspend fun retrievePostById(postId: String): Post? {
        return withContext(Dispatchers.IO) {
            try {
                // 해당 postId로 Firebase 데이터 조회
                val postSnapshot = postsRef.child(postId).get().await()
                // Snapshot 데이터를 Post 객체로 변환하여 반환
                postSnapshot.getValue(Post::class.java)
            } catch (e: Exception) {
                Log.e("PostRepository", "Error retrieving post by ID: $postId", e)
                null // 에러 발생 시 null 반환
            }
        }
    }

    // PostRepository.kt
    suspend fun deletePost(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                // 1. 해당 게시글을 가져오기
                val postSnapshot = postsRef.child(postId).get().await()
                val post = postSnapshot.getValue(Post::class.java)

                post?.let {
                    // 2. 해당 게시글의 replyList에 있는 모든 댓글 삭제
                    Log.e("PostRepository", "remove rp ${post.postReplyIdList}")
                    for (replyId in it.postReplyIdList) {
                        repliesRef.child(replyId).removeValue().await() // 댓글 삭제
                    }

                    // 3. 게시글 삭제
                    postsRef.child(postId).removeValue().await() // 게시글 삭제
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error deleting post $postId", e)
            }
        }
    }


    //댓글 업로드
    suspend fun uploadReply(content: String,postId: String, uid: String) {
        withContext(Dispatchers.IO) {
            // Firebase가 생성한 고유 댓글 ID
            val replyId = repliesRef.push().key ?: return@withContext

            // Firebase에 댓글 데이터 저장
            val reply=Reply(
                replyId=replyId,
                replyTimeStamp=System.currentTimeMillis(),
                replyAuthorId = uid,
                replyContent = content
            )
            repliesRef.child(replyId).setValue(reply).await()

            // 게시물 정보를 가져오기 위한 postRef
            val postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId)

            // 기존 게시물의 postReplyIdList를 가져오기
            val postSnapshot = postRef.get().await()

            // 기존 postReplyIdList에 댓글 ID를 추가한 새로운 리스트 생성
            val updatedReplyIdList = postSnapshot.child("postReplyIdList").children.map { it.value.toString() } + replyId

            // 게시물의 postReplyIdList에 댓글 ID 추가
            postRef.child("postReplyIdList").setValue(updatedReplyIdList).await()
        }
    }


    // 댓글 로드 (post ID 기준)
    suspend fun retrieveRepliesByPostId(postId: String): List<Reply> {
        return withContext(Dispatchers.IO) {
            // 게시물 정보 가져오기
            val postSnapshot = postsRef.child(postId).get().await()

            // 해당 게시물에서 댓글 ID 리스트 가져오기
            val post = postSnapshot.getValue(Post::class.java)
            val replyIdList = post?.postReplyIdList.orEmpty() // 댓글 ID 리스트

            // 댓글 ID 리스트로 댓글들 가져오기
            val replies = replyIdList.mapNotNull { replyId ->
                val replySnapshot = repliesRef.child(replyId).get().await()
                replySnapshot.getValue(Reply::class.java)
            }

            Log.e("PostRepository", "Error deleting post $replies.size")
            // 댓글 리스트를 시간순으로 정렬
            replies.sortedBy { it.replyTimeStamp }
        }
    }

    // 댓글 삭제 함수
    suspend fun deleteReplyByReplyId(replyId: String) {
        withContext(Dispatchers.IO) {
            try {
                // 1. 댓글 정보를 가져오기
                val replySnapshot = repliesRef.child(replyId).get().await()
                val reply = replySnapshot.getValue(Reply::class.java)

                // 2. 댓글이 존재하는 경우
                reply?.let {
                    // 3. 댓글에 해당하는 parentPostId를 통해 해당 게시글 정보 가져오기
                    val postId = it.replyParentPostId // 댓글에 연결된 게시글의 ID

                    // 4. 해당 게시글을 가져오기
                    val postRef = postsRef.child(postId)
                    val postSnapshot = postRef.get().await()
                    val post = postSnapshot.getValue(Post::class.java)

                    // 5. 게시글에서 postReplyIdList 가져오기
                    post?.let {
                        // 기존 댓글 리스트에서 해당 댓글 ID 제거
                        val updatedReplyIdList = post.postReplyIdList.filterNot { it == replyId }

                        // 6. 게시글의 postReplyIdList에 업데이트된 댓글 ID 리스트 저장
                        postRef.child("postReplyIdList").setValue(updatedReplyIdList).await()

                        // 7. 댓글 삭제
                        repliesRef.child(replyId).removeValue().await()

                        Log.d("PostRepository", "댓글 삭제 완료: $replyId")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "댓글 삭제 중 오류 발생: $replyId", e)
            }
        }
    }

    // 게시글 조회수 증가
    suspend fun incrementViewCount(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                // 해당 게시글을 가져오기
                val postSnapshot = postsRef.child(postId).get().await()
                val currentPost = postSnapshot.getValue(Post::class.java)

                // 게시글 조회수 증가
                currentPost?.let {
                    val newViewCount = it.postViewCount + 1 // 현재 조회수에 1을 더함
                    postsRef.child(postId).child("postViewCount").setValue(newViewCount).await() // Firebase에 반영
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error incrementing view count for post $postId", e)
            }
        }
    }

    // 게시글 추천수 증가
    suspend fun incrementRecommendCount(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                // 해당 게시글을 가져오기
                val postSnapshot = postsRef.child(postId).get().await()
                val currentPost = postSnapshot.getValue(Post::class.java)

                // 게시글 추천수 증가
                currentPost?.let {
                    val newRecommendCount = it.postRecommendCount + 1 // 현재 추천수에 1을 더함
                    postsRef.child(postId).child("postRecommendCount").setValue(newRecommendCount).await() // Firebase에 반영
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error incrementing recommend count for post $postId", e)
            }
        }
    }
}
