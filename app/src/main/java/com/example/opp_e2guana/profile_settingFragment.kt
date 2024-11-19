package com.example.opp_e2guana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.databinding.FragmentProfileSettingBinding

/*
  - 이 프레그먼트는 사용자가 계정 정보를 수정하고 싶을 때 수정된 내용을 받아서 DB에 올리고 확인까지 해주는 페이지
  - 아이디는 고정
  - 이메일 주소, 비밀번호, 닉네임, 프로필 사진 4가지가 변경될 수 있음
 */


class profile_settingFragment : Fragment() {
    var binding: FragmentProfileSettingBinding? = null
    //우선 사용자 정보에 대한 번들이 넘어와야됨 이건 모두와 상의해서 어떤 식으로 번들을 주고 받을건지 정해야함!

    override fun onCreateView(                                                  //1차 바인딩
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //여기에다가 id, 닉네임, 비번을 입력 받는 내용을 추가해야됨

        binding?.resettingButton?.setOnClickListener {                  //변경하기 버튼, 이곳을 누르면 수정할건지 되물어보는 팝업창을 띄워야함!

        }
    }
}