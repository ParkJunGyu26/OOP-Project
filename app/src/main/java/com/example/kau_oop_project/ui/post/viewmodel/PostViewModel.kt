package com.example.kau_oop_project.ui.post.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.data.model.Reply
import com.example.kau_oop_project.repository.PostRepository
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository() // Repository 초기화

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _nowPost = MutableLiveData<Post?>()
    val nowPost: LiveData<Post?> get() = _nowPost

    private val _selectedReply = MutableLiveData<Reply?>()
    val selectedReply: LiveData<Reply?> get() = _selectedReply

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    private val _replies = MutableLiveData<List<Reply>>()
    val replies: LiveData<List<Reply>> get() = _replies

    private val _uploadResult = MutableLiveData<Result<Boolean>>()
    val uploadResult: LiveData<Result<Boolean>> get() = _uploadResult


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
            _uploadResult.value = Result.failure(Exception(it)) // 실패 메시지 전달
            return
        }

        val nowPostId=nowPost.value?.postId

        // 유효성 검사 통과 후, Repository에 데이터 전달
        viewModelScope.launch {
            try {
                if(nowPostId==null)postRepository.uploadPost(title, tag, content, userUid)
                else postRepository.updatePost(nowPostId,title,tag,content,userUid)
                val tagsList = if (_tags.value?.toList().isNullOrEmpty()) null else _tags.value?.toList()
                retrievePosts(searchTags = tagsList) // 업로드 후 태그로 게시글 검색

                _uploadResult.value = Result.success(true) // 업로드 성공
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e) // 업로드 실패
            }
        }
    }

    // Post ID를 기반으로 게시글을 가져오는 함수
    fun retrievePostById(postId: String) {
        viewModelScope.launch {
            try {
                // Repository에서 Post ID로 게시물 검색
                val post = postRepository.retrievePostById(postId)
                _nowPost.value = post // 검색된 게시물을 LiveData에 저장

                // 선택된 게시글 ID를 기반으로 댓글 로드
                nowPost.value?.postId?.let { postId ->
                    retrieveReplies(postId)
                }
                Log.d("PostViewModel", "Post retrieved successfully: $postId")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error retrieving post by ID $postId: ${e.message}")
            }
        }
    }

    fun removeNowPost() {
        _nowPost.value=null
    }

    // 댓글 업로드
    fun uploadReply(content: String,uid: String) {
        // 유효성 검사
        val postId=nowPost.value?.postId?:""
        Log.e("PostViewModel", "con : $content uid : $uid postId : $postId ")
        val validationMessage = when {
            postId.isNullOrBlank() -> "게시글 ID를 찾을 수 없습니다."
            uid.isNullOrBlank() -> "사용자 정보를 찾을 수 없습니다."
            content.isNullOrBlank() -> "댓글 내용을 입력해주세요."
            else -> null
        }

        // 유효성 검사 통과 시
        validationMessage?.let {
            _uploadResult.value = Result.failure(Exception(it)) // 실패 메시지 전달
            return
        }

        // 유효성 검사 통과 후, Repository에 데이터 전달
        viewModelScope.launch {
            try {
                // Firebase에 댓글 업로드 요청
                postRepository.uploadReply(content, postId, uid)
                retrieveReplies(postId) // 업로드 후 해당 게시글의 댓글 목록 갱신
                _uploadResult.value = Result.success(true) // 업로드 성공
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e) // 업로드 실패
                Log.e("PostViewModel", "Error uploading reply: ${e.message}")
            }
        }
    }


    // 댓글 가져오기
    fun retrieveReplies(postId: String) {
        viewModelScope.launch {
            try {
                val replies = postRepository.retrieveRepliesByPostId(postId)
                _replies.value = replies // 댓글 데이터를 LiveData에 저장
                Log.d("PostViewModel", "Replies retrieved successfully: ${replies.size}")
                Log.d("PostViewModel", "${replies.joinToString()}")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error retrieving replies: ${e.message}")
            }
        }
    }

    fun deletePost() {
        val postId = _nowPost.value?.postId
        postId?.let {
            viewModelScope.launch {
                try {
                    // 1. 게시글 삭제
                    postRepository.deletePost(it) // 댓글과 게시글 삭제
                    // 2. 게시글 삭제 후 리스트 갱신
                    val tagsList =
                        if (_tags.value?.toList().isNullOrEmpty()) null else _tags.value?.toList()
                    retrievePosts(searchTags = tagsList) // 업로드 후 태그로 게시글 검색
                } catch (e: Exception) {
                    Log.e("PostViewModel", "Error deleting post $it: ${e.message}")
                }
            }
        }
    }

    fun deleteReply(replyId: String) {
        val postId = nowPost.value?.postId ?: return // 현재 게시글 ID를 가져옴
        viewModelScope.launch {
            try {
                // Repository에 댓글 삭제 요청
                postRepository.deleteReplyByReplyId(replyId)
                retrieveReplies(postId) // 댓글 삭제 후 해당 게시글의 댓글 목록 갱신
                Log.d("PostViewModel", "Reply deleted successfully: $replyId")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error deleting reply: ${e.message}")
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

    fun formatTime(timeInMillis: Long): String {
        // Date 객체로 변환
        val date = Date(timeInMillis)

        // 원하는 형식으로 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    // 조회수 증가
    fun incrementViewCount(postId: String) {
        viewModelScope.launch {
            postRepository.incrementViewCount(postId)
            retrievePostById(postId)
        }
    }

    // 추천수 증가
    fun incrementRecommendCount() {
        nowPost.value?.postId?.let{
            viewModelScope.launch {
                postRepository.incrementRecommendCount(it)
                retrievePostById(it)
            }
        }
    }
}
