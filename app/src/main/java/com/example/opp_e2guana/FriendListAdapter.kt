package com.example.opp_e2guana

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendListAdapter(
    private val friendArray: Array<Friend_Data>,
    private val onItemClick: (Friend_Data) -> Unit
    ): RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    // 친구 데이터 클래스
    data class Friend_Data(
        val user_id: String,
        val name: String,
        val phone: String
    )

    // ViewHolder 클래스 정의
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendName: TextView = itemView.findViewById(R.id.textViewFriendName)
        val friendContact: TextView = itemView.findViewById(R.id.textViewFriendContact)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendArray[position] // Array에서 데이터 가져오기
        holder.friendName.text = friend.name
        holder.friendContact.text = friend.phone

        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            onItemClick(friend) // 클릭된 데이터를 콜백으로 전달
        }
    }

    override fun getItemCount(): Int = friendArray.size
}
