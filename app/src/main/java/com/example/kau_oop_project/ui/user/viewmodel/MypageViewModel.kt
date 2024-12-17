package com.example.kau_oop_project.ui.user.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.User
import com.example.kau_oop_project.data.model.response.MyInfo
import com.example.kau_oop_project.data.model.response.MypageResponse
import com.example.kau_oop_project.repository.MypageRepository
import kotlinx.coroutines.launch

class MypageViewModel : ViewModel() {
    private val mypageRepository = MypageRepository()
    
    private val _mypageResult = MutableLiveData<MypageResponse>()
    val mypageResult: LiveData<MypageResponse> = _mypageResult

    // 프로필 이미지 상태
    private val _profileImageState = MutableLiveData<String>()
    val profileImageState: LiveData<String> = _profileImageState

    // 업데이트 결과
    private val _updateResult = MutableLiveData<UpdateResponse>()
    val updateResult: LiveData<UpdateResponse> = _updateResult

    // 이미지 업로드 상태
    private val _imageUploadState = MutableLiveData<ImageUploadState>()
    val imageUploadState: LiveData<ImageUploadState> = _imageUploadState

    // 임시 이미지 URL 저장
    private var tempImageUrl: String? = null

    fun getUserInfo(uid: String) {
        viewModelScope.launch {
            _mypageResult.value = mypageRepository.getDetailUserInfo(uid)
        }
    }

    fun setDefaultImage() {
        tempImageUrl = User().profileImageUrl
        tempImageUrl?.let { url ->
            _imageUploadState.value = ImageUploadState.Success(url)
            _profileImageState.value = url  // UI 업데이트
        }
    }

    fun updateUserInfo(updatedUser: MyInfo) {
        viewModelScope.launch {
            // 실제 수정 버튼 클릭 시에만 데이터베이스 업데이트
            val finalUser = tempImageUrl?.let {
                updatedUser.copy(profileImage = it)
            } ?: updatedUser

            when (val response = mypageRepository.updateUser(finalUser)) {
                is MypageResponse.Success -> {
                    _updateResult.value = UpdateResponse.Success
                    tempImageUrl = null  // 임시 URL 초기화
                }
                is MypageResponse.Error -> {
                    response.logError()
                    _updateResult.value = UpdateResponse.Error("내 정보 수정에서 에러 발생")
                }
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            _imageUploadState.value = ImageUploadState.Loading
            try {
                tempImageUrl = mypageRepository.uploadImageToStorage(imageUri)
                tempImageUrl?.let { url ->
                    _imageUploadState.value = ImageUploadState.Success(url)
                } ?: run {
                    _imageUploadState.value = ImageUploadState.Error("이미지 URL 생성 실패")
                }
            } catch (e: Exception) {
                _imageUploadState.value = ImageUploadState.Error(e.message ?: "이미지 업로드 실패")
            }
        }
    }
}

// 업데이트 응답을 위한 sealed class
sealed class UpdateResponse {
    data object Success : UpdateResponse()
    data class Error(val message: String) : UpdateResponse()
}

sealed class ImageUploadState {
    data object Loading : ImageUploadState()
    data class Success(val imageUrl: String) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}