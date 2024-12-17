package com.example.kau_oop_project.data.model.response

import android.util.Log

sealed class LoginResponse {
    data class Success(
        val uid: String
    ) : LoginResponse(), CommonResponse.Success

    sealed class Error : LoginResponse(), CommonResponse.Error {
        data object WrongPassword : Error() {
            override fun logError() {
                Log.e("LoginError", "비밀번호가 일치하지 않습니다.")
            }
        }

        data object UserNotFound : Error() {
            override fun logError() {
                Log.e("LoginError", "사용자를 찾을 수 없습니다.")
            }
        }

        data object Unknown : Error() {
            override fun logError() {
                Log.e("LoginError", "알 수 없는 에러")
            }
        }
    }
}