package com.example.opp_e2guana

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opp_e2guana.databinding.FragmentFriendlistBinding
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel


class friendlistFragment : Fragment() {

    // ViewBinding 객체, 프래그먼트의 뷰를 참조
    private var binding: FragmentFriendlistBinding? = null
    // RecyclerView 및 어댑터 초기화
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendListAdapter

    // UserDataViewModel 가져오기
    private val userDataViewModel: Userdata_viewmodel by activityViewModels()

    // 검색 기능을 위한 필터링된 친구 리스트 변수
    private var filteredFriends: List<FriendListAdapter.Friend_Data> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceㅁㄴState: Bundle?
    ): View? {
        // 바인딩 초기화
        binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화 (뷰가 생성된 이후에 설정해야 함)
        recyclerView = binding?.recyclerViewFriends ?: throw IllegalStateException("RecyclerView not found")
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Firebase에서 친구 데이터를 가져와 RecyclerView 업데이트
        userDataViewModel.fetchFriendsData { friendsList ->
            filteredFriends = friendsList
            updateRecyclerView(filteredFriends)
        }

        // 검색창 기능 추가
        binding?.searcharea?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase() // 검색어를 소문자로 변환
                filteredFriends = userDataViewModel.show_friendList.filter { friend ->
                    friend.name.lowercase().contains(query) // 이름에 검색어가 포함된 친구만 필터링
                }
                updateRecyclerView(filteredFriends)  // 필터링된 데이터로 RecyclerView 갱신
            }
        })
    }

    // RecyclerView를 업데이트하는 함수
    private fun updateRecyclerView(friends: List<FriendListAdapter.Friend_Data>) {
        // 어댑터 설정 및 클릭 리스너 연결
        adapter = FriendListAdapter(friends.toTypedArray()) { friend ->
            // 친구를 클릭하면 ViewModel에 선택한 친구를 저장하고 채팅 화면으로 이동
            userDataViewModel.selectFriend(friend)
            findNavController().navigate(R.id.action_friendlistFragment_to_chatFragment)
        }
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // 뷰가 파괴될 때 바인딩 해제 (메모리 누수 방지)
    }
}