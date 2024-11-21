package com.example.opp_e2guana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opp_e2guana.adapter.ChatAdapter
import com.example.opp_e2guana.databinding.FragmentChatBinding
import com.example.opp_e2guana.viewmodel.ChatViewModel

class chatFragment : Fragment() {

    // View Binding 사용
    private lateinit var binding: FragmentChatBinding

    // ViewModel 초기화
    private val chatViewModel: ChatViewModel by viewModels()

    // RecyclerView Adapter 초기화 null체크 안 하려고 lateinit
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // FragmentChatBinding을 통해 레이아웃 초기화
        binding = FragmentChatBinding.inflate(inflater, container, false)
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
            adapter = ChatAdapter(messages) // 새로운 메시지 리스트로 어댑터 업데이트
            binding.chatRecyclerView.adapter = adapter
            binding.chatRecyclerView.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤
        }

        // 전송 버튼 클릭 이벤트
        binding.btnSend.setOnClickListener {
            val message = binding.inputMessage.text.toString()
            if (message.isNotBlank()) {
                chatViewModel.sendMessage(message) // ViewModel에 메시지 추가
                binding.inputMessage.text.clear() // 입력창 초기화
            }
        }
    }
}
