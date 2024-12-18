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

        userDataViewModel.currentUserId.observe(viewLifecycleOwner) { currentUserId ->
            userDataViewModel.selectedFriend.observe(viewLifecycleOwner) { friend ->
                // 채팅방 ID 생성 (현재 로그인 사용자 ID와 친구 ID)
                chatViewModel.setChatRoom(currentUserId, friend.user_id)

                // 채팅방 상단에 친구 이름과 프로필 표시
                binding.profileName.text = friend.name
                Picasso.get().load(friend.profileImageUrl).into(binding.profileImage)
            }


            // RecyclerView 설정
            adapter = ChatAdapter(emptyList(), currentUserId)
            binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.chatRecyclerView.adapter = adapter

            /*
        // 선택된 친구 데이터
        userDataViewModel.selectedFriend.observe(viewLifecycleOwner) { friend ->
            val currentUserId = "user1" // 현재 로그인한 사용자의 ID (실제로는 인증 시스템에서 가져와야 함)
            chatViewModel.setChatRoom(currentUserId, friend.user_id) // senderId와 receiverId 전달

            // 친구 이름 및 프로필 사진 설정
            binding.profileName.text = friend.name
            Picasso.get().load(friend.profileImageUrl).into(binding.profileImage)
        }*/

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
                val messageContent = binding.inputMessage.text.toString() // 메시지 입력
                if (messageContent.isNotBlank()) {
                    userDataViewModel.selectedFriend.value?.let { friend ->
                        chatViewModel.sendMessage(messageContent, currentUserId, friend.user_id)
                        binding.inputMessage.text.clear()
                    }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // View Binding 메모리 누수 방지

    }
}
