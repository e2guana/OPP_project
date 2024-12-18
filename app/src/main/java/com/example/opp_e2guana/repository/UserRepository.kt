package com.example.opp_e2guana.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*

class UserRepository {
    val database = Firebase.database
    val userRef = database.getReference("user")

    fun observeUser(name:MutableLiveData<String>) {
        userRef.addValueEventListener(object: ValueEventListener{       //리스너 함수는 2개로 되어있는 객체를 받음, 얘가 실제로 데이터를 가져오는 역할
            override fun onDataChange(snapshot: DataSnapshot) {         //데이터가 처음 불러올 때랑 바뀔 때 읽게 되는 함수
                name.postValue(snapshot.value.toString())               //DB에서 받아올 때 string형태로 바로 들어오지 않음, postValue는 백그라운드에서 받을 수 있게 설정

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun postName(newValue: String) {        //리포지토리에 내용이 변경되는걸 반영하는 역할
        //userRef.setValue(newValue)
        Log.d("name", "change")
    }

}