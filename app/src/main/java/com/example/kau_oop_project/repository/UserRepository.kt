package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.User
import com.example.kau_oop_project.data.model.response.UserInfo
import com.example.kau_oop_project.data.model.response.UserResponse
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
    suspend fun findUserByEmail(email: String): DataSnapshot? {
        return withContext(Dispatchers.IO) {
            userRef.orderByChild("userEmail").equalTo(email).get().await()
        }
    }

    // 외부 도메인 유저 정보 조회(게시글, 채팅 사용)
    suspend fun getUsersInfo(uids: List<String>): UserResponse {
        return withContext(Dispatchers.IO) {
            try {
                val userInfoList = mutableListOf<UserInfo>()
                
                for (uid in uids) {
                    val snapshot = userRef.child(uid).get().await()
                    val name = snapshot.child("name").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    
                    if (name == null || email == null) {
                        return@withContext UserResponse.Error.Unknown
                    }
                    
                    val profileImageUrl = snapshot.child("profileImageUrl")
                        .getValue(String::class.java) 
                        ?: User().profileImageUrl

                    userInfoList.add(
                        UserInfo(
                            name = name,
                            userEmail = email,
                            profileImage = profileImageUrl
                        )
                    )
                }

                if (userInfoList.isEmpty()) {
                    UserResponse.Error.UserNotFound
                } else {
                    UserResponse.Success(userInfoList)
                }
                
            } catch (e: Exception) {
                UserResponse.Error.Unknown
            }
        }
    }
}