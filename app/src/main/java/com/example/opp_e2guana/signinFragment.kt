package com.example.opp_e2guana

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentSigninBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException


class signinFragment : Fragment() {
    val viewModel: Userdata_viewmodel by activityViewModels() //user 정보 뷰모델
    private var binding: FragmentSigninBinding? = null

    // 이미지 선택 관련 변수
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.saveImageData(requireContext(), it)
            val bitmap = BitmapFactory.decodeByteArray(viewModel.imageData, 0, viewModel.imageData!!.size)
            binding?.profileIcon?.setImageBitmap(bitmap)
        } ?: showMessage("이미지를 선택하지 않았습니다.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼 클릭 리스너
        binding?.apply {
            signupButton.setOnClickListener { handleSignup() } // 회원가입 버튼
            backButton.setOnClickListener { navigateToLogin() } // 뒤로가기 버튼
            profileIcon.setOnClickListener { openGallery() } // 프로필 변경 클릭
            // 닫기 버튼 클릭 시 오류 메시지 숨기기
            closeErrorButton.setOnClickListener { errorLayer.visibility = View.GONE }
            touchBlocker.setOnTouchListener { _, _ -> true }
        }
    }

    // 회원가입 처리 함수
    private fun handleSignup() {
        val name = binding?.nicknameEditText?.text.toString() // 텍스트 뷰에 입력된 닉네임
        val phone = binding?.phoneEditText?.text.toString() // 입력된 연락처
        val email = binding?.emailEditText?.text.toString() // 입력된 이메일
        val password = binding?.passwordEditText?.text.toString() //입력된 비번

        if (!validateInputs(name, phone, email, password)) return // 이름, 폰번호, 이메일, 비번 유효성 검사

        // Userdata_viewmodel의 유저데이터 수정
        uploadProfileImage { imageUrl ->
            viewModel.apply {
                set_name(name)
                set_email(email)
                set_phone(phone)
                set_password(password)
                set_imageURL(imageUrl)
            }

            // Firebase Auth에 계정 생성 및 데이터 업로드
            viewModel.createFirebaseUser { success, errormessage ->
                if (success) {
                    viewModel.uploadUserDataToFirebase() // 반환된 URL 저장 및 사용자 정보 파이어베이스에 업로드
                    navigateToFriendList()
                } else {
                    binding?.errorLayer?.visibility = View.VISIBLE
                    when (errormessage) {
                        is FirebaseAuthUserCollisionException -> showMessage("이미 사용 중인 이메일입니다.")
                        is FirebaseNetworkException -> showMessage("네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                        else -> showMessage("알 수 없는 오류")
                    }
                }
            }
        }
    }

    private fun validateInputs(name: String, phone: String, email: String, password: String): Boolean {
        viewModel.isValidNickname(name)?.let {
            showMessage(it)
            return false
        }
        if (!viewModel.isValidPhone(phone)) {
            showMessage("연락처 형식이 올바르지 않습니다.")
            return false
        }
        if (!viewModel.isValidEmail(email)) {
            showMessage("이메일 형식이 올바르지 않습니다.")
            return false
        }
        if (!viewModel.isValidPassword(password)) {
            showMessage("비밀번호는 8자 이상 16자 미만, 영문과 숫자를 포함해야 합니다.")
            return false
        }
        binding?.errorLayer?.visibility = View.GONE
        return true
    }

    // 프로필 이미지를 Firebase Storage에 업로드
    private fun uploadProfileImage(onComplete: (String) -> Unit) {
        viewModel.uploadProfileImage { imageUrl ->
            onComplete(imageUrl ?: Userdata_viewmodel.DEFAULT_PROFILE_IMAGE_URL) // Firebase Storage에 업로드된 URL 반환
        }
    }

    // 메시지 출력 함수(유효성 실패 메시지 출력)
    private fun showMessage(message: String) {
        binding?.apply {
            errorLayer.visibility = View.VISIBLE
            errorMessage.text = message
        }
    }

    // 화면 전환: 친구 목록으로 이동
    private fun navigateToFriendList() =
        findNavController().navigate(R.id.action_signinFragment_to_friendlistFragment)

    // 화면 전환: 로그인 화면으로 이동
    private fun navigateToLogin() =
        findNavController().navigate(R.id.action_signinFragment_to_login_Fragment)

    // 갤러리 열기
    private fun openGallery() {
        pickImageLauncher.launch("image/*") // 갤러리 열기
    }


}
