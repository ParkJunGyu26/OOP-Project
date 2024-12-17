package com.example.kau_oop_project.ui.user.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.response.LoginResponse
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
    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    // 이메일 형식 체크
    private val _emailCheck = MutableLiveData<EmailValidation>()
    val emailCheck: LiveData<EmailValidation> = _emailCheck

    // 상태 변화 감지
    fun verifyUser(email: String?, password: String?) {
        if (email == null || password == null) {
            return
        }

        viewModelScope.launch {
            val result = repository.verifyLogin(email, password)
            _loginResult.value = result
        }
    }

    // 형식 검사
    fun validateEmailFormat(email: String?) {
        when {
            email == null -> updateEmailValidation("이메일이 null입니다.", false)
            email.isBlank() -> updateEmailValidation("이메일을 입력해주세요.", false)
            !emailPattern.matcher(email).matches() -> updateEmailValidation("이메일 형식이 올바르지 않습니다.", false)
            else -> updateEmailValidation("", true)
        }
    }

    private fun updateEmailValidation(message: String, isValid: Boolean) {
        _emailCheck.value = EmailValidation(message, isValid)
    }
}