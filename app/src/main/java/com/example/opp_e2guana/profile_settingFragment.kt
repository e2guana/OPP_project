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


/*
  - 이 프레그먼트는 사용자가 계정 정보를 수정하고 싶을 때 수정된 내용을 받아서 DB에 올리고 확인까지 해주는 페이지
  - 아이디는 고정
  - 이메일 주소, 비밀번호, 닉네임, 프로필 사진 4가지가 변경될 수 있음
 */


class profile_settingFragment : Fragment() {
    //val viewModel: Userdata_viewmodel by viewModels() //viewModel을 viewModels로 위임한다(lazy처럼 적절하게 늦게 초기화함),
    val viewModel : Userdata_viewmodel by activityViewModels() //activity에 있는 (상위)모델을 보고 초기화한다

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

        viewModel.show_name.observe(viewLifecycleOwner) {//뷰모델에 있는 라이브 데이터를 가져다가 바인딩? 한다고 함(이 프레그먼트의 viewlifecycle만큼 볼거다, this는 쓰면 안됨)
            binding?.let() {
                it.showName.setText(viewModel.show_name.value)
                it.showEmail.setText(viewModel.show_email.value)
                it.editName.hint = viewModel.show_name.value   //hint는 글자가 쓰여있는게 아니라 그뒤로 보이는 내용임
                it.editEmail.hint = viewModel.show_email.value
            }
        }

        binding?.testbutton?.setOnClickListener {   //테스트 버튼
            findNavController().navigate(R.id.action_profile_settingFragment_to_show_locationFragment)
        }

        binding?.resettingButton?.setOnClickListener {                  //변경하기 버튼, 이곳을 누르면 수정할건지 되물어보는 팝업창을 띄워야함!
            // showResettingDialog()
            showResettingDialog()
        }
    }

    fun showResettingDialog() {                          //팝업창
        val check_password = binding?.checkPassword?.text.toString()        //비밀번호 변경 전 기존 비밀번호 확인

        MaterialAlertDialogBuilder(requireContext())    //메인 액티비티에서는 Context를 상속 받지만, 프레그먼트에서는 별도로 가져와야함
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

    fun passwordCheck(password: String) {
        val original_password = viewModel.show_password.value
        if(password != original_password) {
            Toast.makeText(context,"잘못된 비밀번호 입니다!", Toast.LENGTH_SHORT).show()
        } else {
            val name = binding?.editName?.text.toString()                       //임시 테스트
            val email = binding?.editEmail?.text.toString()
            val password = binding?.editPassword?.text.toString()

            viewModel.let {                                            //이 부분 반환할 때 예를들어 이름만 바뀌면 나머지 메일이랑 비번은 null로 가서 초기화 되는게 아니라 이전에 있던 데이터를 유지해야됨
                it.set_name(name)                                      //파라미터는 무조건 string으로만 보내야해서 false를 넣을 순 없음
                it.set_email(email)
                it.set_password(password)
            }

            //파이어베이스 내용 변경 확인 후 변경 성공했다고 알리기

            Toast.makeText(context,"변경에 성공하였습니다!", Toast.LENGTH_SHORT).show()
        }
    }
}