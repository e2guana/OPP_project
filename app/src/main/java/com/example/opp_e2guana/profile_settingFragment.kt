package com.example.opp_e2guana

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.databinding.FragmentProfileSettingBinding
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException


/*
  - 이 프레그먼트는 사용자가 계정 정보를 수정하고 싶을 때 수정된 내용을 받아서 DB에 올리고 확인까지 해주는 페이지
  - 아이디는 고정
  - 이메일 주소, 비밀번호, 닉네임, 프로필 사진 4가지가 변경될 수 있음

   - j
 */

//기존에 가지고 있는 유저 정보 내용들, 변수들 하나로 묶어버립시다. - j
data class Original_user(
    val email: String,
    val phone: String,
    val name: String
)

class profile_settingFragment : Fragment() {
    //val viewModel: Userdata_viewmodel by viewModels() //viewModel을 viewModels로 위임한다(lazy처럼 적절하게 늦게 초기화함), -j
    val viewModel: Userdata_viewmodel by activityViewModels() //activity에 있는 (상위)모델을 보고 초기화한다 -j

    var binding: FragmentProfileSettingBinding? = null
    //우선 사용자 정보에 대한 번들이 넘어와야됨 이건 모두와 상의해서 어떤 식으로 번들을 주고 받을건지 정해야함! -j

    override fun onCreateView(                                                  //1차 바인딩 -j
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Firebase에서 사용자 데이터를 가져온다 -MOON
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.fetchUserData(userId) //viewModel에 동기화

        // 이름 변경에 대한 옵저버 -MOON 전화번호랑 이름이랑 옵저버 나눠놨습니다.
        viewModel.show_name.observe(viewLifecycleOwner) { name ->
            binding?.let { //뷰모델에 있는 라이브 데이터를 가져다가 바인딩? 한다고 함(이 프레그먼트의 viewlifecycle만큼 볼거다, this는 쓰면 안됨) -j
                it.showName.setText(name) // 이름 변경 사항을 UI에 반영
                it.editName.hint = name   // hint 설정
            }
        }
        // 전화번호 변경에 대한 옵저버ㅁ
        viewModel.show_phone.observe(viewLifecycleOwner) { phone ->
            binding?.let {
                it.showPhone.setText(phone) // 전화번호 변경 사항을 UI에 반영
                it.editPhone.hint = phone   // hint 설정
            }
        }

        binding?.resettingButton?.setOnClickListener {                  //변경하기 버튼, 이곳을 누르면 수정할건지 되물어보는 팝업창을 띄워야함! -j
            showResettingDialog()
        }

        /*
        계정 삭제 로직 구현
        계삭 할건지 물어보기 -> 파이어베이스 계삭 요청 -> 삭제 됬다면 True반환되게 설정함 -> 삭제되면 자동으로 로그아웃
         */
        binding?.removeButton?.setOnClickListener {                     // 계정 삭제 버튼,
            Log.d("profilesetting", "remove button")
        }

    }

    fun showResettingDialog() {                          //팝업창 -j
        val check_password =
            binding?.checkPassword?.text.toString()        //비밀번호 변경 전 기존 비밀번호 확인 -j

        MaterialAlertDialogBuilder(requireContext())    //메인 액티비티에서는 Context를 상속 받지만, 프레그먼트에서는 별도로 가져와야함 -j
            .setMessage("변경하시겠습니까?")
            .setNegativeButton("취소") { dialog, _ ->
                Log.d("profilesetting", "cancle")
                dialog.dismiss()
            }
            .setPositiveButton("확인") { _, _ ->
                Log.d("profilesetting", "ok")
                passwordCheck(check_password)
            }
            .show()
    }

    /*
    이 부분 코드 로직 좀 다시 점검 해볼 것. 리턴하는게 살짝 애매한데...
    비번 검증 -> 메일, 폰번호 유효성 검사 ->  문제 없으면 firebase올리고 정상적으로 변경됬는지 확인 및 반환
     */

    fun passwordCheck(password: String) {
        val original = Original_user(
            email = viewModel.show_email.value ?: "", // 이메일도 함께 가져오기 (loginUser 함수를 쓰기 위함)
            phone = viewModel.show_phone.value ?: "",
            name = viewModel.show_name.value ?: ""
        )
        val new_password = binding?.editPassword?.text.toString()

        val name = binding?.editName?.text?.toString()!!?.takeIf {
            it.isNotBlank()
        } ?: original.name //null이거나 공백일 때 기존 이름 유지
        val phone = binding?.editPhone?.text?.toString()?.takeIf {
            it.isNotBlank()
        } ?: original.phone //null이거나 공백일 때 기존 이메일 유지

        Log.e("name", "$name")
        Log.e("phone", "$phone")

        //phone 유효성 검사 함수 사용해야합니다. viewmodel에 isvaildPhone함수 있습니다. 사용법은 signinFragment.kt 참고 -MOON

        if (!viewModel.isValidPhone(phone)) {
            Toast.makeText(context, "연락처 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }                                                                                         //연락처 검사 이후 문제 없으면 파이어베이스 업로드 - j
        else {
            //loginUser 함수를 이용해 비밀번호 검증 -MOON
            viewModel.loginUser(original.email, password) { success, errormessage ->
                if (success) {
                    // Firebase에 사용자 정보 업로드, 일단 매개변수로 받는 URL은 임시로 기본이미지 주소로 함. 추후 수정 필요 -MOON
                    viewModel.uploadUserDataToFirebase("android.resource://com.example.opp_e2guana/drawable/ic_profile_icon")
                    Log.d("Firebase", "orpw $password newpw $new_password")
                    viewModel.updatePassword(new_password)
                    Toast.makeText(context, "변경에 성공하였습니다!", Toast.LENGTH_SHORT).show()

                    // viewModel 사용자정보 업데이트 <- 파이어베이스가 성공적으로 변경되고 난 후 업뎃해야 함 - j
                    viewModel.let {
                        it.set_name(name)
                        it.set_phone(phone)
                        it.set_password(password)
                    }
                } else {
                    //비밀번호가 틀린경우
                    Toast.makeText(context, "잘못된 비밀번호 입니다!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// 비번 안써도 되도록 수정하기, 비번을 안쓰면 널이 들어가서 다음에 로그인할 때 튕기는 현상 발견