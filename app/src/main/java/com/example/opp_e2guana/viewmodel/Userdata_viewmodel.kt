package com.example.opp_e2guana.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.FriendListAdapter
import com.example.opp_e2guana.repository.UserRepository

class Userdata_viewmodel:ViewModel() {

    //라이브 데이터라고 해서 (외부)프레그먼트에서 모니터링(옵저브)할 수 있는 string임, 외부 관찰은 가능하나 함부로 못바꿈(private 반드시 걸것)
    private val name = MutableLiveData<String>("UNKNOWN")
    val show_name : LiveData<String> get() = name //이런식으로 볼 수는 있으나 외부에서 바꿀 수 없는 패턴으로 디자인해야함

    private val emailAddress = MutableLiveData<String>("Not have email")
    val show_email :LiveData<String> get() = emailAddress

    private val phoneNumber = MutableLiveData<String>("Not have phoneNumber")
    val show_phone :LiveData<String> get() = phoneNumber

    private val password = MutableLiveData<String>("error") //패스워드는 당연히 보이면 안됨!!
    val show_password :LiveData<String> get() = password          //비밀번호 변경할 때 검증 목적으로

    fun set_name(nameData:String) {                     //이름 변경
        Log.d("name", "$nameData")
        val setName = name.value?.let {                 //name.value를 바꾸지 않고 리포지토리로 발로 올리게 됨. DB의존도가 더 커지는거 아닌가?
            nameData
        } ?: "UNKNOWN"

        repository.postName(setName)
    }

    fun set_email(emailData:String) {                   //이메일 변경
        Log.d("email", "$emailData")
        emailAddress.value = emailAddress.value?.let {
            emailData
        } ?: "Not have email"
    }

    fun set_phone(phone:String) {                       //전화번호 변경
        Log.d("phone", "$phone")
        phoneNumber.value = phoneNumber.value?.let {
            phone
        } ?: "Not have phoneNumber"
    }

    fun set_password(setPassword:String) {              //패스워드 변경
        Log.d("password", "$setPassword")
        password.value = password.value?.let {
            setPassword
        } ?: "Error"
    }

    // 선택된 Friend_Data를 저장할 LiveData
    private val _selectedFriend = MutableLiveData<FriendListAdapter.Friend_Data>()
    val selectedFriend: LiveData<FriendListAdapter.Friend_Data> get() = _selectedFriend


    fun selectFriend(friend: FriendListAdapter.Friend_Data) {   //친구 리스트 정보 불러오기
        _selectedFriend.value = friend
    }

    private val repository = UserRepository()       //얘는 다른 프로퍼티가 선언되고 나서 나중에 받도록 뒤에 있어야함
    init {
        repository.observeUser(name)
    }

}