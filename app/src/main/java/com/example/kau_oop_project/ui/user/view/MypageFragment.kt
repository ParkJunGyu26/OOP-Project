package com.example.kau_oop_project.ui.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kau_oop_project.databinding.FragmentMypageBinding
import com.example.kau_oop_project.ui.user.viewmodel.MypageViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import com.example.kau_oop_project.data.model.response.MypageResponse
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.R
import com.bumptech.glide.Glide
import android.widget.Toast

class MypageFragment : Fragment() {
    private var binding: FragmentMypageBinding? = null
    private val mypageViewModel: MypageViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupButtons()
        loadUserInfo()
        
        // 유저 정보 변경 관찰
        mypageViewModel.mypageResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is MypageResponse.Success -> {
                    binding?.apply {
                        txtMyName.text = result.userInfo.name
                        
                        Glide.with(requireContext())
                            .load(result.userInfo.profileImage)
                            .circleCrop()
                            .error(R.drawable.default_image)
                            .into(txtMyImage)
                    }
                }
                is MypageResponse.Error -> {
                    when (result) {
                        is MypageResponse.Error.UserNotFound -> {
                            Toast.makeText(context, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        is MypageResponse.Error.ImageUploadFailed -> {
                            Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                        is MypageResponse.Error.UpdateFailed -> {
                            Toast.makeText(context, "정보 업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                        is MypageResponse.Error.Unknown -> {
                            Toast.makeText(context, "알 수 없는 에러", Toast.LENGTH_SHORT).show()
                        }
                    }
                    result.logError()  // 에러 로깅
                }
            }
        }
    }

    private fun setupButtons() {
        binding?.apply {
            btnMypageToUpdate.setOnClickListener {
                findNavController().navigate(R.id.action_mypageFragment_to_updateMypageFragment)
            }

            btnMypageToLog.setOnClickListener {
                findNavController().navigate(R.id.action_mypageFragment_to_myLogFragment)
            }
        }
    }

    private fun loadUserInfo() {
        userViewModel.currentUser.value?.let { uid ->
            mypageViewModel.getUserInfo(uid)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}