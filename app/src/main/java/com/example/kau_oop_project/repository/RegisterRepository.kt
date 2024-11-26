package com.example.kau_oop_project.repository

import android.util.Log
import com.example.kau_oop_project.ui.user.viewmodel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterRepository : UserRepository() {
    suspend fun checkEmailAvailability(email: String): Boolean {
        return !checkEmailExists(email)
    }

    suspend fun registerUser(user: User): Boolean = withContext(Dispatchers.IO) {
        val newUserRef = userRef.push()
        newUserRef.setValue(user).await()
        true
    }
}