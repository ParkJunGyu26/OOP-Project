package com.example.kau_oop_project.ui.user.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.repository.LoginRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class EmailValidation(
    val message: String = "",
    val isValid: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository()
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    // 로그인 결과
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    // 이메일 형식 체크
    private val _emailCheck = MutableLiveData<EmailValidation>()
    val emailCheck: LiveData<EmailValidation> get() = _emailCheck

    fun verifyUser(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = repository.verifyLogin(email, password)
        }
    }

    fun validateEmailFormat(email: String) {
        when {
            email.isBlank() -> updateEmailValidation("이메일을 입력해주세요.", false)
            !emailPattern.matcher(email).matches() -> updateEmailValidation("이메일 형식이 올바르지 않습니다.", false)
            else -> updateEmailValidation("", true)
        }
    }

    private fun updateEmailValidation(message: String, isValid: Boolean) {
        _emailCheck.value = EmailValidation(message, isValid)
    }
}