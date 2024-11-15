package com.example.opp_e2guana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.databinding.FragmentShowLocationBinding

/*
    이 프레그먼트는 우선 google맵에 대한 api를 받아서 배경에 띄워야함!
    이 화면에 들어왔을 때 보이는 위치는 친구 위치를 받아서 보여줘야됨
    친구의 이름과 이메일 주소를 띄워줘야 함. 이 부분은 친구 리스트에서 들어왔을 때 이름과 이메일 정보를 받아오는게 좋을듯 or 파이어베이스에서 +위치 정보까지 한 번에 받기?

    만약 맵에서 친구 위치를 어떻게 상세하게 보여줄건지?
        1. api(맵의) 배경은 고정한 채로 구글 맵을 움직일 수 있게 한다.
        2. api(맵의) 배경을 눌렀을 때 새 창이 열리며 전체화면으로 지도를 볼 수 있게한다.

    프로필을 눌렀을 때 친구의 프로필 사진을 띄워줄건지 아니면 프로필 이미지 또한 고정할건지 생각해야됨 <- 현재로썬 일단 고정

 */


class show_locationFragment : Fragment() {

    var binding: FragmentShowLocationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowLocationBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gochatButton?.setOnClickListener() {                                               //채팅 아이콘 버튼, 누르면 채팅방으로 이동
            findNavController().navigate(R.id.action_show_locationFragment_to_chatFragment)
        }

    }
}