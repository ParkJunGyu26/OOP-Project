package com.example.kau_oop_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.databinding.FragmentLoginBinding
import com.example.kau_oop_project.viewmodel.LoginViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    val viewModel: LoginViewModel by activityViewModels()
    var binding: FragmentLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 회원가입 버튼을 눌렀을 경우 네비게이션 (딱히 조건 필요없음)
        binding?.btnLoginToRegi?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding?.btnLogin?.setOnClickListener {
            val inputEmail = binding?.editEmail?.text.toString()
            val inputPassword = binding?.editPassword?.text.toString()

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                binding?.txtError?.text = "이메일과 비밀번호 모두를 입력해주세요."
            } else {
                viewModel.validUser(inputEmail, inputPassword)
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            println("isSuccess : ${isSuccess}")
            if (isSuccess) {
                findNavController().navigate(R.id.action_loginFragment_to_mypageFragment)
            } else {
                binding?.txtError?.text = "회원 정보가 틀렸습니다. 다시 로그인해주세요."
            }
        }
    }

    // 메모리 확보(바인딩을 가비지 처리)
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}