package com.example.kau_oop_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.databinding.FragmentRegisterBinding
import com.example.kau_oop_project.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    var binding: FragmentRegisterBinding? = null
    val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding?.btnReceiveNumber?.setOnClickListener {} -> 인증번호 발송하는 건 우선순위 낮춤

        // 로그인을 다시 누르면 로그인 화면으로 이동
        binding?.btnRegiToLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

//        viewModel.userInfo.observe(viewLifecycleOwner) {
//
//        }

        binding?.btnRegister?.setOnClickListener {
            val inputEmail = binding?.editRegiEmail?.text.toString()

            val inputPassword = binding?.editRegiPassword?.text.toString()
            val inputScool = binding?.editSchool?.text.toString()
            val inputName = binding?.editName?.text.toString()
            val inputMajor = binding?.editMajor?.text.toString()


        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }
}