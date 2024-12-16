package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class UserRepository {
    private val database = Firebase.database
    protected val userRef = database.getReference("users")

    // 이메일로 사용자 조회하는 공통 메서드
    protected suspend fun findUserByEmail(email: String): DataSnapshot? {
        return userRef.orderByChild("userEmail").equalTo(email).get().await()
    }

    suspend fun getUserInfo(uid: String): User? {
        return try {
            val snapshot = userRef.child(uid).get().await()
            val email = snapshot.child("userEmail").getValue(String::class.java) ?: ""
            val name = snapshot.child("name").getValue(String::class.java) ?: ""
            val school = snapshot.child("school").getValue(String::class.java) ?: ""
            val major = snapshot.child("major").getValue(String::class.java) ?: ""
            
            User(
                uid = uid,
                userEmail = email,
                name = name,
                school = school,
                major = major
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByUids(uids: List<String>): List<User> {
        return withContext(Dispatchers.IO) {
            try {
                val userList = mutableListOf<User>()

                for (uid in uids) {
                    val snapshot = userRef.child(uid).get().await()
                    snapshot.child("name").getValue(String::class.java)?.let { name ->
                        val profileImageUrl = snapshot.child("profileImageUrl")
                            .getValue(String::class.java)
                            ?: User().profileImageUrl

                        userList.add(
                            User(
                                uid = uid,
                                name = name,
                                profileImageUrl = profileImageUrl
                            )
                        )
                    }
                }
                userList
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}