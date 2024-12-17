package com.example.kau_oop_project.data.model.response

import android.util.Log

sealed class RegisterResponse {
    data object Success : RegisterResponse(), CommonResponse.Success

    sealed class Error : RegisterResponse(), CommonResponse.Error {
        data object DuplicateEmail : Error() {
            override fun logError() {
                Log.e("RegisterError", "중복되는 이메일입니다.")
            }
        }

        data object Unknown : Error() {
            override fun logError() {
                Log.e("RegisterError", "알 수 없는 에러.")
            }
        }
    }
}