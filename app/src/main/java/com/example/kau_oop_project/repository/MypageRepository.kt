package com.example.kau_oop_project.repository

import android.net.Uri
import com.example.kau_oop_project.data.model.User
import com.example.kau_oop_project.data.model.response.MyInfo
import com.example.kau_oop_project.data.model.response.MypageResponse
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class MypageRepository : UserRepository() {
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    // 마이페이지 유저 정보
    suspend fun getDetailUserInfo(uid: String): MypageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = userRef.child(uid).get().await()

                snapshot?.let { snap ->
                    val email = snap.child("userEmail").getValue(String::class.java)
                    val name = snap.child("name").getValue(String::class.java)
                    val school = snap.child("school").getValue(String::class.java)
                    val major = snap.child("major").getValue(String::class.java)
                    val profileImageUrl = snap.child("profileImageUrl").getValue(String::class.java)

                    // 모든 필드가 null이 아닌지 확인
                    if (email == null || name == null || school == null ||
                        major == null || profileImageUrl == null) {
                        return@withContext MypageResponse.Error.UserNotFound
                    }

                    MypageResponse.Success(
                        MyInfo(
                            uid = uid,
                            userEmail = email,
                            name = name,
                            school = school,
                            major = major,
                            profileImage = profileImageUrl
                        )
                    )
                } ?: MypageResponse.Error.UserNotFound

            } catch (e: Exception) {
                MypageResponse.Error.Unknown
            }
        }
    }

    suspend fun updateUser(user: MyInfo): MypageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "name" to user.name,
                    "userEmail" to user.userEmail,
                    "school" to user.school,
                    "major" to user.major,
                    "profileImageUrl" to user.profileImage  // 이미지 URL도 함께 업데이트
                )
                
                userRef.child(user.uid).updateChildren(updates).await()
                MypageResponse.Success(user)
            } catch (e: Exception) {
                MypageResponse.Error.UpdateFailed
            }
        }
    }

    suspend fun uploadImageToStorage(imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val filename = "profile_images/${UUID.randomUUID()}"
            val imageRef = storageRef.child(filename)
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        }
    }
}