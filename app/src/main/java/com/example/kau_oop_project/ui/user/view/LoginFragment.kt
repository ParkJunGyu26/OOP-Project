package com.example.kau_oop_project.ui.user.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.response.LoginResponse
import com.example.kau_oop_project.databinding.FragmentLoginBinding
import com.example.kau_oop_project.ui.user.viewmodel.LoginViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()  // activityViewModels()에서 변경
    private val userViewModel: UserViewModel by activityViewModels()  // 로그인은 전체를 아우르기 때문에 activityViewModel 사용
    private var binding: FragmentLoginBinding? = null

    // 프래그먼트(뷰) 생성 전 세팅
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 프래그먼트(뷰) 생성되면서
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    // 프래그먼트(뷰)가 생성되고 난 이후
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupTextWatchers()
        observeViewModel()
    }

    private fun setupButtons() {
        binding?.apply {
            btnLogin.setOnClickListener {
                val inputEmail = editEmail.text.toString()
                val inputPassword = editPassword.text.toString()

                if (inputEmail.isBlank() || inputPassword.isBlank()) txtError.text = "이메일과 비밀번호를 모두 입력해주세요."
                else loginViewModel.verifyUser(inputEmail, inputPassword)
            }

            btnLoginToRegi.setOnClickListener {findNavController().navigate(R.id.action_loginFragment_to_registerFragment)}
        }
    }

    private fun setupTextWatchers() {
        binding?.apply {
            editEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { email ->
                        loginViewModel.validateEmailFormat(email)
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (result) {
                    is LoginResponse.Success -> {
                        userViewModel.loginUser(result)
                        findNavController().navigate(R.id.action_loginFragment_to_mypageFragment)
                    }
                    is LoginResponse.Error -> {
                        binding?.txtError?.text = "이메일 또는 비밀번호가 올바르지 않습니다."
                        result.logError()
                    }
                }
            }
        }

        loginViewModel.emailCheck.observe(viewLifecycleOwner) { state ->
            binding?.apply {
                txtError.apply {
                    text = state.message
                    if (state.isValid) btnLogin.isEnabled = true
                }
            }
        }
    }

    // 메모리 확보(바인딩을 가비지 처리)
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}