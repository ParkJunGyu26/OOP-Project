package com.example.kau_oop_project.data.model.response

sealed interface CommonResponse {
    val success: Boolean
    
    interface Success : CommonResponse {
        override val success: Boolean get() = true
    }
    
    interface Error : CommonResponse {
        override val success: Boolean get() = false
        fun logError()
    }
}