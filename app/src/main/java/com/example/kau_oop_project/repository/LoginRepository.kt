package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository : UserRepository() {
    suspend fun verifyLogin(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                findUserByEmail(email)?.let { snapshot ->
                    if (!snapshot.exists()) {
                        return@withContext LoginResponse.Error.UserNotFound
                    }

                    snapshot.children.firstOrNull()?.let { userSnapshot ->
                        val uid = userSnapshot.key ?: 
                            return@withContext LoginResponse.Error.Unknown

                        userSnapshot.child("password").getValue(String::class.java)?.let { storedPassword ->
                            return@withContext if (storedPassword == password) {
                                LoginResponse.Success(uid)
                            } else {
                                LoginResponse.Error.WrongPassword
                            }
                        } ?: LoginResponse.Error.Unknown
                    } ?: LoginResponse.Error.Unknown
                } ?: LoginResponse.Error.Unknown

            } catch (e: Exception) {
                LoginResponse.Error.Unknown
            }
        }
    }
}