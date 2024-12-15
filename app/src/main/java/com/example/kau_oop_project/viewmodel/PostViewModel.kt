package com.example.kau_oop_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.ContentType
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.data.model.PostContent
import com.example.kau_oop_project.data.model.Reply
import com.example.kau_oop_project.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository() // Repository 초기화

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> get() = _selectedPost

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    private val _replies = MutableLiveData<List<Reply>>()
    val replies: LiveData<List<Reply>> get() = _replies

    private val _postUploadResult = MutableLiveData<Result<Boolean>>()
    val postUploadResult: LiveData<Result<Boolean>> get() = _postUploadResult


    init {
        _tags.value = emptyList() // 초기 태그 리스트를 빈 상태로 설정
    }

    // Firebase에서 데이터 읽어오기
    fun retrievePosts(searchUserId:String?=null,searchTags: List<String>? = null,searchInput: String? = null) {
        viewModelScope.launch {
            try {
                val postList: List<Post> = postRepository.retrievePosts(searchUserId,searchTags,searchInput)
                _posts.value=postList
                Log.d("PostViewModel", "Posts retrieved successfully: ${postList.size}")
                Log.d("PostViewModel", "option: uid $searchUserId, input : $searchInput, tags : $searchTags")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error retrieving posts: ${e.message}")
            }
        }
    }

    // 게시글 업로드
    fun uploadPost(title: String, tag: String, content: String, userUid: String) {
        // 유효성 검사
        val validationMessage = when {
            title.isBlank() -> "제목을 입력해주세요."
            tag.isBlank() -> "태그를 입력해주세요."
            content.isBlank() -> "내용을 입력해주세요."
            userUid.isNullOrBlank() -> "사용자 정보를 찾을 수 없습니다."
            else -> null
        }

        // 유효성 검사 통과 시
        validationMessage?.let {
            _postUploadResult.value = Result.failure(Exception(it)) // 실패 메시지 전달
            return
        }

        // 유효성 검사 통과 후, Repository에 데이터 전달
        viewModelScope.launch {
            try {
                // Firebase에 게시글 업로드 요청
                postRepository.uploadPost(title, tag, content, userUid)

                _postUploadResult.value = Result.success(true) // 업로드 성공
            } catch (e: Exception) {
                _postUploadResult.value = Result.failure(e) // 업로드 실패
            }
        }
    }

    // 선택한 게시물을 설정하는 함수
    fun selectPost(post: Post) {
        _selectedPost.value = post
    }

    // 댓글 업로드
    fun uploadReply(postId: String, reply: Reply) {
        viewModelScope.launch {
            try {
                postRepository.uploadReply(postId, reply)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error uploading reply: ${e.message}")
            }
        }
    }

    // 댓글 가져오기
    fun retrieveReplies(postId: String) {
        viewModelScope.launch {
            try {
                val replies = postRepository.retrieveRepliesByPostId(postId)
                _replies.value = replies
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error retrieving replies: ${e.message}")
            }
        }
    }

    fun deletePost() {
        val postId = _selectedPost.value?.postId
        postId?.let {
            viewModelScope.launch {
                try {
                    // 1. 게시글 삭제
                    postRepository.deletePost(it) // 댓글과 게시글 삭제
                    // 2. 게시글 삭제 후 리스트 갱신
                    retrievePosts()
                } catch (e: Exception) {
                    Log.e("PostViewModel", "Error deleting post $it: ${e.message}")
                }
            }
        }
    }
    
    // 태그 추가 함수
    fun addTag(tag: String) {
        val currentTags = _tags.value?.toMutableList() ?: mutableListOf()
        if (!currentTags.contains(tag)) {
            currentTags.add(tag)
            _tags.value = currentTags
        }
    }

    // 태그 제거 함수
    fun removeTag(tag: String) {
        val currentTags = _tags.value?.toMutableList() ?: mutableListOf()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
            _tags.value = currentTags
        }
    }
}
