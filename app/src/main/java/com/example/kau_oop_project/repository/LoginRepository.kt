package com.example.kau_oop_project.repository

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginRepository {
    val database = Firebase.database
    val userRef = database.getReference("user")

    suspend fun checkEmail(inputEmail: String?): Boolean {
        return withContext(Dispatchers.IO) {
            val snapshot = inputEmail?.let {
                userRef.child("email").get().await()
            }
            snapshot?.exists() ?: false
        }
    }

    suspend fun getPassword(inputEmail: String?, inputPassword: String?): Boolean {
        return withContext(Dispatchers.IO) {
            val snapshot = inputEmail?.let {
                userRef.child("password").get().await()
            }
            snapshot?.value?.toString() == inputPassword
        }
    }
}