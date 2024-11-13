package com.example.kau_oop_project.repository

import androidx.lifecycle.MutableLiveData
import com.example.kau_oop_project.viewmodel.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository {
    val database = Firebase.database
    val userRef = database.getReference("users")

//    fun observeInfo(info: MutableLiveData<User>) {
//        userRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                info.postValue(snapshot.value.toString())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }

    // real time 데이터베이스니깐 바로바로 수정. 기존엔 공백으로 취급
    fun postInfo(Info: User) {
        userRef.child("email").setValue(Info.email)
        userRef.child("password").setValue(Info.password)
        userRef.child("school").setValue(Info.school)
        userRef.child("name").setValue(Info.name)
        userRef.child("major").setValue(Info.major)
    }
}