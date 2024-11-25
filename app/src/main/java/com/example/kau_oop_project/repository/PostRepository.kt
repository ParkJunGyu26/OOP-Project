package com.example.kau_oop_project.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.kau_oop_project.data.model.Post
import kotlinx.coroutines.tasks.await
import java.util.*

class PostRepository {

    private val database = FirebaseDatabase.getInstance()
    private val postsRef = database.getReference("posts")

    suspend fun uploadPost(post: Post) {
        withContext(Dispatchers.IO) {
            // Firebase가 생성한 고유 ID
            val DBId = postsRef.push().key ?: return@withContext

            // timestamp를 현재 시간으로 설정
            val writetenTime = System.currentTimeMillis()

            // 새로운 Post 객체 생성, timestamp가 포함된 post
            val newPost = post.copy(postDBId = DBId, postTimeStamp = writetenTime)

            // Firebase에 새로운 Post 저장
            postsRef.child(DBId).setValue(newPost).await()
        }
    }

    suspend fun retrievePosts(): List<Post> {
        return withContext(Dispatchers.IO) {
            val snapshot = postsRef.orderByChild("timeStamp").get().await()

            // timestamp 기준으로 정렬된 게시글 가져오기
            snapshot.children.mapNotNull { postSnapshot ->
                postSnapshot.getValue(Post::class.java)
            }.sortedByDescending { it.postTimeStamp } // 최신 글이 위로 오도록 정렬
        }
    }
}
