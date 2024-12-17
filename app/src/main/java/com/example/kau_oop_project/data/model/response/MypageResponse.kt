package com.example.kau_oop_project.data.model.response

import android.util.Log
import com.example.kau_oop_project.data.model.User

data class MyInfo (
    val uid: String,
    var name: String,
    var userEmail: String,
    var school: String,
    var major: String,
    var profileImage: String
)

sealed class MypageResponse {
    data class Success(
        val userInfo: MyInfo
    ) : MypageResponse(), CommonResponse.Success

    sealed class Error : MypageResponse(), CommonResponse.Error {
        data object UserNotFound : Error() {
            override fun logError() {
                Log.e("MypageError", "사용자를 찾을 수 없습니다.")
            }
        }

        data object ImageUploadFailed : Error() {
            override fun logError() {
                Log.e("MypageError", "이미지 업로드 실패.")
            }
        }

        data object UpdateFailed : Error() {
            override fun logError() {
                Log.e("MypageError", "정보 업데이트 실패")
            }
        }

        data object Unknown: Error() {
            override fun logError() {
                Log.e("MypageError", "알 수 없는 에러")
            }
        }
    }
} 