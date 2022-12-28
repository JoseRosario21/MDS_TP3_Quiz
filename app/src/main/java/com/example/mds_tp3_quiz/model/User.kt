package com.example.mds_tp3_quiz.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val globalPoints: Int,
    val totalMatches: Int,
    val level: Int,
    val currentExp: Int
)
