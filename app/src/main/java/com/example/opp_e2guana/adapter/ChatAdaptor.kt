package com.example.opp_e2guana.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opp_e2guana.R
import com.example.opp_e2guana.model.ChatMessage // ChatMessage 경로 추가

class ChatAdapter(private var messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
        //RecyclerView가 화면에 표시할 레이아웃을 생성
        fun updateMessages(newMessages: List<ChatMessage>) {
            messages = newMessages
            notifyDataSetChanged() // 데이터 갱신
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate( //xml 객체로 변경
            if (viewType == 0) R.layout.chat_sent else R.layout.chat_receive,
            parent,
            false
        )
        return ChatViewHolder(view)
    }
    //RecyclerView.Adapter를 상속받은 클래스에서 반드시 구현해야 하는 메서드
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) { //position은 아이템 데이터 위치
        val message = messages[position]
        holder.bind(message) //아이템 레이아웃 관리 & .bind() message를 화면에 표시
    }
    //몇 개가 있는지
    override fun getItemCount(): Int = messages.size
    //누가 보냈는지
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByMe) 0 else 1
    }
    //RecycleView의 각 View 관리
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: ChatMessage) {
            messageText.text = message.message
        }
    }
}
