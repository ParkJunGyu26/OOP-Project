package com.example.kau_oop_project.data.model.response

import android.util.Log

data class UserInfo(
    val name: String,
    val userEmail: String = "",
    val profileImage: String = ""
)

sealed class UserResponse {
    data class Success(
        val usersInfo: List<UserInfo>
    ) : UserResponse(), CommonResponse.Success

    sealed class Error : UserResponse(), CommonResponse.Error {
        data object UserNotFound : Error() {
            override fun logError() {
                Log.e("UserError", "사용자를 찾을 수 없습니다.")
            }
        }

        data object Unknown : Error() {
            override fun logError() {
                Log.e("UserError", "알 수 없는 에러")
            }
        }
    }
}