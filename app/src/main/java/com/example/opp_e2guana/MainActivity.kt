package com.example.opp_e2guana

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opp_e2guana.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.BuildConfig

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val navController = binding.frgNav.getFragment<NavHostFragment>().navController
        binding.bottomNav.setupWithNavController(navController)

        setContentView(binding.root)
        /*
        프레그먼트 전환 이벤트 감지, 내비게이션 대상이 변경될 때마다 호출됨.
        2번째 파라미터인 destination이 현재 보여지고 있는 프레그먼트가 어떤건지를 알고 있음

        이 부분은 특정 프레그먼트에서 네비게이션 바를 안보이게 하기 위한 함수임
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("destination","${destination}")
            when (destination.id) {
                R.id.login_Fragment -> binding.bottomNav.visibility = View.GONE     //로그인
                R.id.signinFragment -> binding.bottomNav.visibility = View.GONE     //회원가입
                R.id.chatFragment -> binding.bottomNav.visibility = View.GONE       //채팅방
                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }

        /*
        로그아웃 버튼을 눌렀을 때 로그아웃 여부를 확인해주는 팝업창을 띄워주는 내용
        엄밀히 따지면 현재 모든 하단 네비게이션 동작을 아래 함수에서 제어할 수 있게 됨.
         */
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.login_Fragment -> {
                    showLogoutDialog()
                    true
                }
                R.id.friendlistFragment -> {    //로그아웃 이외에 애들도 when 안에 넣어줘야지 동작함 아마도 안넣으면 다 false로 가는듯
                    navController.navigate(R.id.friendlistFragment)
                    true
                }
                R.id.profile_settingFragment -> {
                    navController.navigate(R.id.profile_settingFragment)
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frg_nav)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun showLogoutDialog() {                                //팝업창 띄우는 내용
        MaterialAlertDialogBuilder(this)
            .setMessage("로그아웃 하시겠습니까?")
            .setNegativeButton("취소") { dialog, _ ->  //다이얼로그 버튼시 호출되는 콜백 함수에 전달되는 파라미터 'dialog'만 사용하겠다는 의미
                dialog.dismiss()
            }
            .setPositiveButton("확인") { _, _ ->      //네거티브면 왼쪽, 포지티브면 오른쪽에 버튼 위치함
                //여기서 부터 로그아웃에 대한 과정을 추가하면 됨.
                val navController = binding.frgNav.getFragment<NavHostFragment>().navController
                navController.navigate(R.id.login_Fragment)
            }
            .show()
    }
}

