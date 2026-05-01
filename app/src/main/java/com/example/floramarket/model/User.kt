package com.example.floramarket.model

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isRegistered: Boolean = false
)