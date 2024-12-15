package com.example.kau_oop_project.data.model.response

import android.util.Log
import com.example.kau_oop_project.data.model.User

sealed class MypageResponse : CommonResponse() {
    class Success(
        val user: User
    ) : MypageResponse()

    sealed class Error : MypageResponse() {
        abstract fun logError()

        data object UserNotFound : Error() {
            override fun logError() {
                Log.e("MypageError", "사용자 정보를 찾을 수 없습니다.")
            }
        }

        class Unknown(private val message: String) : Error() {
            override fun logError() {
                Log.e("MypageError", "예상치 못한 에러가 발생했습니다: $message")
            }
        }
    }
} 