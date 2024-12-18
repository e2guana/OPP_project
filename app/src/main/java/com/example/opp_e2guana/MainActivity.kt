package com.example.opp_e2guana

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute.Location
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opp_e2guana.databinding.ActivityMainBinding
import com.example.opp_e2guana.viewmodel.Userdata_viewmodel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.BuildConfig
import java.util.Locale

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

         - j
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.login_Fragment -> binding.bottomNav.visibility = View.GONE             //로그인 -j
                R.id.signinFragment -> binding.bottomNav.visibility = View.GONE             //회원가입 -j
                R.id.chatFragment -> binding.bottomNav.visibility = View.GONE               //채팅방 -j
                R.id.show_locationFragment -> binding.bottomNav.visibility = View.GONE      //친구 위치 -j

                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }

        /*
        로그아웃 버튼을 눌렀을 때 로그아웃 여부를 확인해주는 팝업창을 띄워주는 내용
        엄밀히 따지면 현재 모든 하단 네비게이션 동작을 아래 함수에서 제어할 수 있게 됨. -j
         */
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.login_Fragment -> {
                    showLogoutDialog()
                    true
                }
                R.id.friendlistFragment -> {    //로그아웃 이외에 애들도 when 안에 넣어줘야지 동작함 아마도 안넣으면 다 false로 가는듯 -j
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

    fun showLogoutDialog() {                                //팝업창 띄우는 내용 -j
        MaterialAlertDialogBuilder(this)
            .setMessage("로그아웃 하시겠습니까?")
            .setNegativeButton("취소") { dialog, _ ->  //다이얼로그 버튼시 호출되는 콜백 함수에 전달되는 파라미터 'dialog'만 사용하겠다는 의미 -j
                dialog.dismiss()
            }
            .setPositiveButton("확인") { _, _ ->      //네거티브면 왼쪽, 포지티브면 오른쪽에 버튼 위치함 -j
                //여기서 부터 로그아웃에 대한 과정을 추가하면 됨. -j
                val navController = binding.frgNav.getFragment<NavHostFragment>().navController
                navController.navigate(R.id.login_Fragment)
            }
            .show()
    }

    fun getLocation() {                                     //gps 정보 불러오기 - j
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        //val viewModel: Userdata_viewmodel by viewModels()


        if (ActivityCompat.checkSelfPermission(                                     //퍼미션 검사, 어플리케이션 설정에서 미리 허가해서 문제 없음 -j
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        var gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.d("location", "${gpsLocation?.longitude}")                  //위도 경도 불러오는 것까지는 문제없이 되는 것까지 확인 - j
        Log.d("location", "${gpsLocation?.latitude}")
    }


}

