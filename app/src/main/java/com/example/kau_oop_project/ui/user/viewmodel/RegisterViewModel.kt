package com.example.kau_oop_project.ui.user.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.repository.RegisterRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val PASSWORD_MIN_LENGTH = 8
private const val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$"
private const val ENGLISH_PATTERN = ".*[A-Za-z].*"
private const val NUMBER_PATTERN = ".*\\d.*"
private const val SPECIAL_CHAR_PATTERN = ".*[@\$!%*#?&].*"

data class User(
    val email: String,
    val password: String,
    val school: String,
    val name: String,
    val major: String
)

class FieldValidation(
    val message: String = "",
    val isValid: Boolean = false
)

data class ValidationState(
    val email: FieldValidation = FieldValidation(),
    val password: FieldValidation = FieldValidation(),
    val school: FieldValidation = FieldValidation(),
    val name: FieldValidation = FieldValidation(),
    val major: FieldValidation = FieldValidation()
) {
    fun updateEmail(message: String, isValid: Boolean) = copy(
        email = FieldValidation(message, isValid)
    )
    
    fun updatePassword(message: String, isValid: Boolean) = copy(
        password = FieldValidation(message, isValid)
    )
    
    fun updateSchool(message: String, isValid: Boolean) = copy(
        school = FieldValidation(message, isValid)
    )
    
    fun updateName(message: String, isValid: Boolean) = copy(
        name = FieldValidation(message, isValid)
    )
    
    fun updateMajor(message: String, isValid: Boolean) = copy(
        major = FieldValidation(message, isValid)
    )
    
    fun isAllValid() = email.isValid && password.isValid && 
                       school.isValid && name.isValid && major.isValid
}

class RegisterViewModel : ViewModel() {
    private val registerRepository = RegisterRepository()
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
    
    private val _validationState = MutableLiveData(ValidationState())
    val validationState: LiveData<ValidationState> = _validationState
    
    private val _isRegisterEnabled = MutableLiveData(false)
    val isRegisterEnabled: LiveData<Boolean> = _isRegisterEnabled
    
    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult
    
    private var isEmailDuplicationChecked = false

//    companion object {
//        private const val PASSWORD_MIN_LENGTH = 8
//        private const val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$"
//        private const val ENGLISH_PATTERN = ".*[A-Za-z].*"
//        private const val NUMBER_PATTERN = ".*\\d.*"
//        private const val SPECIAL_CHAR_PATTERN = ".*[@\$!%*#?&].*"
//    }

    fun validateEmailFormat(email: String) {
        when {
            email.isBlank() ->
                updateEmailValidation("이메일을 입력해주세요.", false)
            !emailPattern.matcher(email).matches() -> 
                updateEmailValidation("이메일 형식이 올바르지 않습니다.", false)
            else ->
                updateEmailValidation("이메일 중복검사를 해주세요.", false)
        }
        updateRegisterButtonState()
    }

    fun checkEmailDuplication(email: String) {
        if (email.isBlank()) {
            updateEmailValidation("이메일을 입력해주세요.", false)
            return
        }

        viewModelScope.launch {
            val isAvailable = registerRepository.checkEmailAvailability(email)
            isEmailDuplicationChecked = isAvailable
            
            updateEmailValidation(
                if (isAvailable) "사용 가능한 이메일입니다." 
                else "이미 사용중인 이메일입니다.",
                isAvailable
            )

            updateRegisterButtonState()
        }
    }

    fun validatePassword(password: String) {
        when {
            password.isBlank() ->
                updatePasswordValidation("비밀번호를 입력해주세요.", false)
            password.length < PASSWORD_MIN_LENGTH ->
                updatePasswordValidation("비밀번호는 8자 이상이어야 합니다.", false)
            !password.matches(ENGLISH_PATTERN.toRegex()) ->
                updatePasswordValidation("영문자를 포함해야 합니다.", false)
            !password.matches(NUMBER_PATTERN.toRegex()) ->
                updatePasswordValidation("숫자를 포함해야 합니다.", false)
            !password.matches(SPECIAL_CHAR_PATTERN.toRegex()) ->
                updatePasswordValidation("특수문자를 포함해야 합니다.", false)
            !password.matches(PASSWORD_PATTERN.toRegex()) ->
                updatePasswordValidation("올바르지 않은 비밀번호 형식입니다.", false)
            else ->
                updatePasswordValidation("사용 가능한 비밀번호입니다.", true)
        }
        updateRegisterButtonState()
    }

    fun validateSchool(school: String) {
        when {
            school.isBlank() -> 
                updateSchoolValidation("학교를 입력해주세요.", false)
            !("학교" in school || "school" in school.lowercase()) ->
                updateSchoolValidation("학교 이름을 정확히 입력하세요.", false)
            else ->
                updateSchoolValidation("", true)
        }
        updateRegisterButtonState()
    }

    fun validateName(name: String) {
        when {
            name.isBlank() ->
                updateNameValidation("이름을 입력해주세요.", false)
            else ->
                updateNameValidation("", true)
        }
        updateRegisterButtonState()
    }

    fun validateMajor(major: String) {
        when {
            major.isBlank() ->
                updateMajorValidation("전공을 입력해주세요.", false)
            else ->
                updateMajorValidation("", true)
        }
        updateRegisterButtonState()
    }

    private fun updateEmailValidation(message: String, isValid: Boolean) {
        _validationState.value = _validationState.value?.updateEmail(message, isValid)
    }

    private fun updatePasswordValidation(message: String, isValid: Boolean) {
        _validationState.value = _validationState.value?.updatePassword(message, isValid)
    }

    private fun updateSchoolValidation(message: String, isValid: Boolean) {
        _validationState.value = _validationState.value?.updateSchool(message, isValid)
    }

    private fun updateNameValidation(message: String, isValid: Boolean) {
        _validationState.value = _validationState.value?.updateName(message, isValid)
    }

    private fun updateMajorValidation(message: String, isValid: Boolean) {
        _validationState.value = _validationState.value?.updateMajor(message, isValid)
    }

    private fun updateRegisterButtonState() {
        _validationState.value?.let { state ->
            _isRegisterEnabled.value = state.isAllValid() && isEmailDuplicationChecked
        }
    }

    fun registerUser(email: String, password: String, school: String, name: String, major: String) {
        if (_isRegisterEnabled.value == true) {
            viewModelScope.launch {
                val user = User(
                    email = email.trim(),
                    password = password,
                    school = school.trim(),
                    name = name.trim(),
                    major = major.trim()
                )
                val success = registerRepository.registerUser(user)
                _registerResult.value = success
            }
        }
    }
}