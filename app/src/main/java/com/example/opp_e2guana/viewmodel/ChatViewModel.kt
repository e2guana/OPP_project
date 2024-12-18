package com.example.opp_e2guana.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val database = FirebaseDatabase.getInstance().reference
    private var chatRoomId: String = "" // 현재 채팅방 ID

    //채팅방 ID 설정 및 메시지 불러오기
    fun setChatRoom(currentUserId: String, friendId: String) {
        chatRoomId = generateChatRoomId(currentUserId, friendId)
        fetchMessages()
    }

    // 두 ID를 정렬해 고유 채팅방 ID 생성
    private fun generateChatRoomId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }


    //Firebase에서 채팅 메시지 실시간 가져오기
    private fun fetchMessages() {
        if (chatRoomId.isEmpty()) return

        val chatRef = database.child("chats").child(chatRoomId)

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (child in snapshot.children) {
                    val message = child.getValue(ChatMessage::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                _messages.value = messages.sortedBy { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatViewModel", "Failed to fetch messages", error.toException())
            }
        })
    }

    //Firebase에 메시지 전송
    fun sendMessage(message: String, senderId: String, receiverId: String) {
        if (chatRoomId.isEmpty() || message.isBlank()) return

        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = senderId,
            receiverId = receiverId,
            message = message.trim(),
            timestamp = System.currentTimeMillis(),
            isSentByMe = true
        )

        database.child("chats").child(chatRoomId).push().setValue(newMessage)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Failed to send message", e)
            }
    }
}
