package com.example.kau_oop_project.repository

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginRepository {
    val database = Firebase.database
    val userRef = database.getReference("users")

    suspend fun checkEmail(inputEmail: String?): Boolean {
        return withContext(Dispatchers.IO) {
            inputEmail?.let { email ->
                val snapshot = userRef.get().await()

                var exists = false
                snapshot.children.forEach { userSnapshot ->
                    val userEmail = userSnapshot.child("email").value as? String
                    if (userEmail == email) {
                        exists = true
                        return@forEach
                    }
                }
                exists
            } ?: false
        }
    }

    suspend fun getPassword(inputEmail: String?, inputPassword: String?): Boolean {
        return withContext(Dispatchers.IO) {
            inputEmail?.let { email ->
                val snapshot = userRef.get().await()

                var isValid = false
                snapshot.children.forEach { userSnapshot ->
                    val userEmail = userSnapshot.child("email").value as? String
                    val userPassword = userSnapshot.child("password").value as? String
                    if (userEmail == email && userPassword == inputPassword) {
                        isValid = true
                        return@forEach
                    }
                }
                isValid
            } ?: false
        }
    }
}