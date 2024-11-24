package com.example.kau_oop_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val repository = LoginRepository()

    fun validUser(inputEmail: String?, inputPassword: String?) {
        viewModelScope.launch {
            val existEmail = repository.checkEmail(inputEmail)
            println("existEmail : ${existEmail}")
            if (existEmail) {
                _loginResult.value = repository.getPassword(inputEmail, inputPassword)
            } else {
                _loginResult.value = false
            }
        }
    }
}