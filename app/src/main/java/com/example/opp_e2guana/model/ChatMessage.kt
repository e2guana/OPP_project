package com.example.opp_e2guana.model

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isSentByMe: Boolean = false // True: 내가 보낸 메시지
)
//기본값이 설정되어 있지 않으면 파이어베이스에서 접근할 때 앱이 강제종료됨
