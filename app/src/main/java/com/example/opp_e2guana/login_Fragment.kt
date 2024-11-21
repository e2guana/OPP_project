package com.example.opp_e2guana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel

class login_Fragment : Fragment() {
    val viewModel: Userdata_viewmodel by activityViewModels()       //사용자 정보는 나중에 firebase에서 받아와야 합니다!
    var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼 클릭 시 이메일 형식 검증
        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()

            // 이메일 형식 검증
            if (!isValidEmail(email)) {
                // 이메일 형식이 올바르지 않으면 오류 메시지 보이기
                binding?.errorMessageBox?.visibility = View.VISIBLE
            } else {
                // 이메일 형식이 올바르면 오류 메시지 숨기기
                binding?.errorMessageBox?.visibility = View.GONE
                // 화면 전환
                findNavController().navigate(R.id.action_login_Fragment_to_friendlistFragment)
            }

            viewModel.set_email(email)
            viewModel.set_password(password)
        }

        // 회원가입 버튼 클릭 시
        binding?.signupMoveButton?.setOnClickListener {
            findNavController().navigate(R.id.action_login_Fragment_to_signinFragment)
        }

        // 닫기 버튼 클릭 시 오류 메시지 숨기기
        binding?.closeErrorButton?.setOnClickListener {
            binding?.errorMessageBox?.visibility = View.GONE
        }
    }


    // 이메일 형식 검증 함수
    private fun isValidEmail(email: String): Boolean {                                              // <- 이메일 형식 오류 체크도 뷰모델로 가는게 좋지 않을까요?
    // 이메일 형식 검증을 위한 정규식
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(Regex(emailPattern))
    }
}
