package com.example.kau_oop_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository() // Repository 직접 초기화

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    // 데이터 읽어오는 함수
    fun retrievePosts() {
        viewModelScope.launch {
            try {
                val postList = postRepository.retrievePosts()
                _posts.value = postList
            } catch (e: Exception) {
                _posts.value = emptyList()
            }
        }
    }

    // 데이터 업로드 함수
    fun uploadPost(post: Post) {
        viewModelScope.launch {
            try {
                postRepository.uploadPost(post) // 포스트를 업로드
                retrievePosts() // 업로드 후 리스트 갱신
            } catch (e: Exception) {
                // 에러 처리 로직
            }
        }
    }
}
