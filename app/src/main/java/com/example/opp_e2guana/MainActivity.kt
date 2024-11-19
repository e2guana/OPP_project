package com.example.opp_e2guana

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opp_e2guana.databinding.ActivityMainBinding

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
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("destination","${destination}")
            when (destination.id) {
                R.id.login_Fragment -> binding.bottomNav.visibility = View.GONE     //로그인
                R.id.signinFragment -> binding.bottomNav.visibility = View.GONE   //회원가입
                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frg_nav)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}