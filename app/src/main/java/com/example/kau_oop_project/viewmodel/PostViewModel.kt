package com.example.kau_oop_project.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository() // Repository 초기화
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _selectedPost = MutableLiveData<Post>()
    val selectedPost: LiveData<Post?> get() = _selectedPost

    // Firebase에서 데이터 읽어오기
    fun retrievePosts() {
        viewModelScope.launch {
            val postList:List<Post> = postRepository.retrievePosts()
            Log.d("PostViewModel", "Posts retrieved successfully: ${postList.size}")
            _posts.value = postList
        }
    }

    // 제목 기준으로 정렬
    fun sortPostsByTitle() {
        _posts.value = _posts.value?.sortedBy { it.postTitle }
    }

    fun sortPostsByTimeStamp() {
        _posts.value = _posts.value?.sortedBy { it.postTimeStamp }
    }

    fun sortPostsByRecommendCount() {
        _posts.value = _posts.value?.sortedBy { it.postRecommendCount }
    }

    fun sortPostsByViewCount() {
        _posts.value = _posts.value?.sortedBy { it.postViewCount }
    }


    // 데이터 업로드 함수
    fun uploadPost(post: Post) {
        viewModelScope.launch {
            postRepository.uploadPost(post) // 포스트를 업로드
            retrievePosts() // 업로드 후 리스트 갱신
        }
    }

    // 선택한 게시물을 설정하는 함수
    fun selectPost(post: Post) {
        _selectedPost.value = post
    }
}
