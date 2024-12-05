package com.example.opp_e2guana.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.model.ChatMessage


class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val Messages = mutableListOf<ChatMessage>()

    init {
        _messages.value = Messages
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
        Messages.add(newMessage)
        _messages.value = Messages // LiveData 업데이트
    }
}
