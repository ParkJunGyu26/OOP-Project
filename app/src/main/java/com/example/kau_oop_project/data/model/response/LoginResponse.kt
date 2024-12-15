package com.example.kau_oop_project.data.model.response

import android.util.Log

sealed class LoginResponse : CommonResponse() {
    class Success(
        val uid: String,
        val name: String,
        val userEmail: String,
        val school: String,
        val major: String,
        val profileImageUrl: String
    ) : LoginResponse()

    sealed class Error : LoginResponse() {
        abstract fun logError()

        data object InvalidPassword : Error() {
            override fun logError() {
                Log.e("LoginError", "패스워드가 틀렸습니다.")
            }
        }

        data object UserNotFound : Error() {
            override fun logError() {
                Log.e("LoginError", "사용자 정보를 찾을 수 없습니다.")
            }
        }

        class Unknown(private val message: String) : Error() {
            override fun logError() {
                Log.e("LoginError", "예상치못한 에러가 발생했습니다: $message")
            }
        }
    }
}