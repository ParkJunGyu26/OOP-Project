package com.example.kau_oop_project.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository : UserRepository() {
    suspend fun verifyLogin(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (!checkEmailExists(email)) return@withContext false
            
            val snapshot = getUserByEmail(email)
            snapshot?.children?.firstOrNull()?.let { userSnapshot ->
                userSnapshot.child("password").value as? String == password
            } ?: false
        }
    }
}