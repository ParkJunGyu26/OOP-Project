package com.example.kau_oop_project.ui.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.response.LoginResponse
import com.example.kau_oop_project.data.model.response.UserInfo
import com.example.kau_oop_project.data.model.response.UserResponse
import com.example.kau_oop_project.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    
    private val _currentUser = MutableLiveData<String?>()
    val currentUser: LiveData<String?> = _currentUser

    // 게시판, 채팅에서 필요한 유저 정보
    private val _userInfoList = MutableLiveData<List<UserInfo?>>()
    val userInfoList: LiveData<List<UserInfo?>> = _userInfoList

    fun loginUser(loginResponse: LoginResponse.Success) {
        _currentUser.value = loginResponse.uid
    }

    fun logoutUser() {
        if (isLoggedIn()) _currentUser.value = null
    }

    private fun isLoggedIn(): Boolean = currentUser.value != null

    // 게시판 댓글의 유저 정보
    fun getUsers(uidList: List<String>?) {
        if (uidList.isNullOrEmpty()) {
            _userInfoList.value = emptyList()
            return
        }

        viewModelScope.launch {
            when (val userInfo = userRepository.getUsersInfo(uidList)) {
                is UserResponse.Success -> _userInfoList.value = userInfo.usersInfo
                is UserResponse.Error -> _userInfoList.value = emptyList()
            }
        }
    }
}