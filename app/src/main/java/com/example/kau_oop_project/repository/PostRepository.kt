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
    suspend fun uploadReply(postId: String, reply: Reply) {
        withContext(Dispatchers.IO) {
            // Firebase가 생성한 고유 댓글 ID
            val replyId = repliesRef.push().key ?: return@withContext

            // 현재 시간을 타임스탬프로 사용
            val newReply = reply.copy(replyId = replyId, replyTimeStamp = System.currentTimeMillis(), replyParentPostId = postId)
            // Firebase에 댓글 데이터 저장
            repliesRef.child(replyId).setValue(newReply).await()

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
            val snapshot = repliesRef.child(postId).get().await()

            // 댓글 리스트 생성
            snapshot.children.mapNotNull { replySnapshot ->
                replySnapshot.getValue(Reply::class.java)
            }.sortedBy { it.replyTimeStamp } // 시간순 정렬
        }
    }

    // PostRepository.kt
    suspend fun deleteReplyByReplyId(replyId: String) {
        withContext(Dispatchers.IO) {

        }
    }
}
