package com.example.myapplication.domain.model

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val userName: String,
    val password: String,
    val gender: Gender? = null,
    val birthDate: String = "",
    val score: Int = 0,
    val profilePictureUrl: String = "",
    val role: UserRole = UserRole.USER
)
