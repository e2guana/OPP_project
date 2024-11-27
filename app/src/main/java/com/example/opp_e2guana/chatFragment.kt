package com.example.opp_e2guana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opp_e2guana.adapter.ChatAdapter
import com.example.opp_e2guana.databinding.FragmentChatBinding
import com.example.opp_e2guana.viewmodel.ChatViewModel
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel

class chatFragment : Fragment() {

    // View Binding 사용
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    // ViewModel 초기화
    private val chatViewModel: ChatViewModel by viewModels()

    // RecyclerView Adapter 초기화
    private lateinit var adapter: ChatAdapter

    // UserDataViewModel 가져오기 (friendlistFragment에서 친구데이터를 가져옴)
    private val userDataViewModel: Userdata_viewmodel by activityViewModels()

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

        // ViewModel의 LiveData 관찰
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.updateMessages(messages) // 어댑터의 데이터 업데이트 메서드 호출
            binding.chatRecyclerView.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤
        }

        // 전송 버튼 클릭 이벤트
        binding.btnSend.setOnClickListener {
            val message = binding.inputMessage.text.toString() // 메시지 입력
            if (message.isNotBlank()) {
                chatViewModel.sendMessage(message) // ViewModel에 메시지 추가
                binding.inputMessage.text.clear() // 전송 후 입력창 초기화
            }
        }

        // **뒤로가기 버튼 클릭 이벤트**
        binding.btnBack.setOnClickListener {
            // FragmentManager를 사용해 현재 Fragment를 스택에서 제거
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // View Binding 메모리 누수 방지

    }
}
