package com.example.kau_oop_project.repository

import com.google.firebase.database.FirebaseDatabase

class ChatDetailRepository {

    private val database = FirebaseDatabase.getInstance()
    private val postsRef = database.getReference("chats")
    // 데이터 통신 예정

}