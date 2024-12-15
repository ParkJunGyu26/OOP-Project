package com.example.kau_oop_project.ui.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kau_oop_project.data.model.response.LoginResponse
import com.example.kau_oop_project.data.model.user.User

class UserViewModel : ViewModel() {
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun loginUser(loginResponse: LoginResponse.Success) {
        _currentUser.value = User(
            uid = loginResponse.uid,
            userEmail = loginResponse.userEmail,
            name = loginResponse.name,
            school = loginResponse.school,
            major = loginResponse.major,
            profileImageUrl = loginResponse.profileImageUrl
        )
    }

    fun logoutUser() {
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = currentUser.value != null
}