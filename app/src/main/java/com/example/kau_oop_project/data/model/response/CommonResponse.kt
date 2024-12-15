package com.example.kau_oop_project.data.model.response

sealed class CommonResponse {
    open class Success<T>(val data: T) : CommonResponse()
    sealed class Error : CommonResponse() {
        abstract fun logError()
    }
}