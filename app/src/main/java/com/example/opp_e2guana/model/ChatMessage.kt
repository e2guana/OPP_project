package com.example.opp_e2guana.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long,
    val isSentByMe: Boolean //True:내가 보낸 메세지
)
