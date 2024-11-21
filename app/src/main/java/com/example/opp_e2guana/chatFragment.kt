package com.example.opp_e2guana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels

class chatFragment : Fragment() {
    // UserDataViewModel 가져오기 (friendlistFragment에서 친구데이터를 가져옴)
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // ViewModel에서 Friend_Data 가져오기
        userDataViewModel.selectedFriend.observe(viewLifecycleOwner) { friend ->
            // Friend_Data의 정보로 UI 업데이트 (받은 정보를 활용하는 예시)
            view.findViewById<TextView>(R.id.textView3).text =
                "Name: ${friend.name}\nContact: ${friend.contact}\nUser ID: ${friend.user_id}"
        }

        return view
    }
}