package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.User
import com.example.kau_oop_project.data.model.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterRepository : UserRepository() {
    suspend fun checkEmailAvailability(email: String): RegisterResponse {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = findUserByEmail(email)
                snapshot?.let {
                    if (it.exists()) RegisterResponse.Error.DuplicateEmail
                    else RegisterResponse.Success
                } ?: RegisterResponse.Error.Unknown
            } catch (e: Exception) {
                RegisterResponse.Error.Unknown
            }
        }
    }

    suspend fun registerUser(user: User): RegisterResponse {
        return withContext(Dispatchers.IO) {
            try {
                val newUserRef = userRef.push()
                newUserRef.setValue(user).await()
                RegisterResponse.Success
            } catch (e: Exception) {
                RegisterResponse.Error.Unknown
            }
        }
    }
}