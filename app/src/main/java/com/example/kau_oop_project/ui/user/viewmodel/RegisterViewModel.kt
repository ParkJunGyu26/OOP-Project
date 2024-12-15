package com.example.kau_oop_project.ui.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.user.User
import com.example.kau_oop_project.data.model.response.RegisterResponse
import com.example.kau_oop_project.repository.RegisterRepository
import com.example.kau_oop_project.data.validator.RegisterValidator
import com.example.kau_oop_project.data.model.validation.ValidationResult
import kotlinx.coroutines.launch

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
    private val validator = RegisterValidator()
    
    private val _validationState = MutableLiveData(ValidationState())
    val validationState: LiveData<ValidationState> = _validationState
    
    private val _isRegisterEnabled = MutableLiveData(false)
    val isRegisterEnabled: LiveData<Boolean> = _isRegisterEnabled
    
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult
    
    private var isEmailDuplicationChecked = false

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

    fun registerUser(email: String, password: String, school: String, name: String, major: String) {
        if (_isRegisterEnabled.value == true) {
            viewModelScope.launch {
                val user = User(
                    userEmail = email.trim(),
                    password = password,
                    school = school.trim(),
                    name = name.trim(),
                    major = major.trim()
                )
                _registerResult.value = registerRepository.registerUser(user)
            }
        }
    }

    fun validateEmailFormat(email: String) {
        when (val result = validator.validateEmail(email)) {
            is ValidationResult.Invalid -> {
                updateEmailValidation(result.message, false)
                isEmailDuplicationChecked = false  // 이메일 형식이 바뀌면 중복 검사 초기화
            }
            is ValidationResult.Valid -> {
                updateEmailValidation("이메일 중복검사를 해주세요.", true)
                isEmailDuplicationChecked = false  // 이메일이 바뀌면 중복 검사 상태 초기화
            }
        }
        updateRegisterButtonState()
    }

    fun checkEmailDuplication(email: String) {
        when (val validationResult = validator.validateEmail(email)) {
            is ValidationResult.Invalid -> {
                updateEmailValidation(validationResult.message, false)
                return
            }
            is ValidationResult.Valid -> {
                viewModelScope.launch {
                    when (val response = registerRepository.checkEmailAvailability(email)) {
                        is RegisterResponse.Success -> {
                            isEmailDuplicationChecked = true
                            updateEmailValidation("사용 가능한 이메일입니다.", true)
                        }
                        is RegisterResponse.Error.DuplicateEmail -> {
                            isEmailDuplicationChecked = false
                            updateEmailValidation("이미 사용중인 이메일입니다.", false)
                            response.logError()
                        }
                        is RegisterResponse.Error.Unknown -> {
                            isEmailDuplicationChecked = false
                            updateEmailValidation("이메일 중복 확인 중 오류가 발생했습니다.", false)
                            response.logError()
                        }
                    }
                }
            }
        }
        updateRegisterButtonState()
    }

    fun validatePassword(password: String) {
        when (val result = validator.validatePassword(password)) {
            is ValidationResult.Invalid -> updatePasswordValidation(result.message, false)
            is ValidationResult.Valid -> updatePasswordValidation("사용 가능한 비밀번호입니다.", true)
        }
        updateRegisterButtonState()
    }

    fun validateSchool(school: String) {
        when (val result = validator.validateSchool(school)) {
            is ValidationResult.Invalid -> updateSchoolValidation(result.message, false)
            is ValidationResult.Valid -> updateSchoolValidation("", true)
        }
        updateRegisterButtonState()
    }

    fun validateName(name: String) {
        when (val result = validator.validateName(name)) {
            is ValidationResult.Invalid -> updateNameValidation(result.message, false)
            is ValidationResult.Valid -> updateNameValidation("", true)
        }
        updateRegisterButtonState()
    }

    fun validateMajor(major: String) {
        when (val result = validator.validateMajor(major)) {
            is ValidationResult.Invalid -> updateMajorValidation(result.message, false)
            is ValidationResult.Valid -> updateMajorValidation("", true)
        }
        updateRegisterButtonState()
    }

    private fun updateRegisterButtonState() {
        _validationState.value?.let { state ->
            _isRegisterEnabled.value = state.isAllValid() && isEmailDuplicationChecked
        }
    }
}