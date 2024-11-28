package com.example.opp_e2guana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast //오류창 (비밀번호 유효화검사 등)
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentSigninBinding
import android.content.Intent //여기부터 이미지 관련 임포트
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class signinFragment : Fragment() {
    val viewModel: Userdata_viewmodel by activityViewModels()                   //user 정보 뷰모델
    private var binding: FragmentSigninBinding? = null

    // 이미지 선택 관련 변수
    private var selectedImageUri: Uri? = null // Uri
    private var imageData: ByteArray? = null // 저장할 Array
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // 결과 처리: 선택한 이미지 URI를 사용
        if (uri != null) {
            selectedImageUri = uri
            binding?.profileIcon?.setImageURI(uri) // ImageView에 이미지 표시
            saveImageData()
        } else {
            showToast("이미지를 선택하지 않을 시 기본 프로필로 대체됩니다.")
        }
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
        }
    }

    // 회원가입 처리 함수
    private fun handleSignup() {
        val nickname = binding?.nicknameEditText?.text.toString() // 입력된 닉네임
        val phone = binding?.phoneEditText?.text.toString() // 입력된 연락처
        val email = binding?.emailEditText?.text.toString() // 입력된 이메일
        val password = binding?.passwordEditText?.text.toString() //입력된 비번

        // 닉네임 유효성 검사
        isValidNickname(nickname)?.let {
            showToast(it) // 유효성 검사 실패 메시지
            return
        }
        // 연락처 유효성 검사
        if (!isValidPhone(phone)) {
            showToast("연락처 형식이 올바르지 않습니다.") // 유효성 검사 실패 메시지
            return
        }
        // 이메일 유효성 검사
        if (!isValidEmail(email)) {
            showToast("이메일 형식이 올바르지 않습니다.")
            return
        }
        // 비밀번호 유효성 검사
        if (!isValidPassword(password)) {
            showToast("비밀번호는 8자 이상 16자 미만, 영문과 숫자를 포함해야 합니다.")
            return
        }

        // Firebase 배우기 전에 더미데이터 임시 저장
        viewModel.apply() {
            set_name(nickname)
            set_email(email)
            set_phone(phone)
            set_password(password)
        }


        // 조건이 모두 충족되면 화면 전환
        navigateToFriendList()
    }
    object UserData { // Firebase 대체, Firebase 쓸 때 지울 예정
        var userId: String? = null
        var nickname: String? = null
        var phone: String? = null
        var email: String? = null
        var password: String? = null
    }

    // 닉네임 유효성 검사 함수
    private fun isValidNickname(nickname: String): String? {
        return when {
            nickname.isBlank() -> "닉네임은 비어 있을 수 없습니다."
            nickname.contains(" ") -> "닉네임에는 공백이 포함될 수 없습니다."
            nickname.toByteArray(Charsets.UTF_8).size > 30 -> "닉네임은 최대 한글 10글자, 영어 30글자까지 가능합니다."
            else -> null //닉네임이 유효하다면 null 반환
        }
    }
    // 연락처 유효성 검사 함수
    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^01[016789]-?\\d{3,4}-?\\d{4}$" //ex) 010-1234-5678
        return phone.matches(Regex(phonePattern))
    }
    // 이메일 유효성 검사 함수
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}" //ex) abcd@java.com
        return email.matches(Regex(emailPattern))
    }
    // 비밀번호 유효성 검사 함수
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}$" //ex) abcd1234
        return password.matches(passwordPattern.toRegex())
    }
    // Toast 메시지 출력 함수(유효성 실패 메시지 출력)
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

    // 이미지 저장
    private fun saveImageData() {
        selectedImageUri?.let { uri ->
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            imageData = inputStream?.readBytes()
            inputStream?.close()
            // 이미지 데이터가 저장되었습니다.
            // 추후에 데이터베이스에 업로드할 수 있습니다.
        }
    }

    /* add/hide인 경우 쓸 필요 없음
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    } */
}
