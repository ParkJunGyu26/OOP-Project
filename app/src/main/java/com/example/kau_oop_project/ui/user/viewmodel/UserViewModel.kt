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

    private val _repliesUsersInfoList = MutableLiveData<HashMap<String, UserInfo>>()
    val repliesUsersInfoList: LiveData<HashMap<String, UserInfo>> = _repliesUsersInfoList

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
                        2 -> _repliesUsersInfoList.value = userInfo.usersInfo // 댓글
                        3 -> _userInfoList.value = userInfo.usersInfo  // 채팅
                    }
                }
                is UserResponse.Error -> _userInfoList.value = hashMapOf()  // 에러 시 빈 HashMap
                else -> return@launch
            }
        }
    }

    // 테스트용 메서드
//    fun testGetUsers() {
//        viewModelScope.launch {
//            // 실제 DB에 있는 uid 리스트로 테스트
//            val testUids = listOf(
//                "-NkWXYZ123abc",  // 여기에 실제 DB의 uid를 넣으세요
//                "-NkWXYZ456def",
//                "-NkWXYZ789ghi",
//                "-NkWXYZ101jkl",
//                "-NkWXYZ202mno"
//            )
//
//            // 게시판용 테스트 (domain = 1)
//            getUsers(testUids, 1)
//
//            // 결과 로깅
//            postUsersInfoList.observe(/* lifecycleOwner */) { users ->
//                users?.forEach { user ->
//                    Log.d("UserTest", "게시판 유저: ${user?.name}, ${user?.userEmail}")
//                }
//            }
//
//            // 채팅용 테스트 (domain = 2)
//            getUsers(testUids, 2)
//
//            // 결과 로깅
//            userInfoList.observe(/* lifecycleOwner */) { users ->
//                users?.forEach { user ->
//                    Log.d("UserTest", "채팅 유저: ${user?.name}, ${user?.userEmail}")
//                }
//            }
//        }
//    }
}