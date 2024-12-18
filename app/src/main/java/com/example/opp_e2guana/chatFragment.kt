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
                // Navigation을 사용하여 friendlistFragment로 이동
                findNavController().navigate(R.id.action_chatFragment_to_friendlistFragment2)
            }

            // 키보드 이벤트
            binding.root.viewTreeObserver.addOnGlobalLayoutListener {
                if (_binding != null) { // View가 살아 있는지 체크!!!!!없으면 앱 튕김
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 키보드 이벤트 리스너 제거
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener {}
        _binding = null // View Binding 메모리 누수 방지

    }
}
