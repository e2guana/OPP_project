package com.example.opp_e2guana

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opp_e2guana.databinding.FragmentFriendlistBinding
import androidx.fragment.app.activityViewModels
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel

//import com.bumptech.glide.Glide


class friendlistFragment : Fragment() {

    private var binding: FragmentFriendlistBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendListAdapter

    // UserDataViewModel 가져오기
    private val userDataViewModel: Userdata_viewmodel by activityViewModels()

    // 친구 목록 예시 데이터
    private val dummyfriends = arrayOf(
        FriendListAdapter.Friend_Data("user1", "Alice", "010-1111-2222"),
        FriendListAdapter.Friend_Data("user2", "Bob", "010-2222-3333"),
        FriendListAdapter.Friend_Data("user3", "Child", "010-3333-4444"),
        FriendListAdapter.Friend_Data("user4", "Alice2", "010-4111-2222"),
        FriendListAdapter.Friend_Data("user5", "Bob2", "010-5222-3333"),
        FriendListAdapter.Friend_Data("user6", "Child2", "010-6333-4444"),
        FriendListAdapter.Friend_Data("user7", "Alice3", "010-7111-2222"),
        FriendListAdapter.Friend_Data("user8", "Bob3", "010-8222-3333"),
        FriendListAdapter.Friend_Data("user9", "Child3", "010-9333-4444"),
        FriendListAdapter.Friend_Data("user10", "Alice4", "010-2211-2222"),
        FriendListAdapter.Friend_Data("user11", "Bob4", "010-3322-3333"),
        FriendListAdapter.Friend_Data("user12", "Child4", "010-4433-4444"),
        FriendListAdapter.Friend_Data("user13", "Alice5", "010-5511-2222"),
        FriendListAdapter.Friend_Data("user14", "Bob5", "010-6622-3333"),
        FriendListAdapter.Friend_Data("user15", "Child5", "010-7733-4444")
    )

    // 검색 기능을 위한 변수
    private var filteredFriends = dummyfriends.toList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friendlist, container, false)

        // RecyclerView 초기화 및 설정
        recyclerView = view.findViewById(R.id.recyclerViewFriends)
        updateRecyclerView(dummyfriends.toList())

        // 검색창 기능 추가
        val searchEditText = view.findViewById<EditText>(R.id.searcharea)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase() // 검색어를 소문자로 변환(대소문자 상관X)
                filteredFriends = dummyfriends.filter { friend ->
                    friend.name.lowercase().contains(query)
                }
                updateRecyclerView(filteredFriends)
            }
        })

        return view
    }

    // RecyclerView 업데이트 함수 (처음 화면, 검색할때마다 업데이트되어 이 함수를 불러옴)
    private fun updateRecyclerView(friends: List<FriendListAdapter.Friend_Data>) {
        // 친구 클릭 시 채팅방으로 이동. ViewModel 이용해서 친구데이터도 전달
        adapter = FriendListAdapter(friends.toTypedArray()) { friend ->
            userDataViewModel.selectFriend(friend)
            findNavController().navigate(R.id.action_friendlistFragment_to_chatFragment)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //UserData 데이터를 UI에 바인딩
        binding?.apply {
            FirebaseNickname.text = signinFragment.UserData.nickname ?: "닉네임 없음"
            FirebaseEmail.text = signinFragment.UserData.email ?: "이메일 없음"
            FirebasePhone.text = signinFragment.UserData.phone ?: "연락처 없음"
            FirebasePassword.text = signinFragment.UserData.password ?: "비번 없음"

            //Glide로 프로필 이미지 로드
            Glide.with(this@FriendlistFragment)
                .load(UserData.profileImageUrl) // UserData에 저장된 프로필 이미지 URL
                .placeholder(R.drawable.default_profile) // 기본 이미지
                .error(R.drawable.error_image) // 실패 시 이미지
                .into(profileImageView)
        }
        }
    }*/
}

