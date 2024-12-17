package com.example.kau_oop_project.ui.user.viewmodel

import android.util.Log
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

    // 현재 접속 유저
    private val _currentUser = MutableLiveData<String?>()
    val currentUser: LiveData<String?> = _currentUser

    // 채팅에서 필요한 유저 정보
    private val _userInfoList = MutableLiveData<HashMap<String, UserInfo>>()
    val userInfoList: LiveData<HashMap<String, UserInfo>> = _userInfoList

    // 게시판에서 필요한 유저 정보
    private val _postUsersInfoList = MutableLiveData<HashMap<String, UserInfo>>()
    val postUsersInfoList: LiveData<HashMap<String, UserInfo>> = _postUsersInfoList

    fun loginUser(loginResponse: LoginResponse.Success) {
        _currentUser.value = loginResponse.uid
    }

    // 게시판 댓글과 채팅의 유저 정보( domain : 1(게시판), 2(채팅) )
    fun getUsers(uidList: List<String?>, domain: Int) {
        Log.d("PostUserViewModel", "Launch with : $uidList")
        if (uidList.isEmpty()) {
            Log.d("PostUserViewModel", "No uidList : $uidList")
            _userInfoList.value = hashMapOf()  // 빈 HashMap 생성
            return
        }

        viewModelScope.launch {
            when (val userInfo = userRepository.getUsersInfo(uidList)) {
                is UserResponse.Success -> {
                    when (domain) {
                        1 -> _postUsersInfoList.value = userInfo.usersInfo  // 게시판
                        2 -> _userInfoList.value = userInfo.usersInfo  // 채팅
                    }
                    Log.d("PostUserViewModel", "getUsersResultPost ${postUsersInfoList.value}")
                    Log.d("PostUserViewModel", "getUsersResultUser ${userInfoList.value}")
                }
                is UserResponse.Error -> _userInfoList.value = hashMapOf()  // 에러 시 빈 HashMap
            }
        }
    }
}