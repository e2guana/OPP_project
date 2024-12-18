package com.example.opp_e2guana

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.databinding.FragmentShowLocationBinding
import com.google.android.gms.common.api.GoogleApiClient

import com.google.android.gms.maps.*                                        //지도
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker                             //마커
import com.google.android.gms.maps.model.MarkerOptions                      //마커 설정


/*
    이 프레그먼트는 우선 google맵에 대한 api를 받아서 배경에 띄워야함!
    이 화면에 들어왔을 때 보이는 위치는 친구 위치를 받아서 보여줘야됨
    친구의 이름과 이메일 주소를 띄워줘야 함. 이 부분은 친구 리스트에서 들어왔을 때 이름과 이메일 정보를 받아오는게 좋을듯 or 파이어베이스에서 +위치 정보까지 한 번에 받기?

    만약 맵에서 친구 위치를 어떻게 상세하게 보여줄건지?
        1. api(맵의) 배경은 고정한 채로 구글 맵을 움직일 수 있게 한다.
        2. api(맵의) 배경을 눌렀을 때 새 창이 열리며 전체화면으로 지도를 볼 수 있게한다.

    프로필을 눌렀을 때 친구의 프로필 사진을 띄워줄건지 아니면 프로필 이미지 또한 고정할건지 생각해야됨 <- 현재로썬 일단 고정

    https://tekken5953.tistory.com/17
    지도 정보 불러오는거는 나중에 백그라운드 돌려서 특정 시간마다 받아와야됨. 일단은 여기서 내 위치를 불렀을 때 제대로 뜨는지 테스트 우선하고, 동시에 LMS에 있는 백그라운드도 같이 만들기


    - j
 */


class show_locationFragment : Fragment() {

    var binding: FragmentShowLocationBinding? = null
    private lateinit var google_map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowLocationBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment  //mapfragment가 null이면 log 출력 <- null이면 로딩이 안되고 튕김 -j
        mapFragment?.getMapAsync { googleMap ->
            google_map = googleMap

            val KAU_address = LatLng(37.600228, 126.865377)         //항공대 -j
            val Default_location_KAU = google_map.addMarker(                        //위치가 출력되지 않을 경우 보여줄 위치 -j
                MarkerOptions()
                    .position(KAU_address)
                    .title("기본 위치")
            )
            Default_location_KAU?.showInfoWindow()                                  //마커 위에 타이틀을 항상 띄어주는 내용 -j

            google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(KAU_address, 17f))        //-1부터 가능 -1은 세계지도로 보여줌. float 형식으로 받음. 17이 제일 적당한듯? -j
        } ?: Log.e("showlocation_map", "Mapfragment is null")

        binding?.backButton?.setOnClickListener {                                                   //뒤로 가기 버튼 -j
            parentFragmentManager.popBackStack() //현재 Fragment를 스택에서 제거하는 함수
        }


        binding?.gochatButton?.setOnClickListener() {                                               //채팅 아이콘 버튼, 누르면 채팅방으로 이동 -j
            findNavController().navigate(R.id.action_show_locationFragment_to_chatFragment)
        }

    }
}