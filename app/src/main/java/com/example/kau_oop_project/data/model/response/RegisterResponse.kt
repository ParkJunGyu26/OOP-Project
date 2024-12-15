package com.example.kau_oop_project.data.model.response

import android.util.Log

sealed class RegisterResponse : CommonResponse() {
    data object Success : RegisterResponse()

    sealed class Error : RegisterResponse() {
        abstract fun logError()

        data object DuplicateEmail : Error() {
            override fun logError() {
                Log.e("RegisterError", "중복되는 이메일입니다.")
            }
        }
        class Unknown(private val message: String) : Error() {
            override fun logError() {
                Log.e("RegisterError", "알수 없는 에러입니다: $message")
            }
        }
    }
}