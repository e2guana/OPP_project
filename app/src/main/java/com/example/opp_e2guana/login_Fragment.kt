package com.example.opp_e2guana

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 * Use the [login_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class login_Fragment : Fragment() {

    var binding: FragmentLoginBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.loginButton?.setOnClickListener {                                              //로그인 버튼
            findNavController().navigate(R.id.action_login_Fragment_to_signinFragment)
        }

        binding?.signupMoveButton?.setOnClickListener {                                        //회원가입 버튼
            findNavController().navigate(R.id.action_login_Fragment_to_signinFragment)
        }
    }
}