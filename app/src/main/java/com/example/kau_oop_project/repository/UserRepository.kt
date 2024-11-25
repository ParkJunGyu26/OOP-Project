package com.example.kau_oop_project.repository

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class UserRepository {
    private val database = Firebase.database
    protected val userRef = database.getReference("users")

    protected suspend fun getUserByEmail(email: String): DataSnapshot? {
        return try {
            userRef.orderByChild("email").equalTo(email).get().await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = getUserByEmail(email)
                snapshot?.exists() ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}