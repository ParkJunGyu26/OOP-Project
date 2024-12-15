package com.example.kau_oop_project.ui.user.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.databinding.FragmentRegisterBinding
import com.example.kau_oop_project.ui.user.viewmodel.RegisterViewModel
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.response.RegisterResponse

class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextWatchers()
        setupButtons()
        observeViewModel()
    }

    private fun setupTextWatchers() {
        binding?.apply {
            // 이메일 입력 감지
            editRegiEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { email ->
                        viewModel.validateEmailFormat(email)
                    }
                }
            })
            
            // 비밀번호 입력 감지
            editRegiPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { password ->
                        viewModel.validatePassword(password)
                    }
                }
            })
            
            // 학교 입력 감지
            editSchool.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { school ->
                        viewModel.validateSchool(school)
                    }
                }
            })
            
            // 이름 입력 감지
            editName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { name ->
                        viewModel.validateName(name)
                    }
                }
            })
            
            // 전공 입력 감지
            editMajor.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0?.toString()?.let { major ->
                        viewModel.validateMajor(major)
                    }
                }
            })
        }
    }

    private fun setupButtons() {
        binding?.apply {
            // 이메일 중복 검사
            btnReceiveNumber.setOnClickListener {
                editRegiEmail.text.toString().let { email ->
                    viewModel.checkEmailDuplication(email)
                }
            }

            // 로그인 화면으로 이동
            btnRegiToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            // 회원가입 버튼
            btnRegister.setOnClickListener {
                editRegiEmail.text.toString().let { email ->
                    editRegiPassword.text.toString().let { password ->
                        editSchool.text.toString().let { school ->
                            editName.text.toString().let { name ->
                                editMajor.text.toString().let { major ->
                                    viewModel.registerUser(email, password, school, name, major)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(viewLifecycleOwner) { state ->
            binding?.apply {
                txtEmailVerification.apply {
                    text = state.email.message
                    setTextColor(if (state.email.isValid) Color.BLUE else Color.RED)
                }

                txtPasswordValidation.apply {
                    text = state.password.message
                    setTextColor(if (state.password.isValid) Color.BLUE else Color.RED)
                }

                txtSchoolValidation.apply {
                    text = state.school.message
                    setTextColor(if (state.school.isValid) Color.BLUE else Color.RED)
                }

                btnReceiveNumber.isEnabled = state.email.isValid
            }
        }

        viewModel.isRegisterEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding?.btnRegister?.isEnabled = isEnabled
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResponse.Success -> {
                    Toast.makeText(context, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    view?.postDelayed({
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }, 1000)
                }
                is RegisterResponse.Error -> {
                    val message = when(result) {
                        is RegisterResponse.Error.DuplicateEmail -> "이미 존재하는 이메일입니다."
                        is RegisterResponse.Error.Unknown -> "회원가입 중 오류가 발생했습니다."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    result.logError()
                }
            }
        }
    }

    // 가비지 처리
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}