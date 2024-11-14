package com.example.opp_e2guana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.opp_e2guana.databinding.FragmentLoginBinding
import com.example.opp_e2guana.databinding.FragmentSigninBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [signinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class signinFragment : Fragment() {

    var binding: FragmentSigninBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // signupButton 클릭 시 friendlistFragment로 이동
        binding?.signupButton?.setOnClickListener {
            findNavController().navigate(R.id.action_signinFragment_to_friendlistFragment)
        }

        // backButton 클릭 시 login_Fragment로 이동
        binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_signinFragment_to_login_Fragment)
        }
    }


}