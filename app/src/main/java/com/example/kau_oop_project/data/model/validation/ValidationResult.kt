package com.example.kau_oop_project.data.model.validation

sealed class ValidationResult {
    data class Invalid(val message: String) : ValidationResult()
    data object Valid : ValidationResult()
} 