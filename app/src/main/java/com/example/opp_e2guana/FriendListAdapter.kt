package com.example.opp_e2guana

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso



class FriendListAdapter(
    private val friendArray: MutableList<Friend_Data>, // 친구 리스트 (MutableList로 변경)
    private val onItemClick: (Friend_Data) -> Unit // 클릭 리스너
) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    // 친구 데이터 클래스
    data class Friend_Data(
        val user_id: String = "",
        val name: String = "",
        val phone: String = "",
        val profileImageUrl: String = "https://example.com/default_profile_image.jpg"
    )

    // ViewHolder 클래스 정의
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendName: TextView = itemView.findViewById(R.id.textViewFriendName) // 친구 이름
        val friendPhone: TextView = itemView.findViewById(R.id.textViewFriendPhone) // 전화번호
        val friendProfile: ImageView = itemView.findViewById(R.id.imageViewProfile) // 프로필 이미지
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendArray[position] // MutableList에서 데이터 가져오기
        holder.friendName.text = friend.name
        holder.friendPhone.text = friend.phone

        // 프로필 이미지 로딩 (Picasso 사용)
        Picasso.get()
            .load(friend.profileImageUrl)
            .placeholder(R.drawable.ic_profile_icon) // 로딩 중 표시할 기본 이미지
            .error(R.drawable.ic_profile_icon) // 오류 발생 시 기본 이미지
            .into(holder.friendProfile)

        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            onItemClick(friend) // 클릭된 데이터를 콜백으로 전달
        }
    }

    override fun getItemCount(): Int = friendArray.size

    // Firebase에서 친구 목록 가져오기
    fun fetchFriends() {
        val database = FirebaseDatabase.getInstance().reference.child("users")

        database.get().addOnSuccessListener { snapshot ->
            friendArray.clear() // 기존 리스트 초기화
            for (child in snapshot.children) {
                val friend = child.getValue(Friend_Data::class.java)
                if (friend != null) {
                    friendArray.add(friend) // MutableList에 데이터 추가
                }
            }
            notifyDataSetChanged() // RecyclerView 갱신
        }.addOnFailureListener { error ->
            error.printStackTrace()
        }
    }
}
