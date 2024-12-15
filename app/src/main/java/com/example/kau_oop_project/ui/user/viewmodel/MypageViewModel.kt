package com.example.kau_oop_project.ui.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.response.MypageResponse
import com.example.kau_oop_project.repository.UserRepository
import kotlinx.coroutines.launch

class MypageViewModel : ViewModel() {
    private val repository = UserRepository()
    
    private val _mypageResult = MutableLiveData<MypageResponse>()
    val mypageResult: LiveData<MypageResponse> = _mypageResult
    
    fun fetchUserInfo(uid: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserInfo(uid)
                _mypageResult.value = if (user != null) {
                    MypageResponse.Success(user)
                } else {
                    MypageResponse.Error.UserNotFound
                }
            } catch (e: Exception) {
                _mypageResult.value = MypageResponse.Error.Unknown(e.message ?: "Unknown error")
            }
        }
    }
} 