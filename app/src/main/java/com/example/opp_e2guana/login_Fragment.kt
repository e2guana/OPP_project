package com.example.opp_e2guana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class login_Fragment : Fragment() {
    val viewModel: Userdata_viewmodel by activityViewModels()       //사용자 정보는 나중에 firebase에서 받아와야 합니다!
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // 로그인 버튼 클릭 시 이메일 형식 검증
            loginButton.setOnClickListener {
                val email = binding?.emailEditText?.text.toString()
                val password = binding?.passwordEditText?.text.toString()

                // 이메일 형식 검증
                if (!viewModel.isValidEmail(email)) {
                    // 이메일 형식이 올바르지 않으면 오류 메시지 보이기
                    errorMessageBox.visibility = View.VISIBLE
                    errorMessage.text = "올바른 이메일 형식이 아닙니다."
                }
                else if (password.isEmpty()) {
                    // 비밀번호가 비어 있으면 오류 메시지 보이기
                    errorMessageBox.visibility = View.VISIBLE
                    errorMessage.text = "비밀번호를 입력해주세요."
                }
                else {
                    // 이메일 형식이 올바르면 오류 메시지 숨기기
                    errorMessageBox.visibility = View.GONE

                    // ViewModel을 통해 로그인 처리
                    viewModel.loginUser(email, password) { success, errormessage ->
                        if (success) {
                            // Firebase에 있는 사용자 정보(email, password)가 동일하다면 친구목록으로 화면전환
                            findNavController().navigate(R.id.action_login_Fragment_to_friendlistFragment)
                        } else {
                            // 사용자 정보와 일치하지 않는다면 (즉, 이메일과 비밀번호가 틀리다면) 오류메시지 출력
                            errorMessageBox.visibility = View.VISIBLE
                            if (errormessage is FirebaseNetworkException) {
                                errorMessage.text = "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            } else {
                                errorMessage.text = "이메일 혹은 비밀번호가 틀립니다."
                            }
                        }
                    }
                }
            }

            // 회원가입 버튼 클릭 시
            signupMoveButton.setOnClickListener {
                findNavController().navigate(R.id.action_login_Fragment_to_signinFragment)
            }

            // 닫기 버튼 클릭 시 오류 메시지 숨기기
            closeErrorButton.setOnClickListener {
                errorMessageBox.visibility = View.GONE
            }
        }
    }

}
