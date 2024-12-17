package com.example.kau_oop_project.data.model

data class User(
    val userEmail: String = "",
    val password: String = "",
    val name: String = "",
    val school: String = "",
    val major: String = "",
    val profileImageUrl: String = "https://firebasestorage.googleapis.com/v0/b/kau-oop-project.appspot.com/o/default_image.png?alt=media"
)