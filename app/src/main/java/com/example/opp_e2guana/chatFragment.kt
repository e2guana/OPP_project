package com.example.opp_e2guana

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opp_e2guana.adapter.ChatAdapter
import com.example.opp_e2guana.databinding.FragmentChatBinding
import com.example.opp_e2guana.viewmodel.ChatViewModel
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.squareup.picasso.Picasso


class chatFragment : Fragment() {

    // View Binding 사용
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    // ViewModel 초기화
    private val chatViewModel: ChatViewModel by viewModels()

    // UserDataViewModel 가져오기 (friendlistFragment에서 친구데이터를 가져옴)
    private val userDataViewModel: Userdata_viewmodel by activityViewModels()

    // RecyclerView Adapter 초기화
    private lateinit var adapter: ChatAdapter
    private lateinit var chatRoomId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // FragmentChatBinding을 통해 레이아웃 초기화
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        adapter = ChatAdapter(emptyList())
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

        // 선택된 친구 데이터
        userDataViewModel.selectedFriend.observe(viewLifecycleOwner) { friend ->
            chatRoomId = generateChatRoomId("user1", friend.user_id)
            chatViewModel.setChatRoomId(chatRoomId)

            // 친구 이름 및 프로필 사진 설정
            binding.profileName.text = friend.name
            Picasso.get().load(friend.profileImageUrl).into(binding.profileImage)
        }

        /* 선택된 친구 정보 가져오기
        userDataViewModel.selectedFriend.observe(viewLifecycleOwner) { friend ->
            binding.profileName.text = friend.name
            // Picasso 또는 Glide를 사용해 프로필 이미지를 설정
            Picasso.get().load(friend.profileImageUrl).into(binding.profileImage)
        } */


        // ViewModel 메세지 불러오기
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.updateMessages(messages) // 어댑터의 데이터 업데이트 메서드 호출
            binding.chatRecyclerView.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤
        }

        // 프로필 버튼 눌렀을 때 친구 위치 확인하기 - j
        binding.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_show_locationFragment)
        }

        // 전송 버튼 클릭 이벤트
        binding.btnSend.setOnClickListener {
            val message = binding.inputMessage.text.toString() // 메시지 입력
            if (message.isNotBlank()) {
                chatViewModel.sendMessage(message, "user1", userDataViewModel.selectedFriend.value!!.user_id) // ViewModel에 메시지 추가
                binding.inputMessage.text.clear() // 전송 후 입력창 초기화
            }
        }

        //뒤로가기 이벤트
        binding.btnBack.setOnClickListener {
            // FragmentManager를 사용해 현재 Fragment를 스택에서 제거
            parentFragmentManager.popBackStack()
        }

        // 키보드 이벤트
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // 키보드가 열렸을 때
                binding.bottomBar.translationY = -keypadHeight.toFloat()
            } else {
                // 키보드가 닫혔을 때
                binding.bottomBar.translationY = 0f
            }
        }
    }

    private fun generateChatRoomId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // View Binding 메모리 누수 방지

    }
}
