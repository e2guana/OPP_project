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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class signinFragment : Fragment() {
    val viewModel: Userdata_viewmodel by activityViewModels()                   //user 정보 뷰모델
    private var binding: FragmentSigninBinding? = null

    companion object {
        const val DEFAULT_PROFILE_IMAGE_RESOURCE = "drawable/ic_profile_icon" // 리소스 경로
        val DEFAULT_PROFILE_IMAGE_URL: String
            get() = "android.resource://com.example.opp_e2guana/$DEFAULT_PROFILE_IMAGE_RESOURCE"
    }

    // 이미지 선택 관련 변수
    private var selectedImageUri: Uri? = null // Uri
    private var imageData: ByteArray? = null // 저장할 Array
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // 결과 처리: 선택한 이미지 URI를 사용
        if (uri != null) {
            selectedImageUri = uri
            saveImageData()
        } else {
            showMessage("이미지를 선택하지 않을 시 기본 프로필로 대체됩니다.")
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
            // 닫기 버튼 클릭 시 오류 메시지 숨기기
            closeErrorButton.setOnClickListener {
                errorLayer.visibility = View.GONE
            }
            touchBlocker.setOnTouchListener { _, _ -> true }
        }
    }

    // 회원가입 처리 함수
    private fun handleSignup() {
        val name = binding?.nicknameEditText?.text.toString() // 텍스트 뷰에 입력된 닉네임
        val phone = binding?.phoneEditText?.text.toString() // 입력된 연락처
        val email = binding?.emailEditText?.text.toString() // 입력된 이메일
        val password = binding?.passwordEditText?.text.toString() //입력된 비번

        // 닉네임 유효성 검사
        viewModel.isValidNickname(name)?.let {
            showMessage(it)
            return
        }
        // 연락처 유효성 검사
        if (!viewModel.isValidPhone(phone)) {
            showMessage("연락처 형식이 올바르지 않습니다.")
            return
        }
        // 이메일 유효성 검사
        if (!viewModel.isValidEmail(email)) {
            showMessage("이메일 형식이 올바르지 않습니다.")
            return
        }
        // 비밀번호 유효성 검사
        if (!viewModel.isValidPassword(password)) {
            showMessage("비밀번호는 8자 이상 16자 미만, 영문과 숫자를 포함해야 합니다.")
            return
        }

        // 형식이 올바르면 오류 메시지 숨기기
        binding?.errorLayer?.visibility = View.GONE

        // Userdata_viewmodel의 유저데이터 수정
        viewModel.apply() {
            set_name(name)
            set_email(email)
            set_phone(phone)
            set_password(password)
        }

        // Firebase Auth에 계정 생성 및 데이터 업로드
        viewModel.createFirebaseUser { success, errormessage ->
            if (success) {
                uploadProfileImage { imageUrl ->
                    Log.d("Firebase", "Using image URL: $imageUrl")
                    viewModel.uploadUserDataToFirebase(imageUrl) // 반환된 URL 저장 및 사용자 정보 파이어베이스에 업로드
                    navigateToFriendList()
                }
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

    // 프로필 이미지를 Firebase Storage에 업로드 (Firebase가 Spark 무료요금제라서 Storage 기능이 잠겨있습니다. 추후 조치해야할듯)
    private fun uploadProfileImage(onComplete: (String) -> Unit) {
        if (imageData == null) {
            // 기본 이미지 URL로 대체
            Log.d("Firebase", "Not select profileImage")
            onComplete(DEFAULT_PROFILE_IMAGE_URL)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val profileRef = storageRef.child("profiles/${System.currentTimeMillis()}.jpg")

        profileRef.putBytes(imageData!!)
            .addOnSuccessListener { taskSnapshot ->
                // 업로드 성공, 업로드한 이미지 URL 가져오기
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri -> //여기에 걸려야하는데, 스토리지에 접근이 안됨
                    Log.d("Firebase", "Profile image upload: $uri")
                    onComplete(uri.toString()) // 업로드된 이미지 URL 반환
                }.addOnFailureListener { exception ->
                    // URL 가져오기 실패
                    Log.d("Firebase", "Failed to get download URL: ${exception.message}")
                    onComplete(DEFAULT_PROFILE_IMAGE_URL)
                }
            }
            .addOnFailureListener {
                // 업로드 실패, 기본 이미지 URL 반환
                Log.d("Firebase", "Failed Image") //현재 여기로 접근이 돼서 기본이미지로 반환되는 중
                onComplete(DEFAULT_PROFILE_IMAGE_URL)
            }
    }

    // 메시지 출력 함수(유효성 실패 메시지 출력)
    private fun showMessage(message: String) {
        // 형식이 올바르지 않으면 오류 메시지 보이기
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

    // 이미지 저장
    private fun saveImageData() {
        selectedImageUri?.let { uri ->
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 이미지를 정사각형으로 크롭
            val croppedBitmap = cropToSquare(originalBitmap)

            // Firebase 업로드를 위해 ByteArray로 변환
            val byteArrayOutputStream = ByteArrayOutputStream()
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            imageData = byteArrayOutputStream.toByteArray()

            // 프로필 이미지뷰에 정사각형 이미지 표시
            binding?.profileIcon?.setImageBitmap(croppedBitmap)
        }
    }

    // 이미지 크롭 함수 (정사각형으로 크롭)
    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newSize = minOf(width, height) // 이미지의 가장 작은 변 기준으로 정사각형 생성

        val xOffset = (width - newSize) / 2
        val yOffset = (height - newSize) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newSize, newSize)
    }

}
