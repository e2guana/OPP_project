package com.example.opp_e2guana.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.model.ChatMessage


class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val dummyMessages = mutableListOf<ChatMessage>(
        ChatMessage("1", "user1", "user2", "안녕하세요!", System.currentTimeMillis(), true),
        ChatMessage("2", "user2", "user1", "안녕하세요! 반갑습니다.", System.currentTimeMillis(), false)
    )

    init {
        _messages.value = dummyMessages
    }

    fun sendMessage(message: String) {
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(), //message 분류를 위한 id
            senderId = "user1", // 나의 ID
            receiverId = "user2", // 상대방 ID
            message = message,
            timestamp = System.currentTimeMillis(),
            isSentByMe = true
        )
        dummyMessages.add(newMessage)
        _messages.value = dummyMessages // LiveData 업데이트
    }
}
