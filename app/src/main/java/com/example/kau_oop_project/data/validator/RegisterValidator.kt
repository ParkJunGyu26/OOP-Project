package com.example.kau_oop_project.data.validator

import android.util.Patterns
import com.example.kau_oop_project.data.model.validation.ValidationResult
import java.util.regex.Pattern

class RegisterValidator {
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    fun validateEmail(email: String): ValidationResult = when {
        email.isBlank() -> ValidationResult.Invalid("이메일을 입력해주세요.")
        !emailPattern.matcher(email).matches() -> ValidationResult.Invalid("이메일 형식이 올바르지 않습니다.")
        else -> ValidationResult.Valid
    }

    fun validatePassword(password: String): ValidationResult = when {
        password.isBlank() -> ValidationResult.Invalid("비밀번호를 입력해주세요.")
        password.length < PASSWORD_MIN_LENGTH -> ValidationResult.Invalid("비밀번호는 8자 이상이어야 합니다.")
        !password.matches(ENGLISH_PATTERN.toRegex()) -> ValidationResult.Invalid("영문자를 포함해야 합니다.")
        !password.matches(NUMBER_PATTERN.toRegex()) -> ValidationResult.Invalid("숫자를 포함해야 합니다.")
        !password.matches(SPECIAL_CHAR_PATTERN.toRegex()) -> ValidationResult.Invalid("특수문자를 포함해야 합니다.")
        !password.matches(PASSWORD_PATTERN.toRegex()) -> ValidationResult.Invalid("올바르지 않은 비밀번호 형식입니다.")
        else -> ValidationResult.Valid
    }

    fun validateSchool(school: String): ValidationResult = when {
        school.isBlank() -> ValidationResult.Invalid("학교를 입력해주세요.")
        !("학교" in school || "school" in school.lowercase()) -> ValidationResult.Invalid("학교 이름을 정확히 입력하세요.")
        else -> ValidationResult.Valid
    }

    fun validateName(name: String): ValidationResult = when {
        name.isBlank() -> ValidationResult.Invalid("이름을 입력해주세요.")
        else -> ValidationResult.Valid
    }

    fun validateMajor(major: String): ValidationResult = when {
        major.isBlank() -> ValidationResult.Invalid("전공을 입력해주세요.")
        else -> ValidationResult.Valid
    }

    // 상수들을 companion object에 넣은 이유:
    // 1. 클래스와 관련된 상수들을 논리적으로 그룹화
    // 2. 다른 클래스에서도 RegisterValidator.PASSWORD_MIN_LENGTH 처럼 접근 가능
    // 3. 메모리 효율성 (클래스 인스턴스마다 생성되지 않음)
    companion object {
        private const val PASSWORD_MIN_LENGTH = 8
        private const val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$"
        private const val ENGLISH_PATTERN = ".*[A-Za-z].*"
        private const val NUMBER_PATTERN = ".*\\d.*"
        private const val SPECIAL_CHAR_PATTERN = ".*[@\$!%*#?&].*"
    }
}