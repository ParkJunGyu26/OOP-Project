package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository : UserRepository() {
    suspend fun verifyLogin(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = findUserByEmail(email)
                snapshot?.children?.firstOrNull()?.let { userSnapshot ->
                    val storedPassword = userSnapshot.child("password").getValue(String::class.java)

                    if (storedPassword == password) {
                        LoginResponse.Success(
                            uid = userSnapshot.key ?: "",
                            name = userSnapshot.child("name").getValue(String::class.java) ?: "",
                            userEmail = email,
                            school = userSnapshot.child("school").getValue(String::class.java) ?: "",
                            major = userSnapshot.child("major").getValue(String::class.java) ?: "",
                            profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String::class.java) ?: ""
                        )
                    } else {
                        LoginResponse.Error.InvalidPassword
                    }
                } ?: LoginResponse.Error.UserNotFound
            } catch (e: Exception) {
                LoginResponse.Error.Unknown(e.message ?: "Unknown")
            }
        }
    }
}