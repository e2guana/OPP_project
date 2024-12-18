package com.example.opp_e2guana.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opp_e2guana.R
import com.example.opp_e2guana.model.ChatMessage // ChatMessage 경로 추가

class ChatAdapter(private var messages: List<ChatMessage>,
                  private val currentUserId: String // 현재 로그인한 사용자 ID 추가
    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Types
    private companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2

        /*RecyclerView가 화면에 표시할 레이아웃을 생성
        fun updateMessages(newMessages: List<ChatMessage>) {
            messages = newMessages
            notifyDataSetChanged() // 데이터 갱신   */
        }

    // 보낸 메세지 ViewHolder
    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message_text)
    }

    // 받은 메세지 ViewHolder
    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message_text)
    }

    // 메시지 타입 구분: 보낸 사람과 로그인한 ID 비교 (수정된 부분)
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_receive, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    //RecyclerView.Adapter를 상속받은 클래스에서 반드시 구현해야 하는 메서드
    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int) { //position은 아이템 데이터 위치
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.messageText.text = message.message
        } else if (holder is ReceivedMessageViewHolder) {
            holder.messageText.text = message.message
        }
    }

    /*누가 보냈는지
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByMe) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }*/

    //몇 개가 있는지
    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    /*
    //RecycleView의 각 View 관리
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: ChatMessage) {
            messageText.text = message.message
        }
    }*/
}
