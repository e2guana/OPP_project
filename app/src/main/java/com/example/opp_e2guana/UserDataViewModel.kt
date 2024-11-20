package com.example.opp_e2guana

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserDataViewModel : ViewModel() {
    // 선택된 Friend_Data를 저장할 LiveData
    private val _selectedFriend = MutableLiveData<FriendListAdapter.Friend_Data>()
    val selectedFriend: LiveData<FriendListAdapter.Friend_Data> get() = _selectedFriend

    // 선택된 Friend_Data 설정 함수
    fun selectFriend(friend: FriendListAdapter.Friend_Data) {
        _selectedFriend.value = friend
    }
}
