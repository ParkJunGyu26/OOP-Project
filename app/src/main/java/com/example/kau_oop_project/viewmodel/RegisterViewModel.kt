package com.example.kau_oop_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.repository.LoginRepository
import com.example.kau_oop_project.repository.RegisterRepository
import kotlinx.coroutines.launch

data class User(
    val email: String,
    val password: String,
    val school: String,
    val name: String,
    val major: String
)

class RegisterViewModel : ViewModel() {
    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> get() = _userInfo

//    private val registerRepository = RegisterRepository()
//    init {
//        registerRepository.observeInfo(_userInfo)
//    }
//    private val loginRepository = LoginRepository()


    // ㅇㅇㅇ

    fun registerUser() {

    }

//    val
}