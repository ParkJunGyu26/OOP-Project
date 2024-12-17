package com.example.kau_oop_project.ui.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.kau_oop_project.R
import com.example.kau_oop_project.databinding.FragmentUpdateMypageBinding
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import android.app.AlertDialog
import android.widget.Toast
import com.example.kau_oop_project.data.model.User
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.ui.user.viewmodel.MypageViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UpdateResponse
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.example.kau_oop_project.ui.user.viewmodel.ImageUploadState
import com.example.kau_oop_project.data.model.response.MyInfo
import com.example.kau_oop_project.data.model.response.MypageResponse


class updateMypageFragment : Fragment() {
    private var binding: FragmentUpdateMypageBinding? = null
    private val userViewModel: UserViewModel by activityViewModels()
    private val mypageViewModel: MypageViewModel by viewModels()

    // 갤러리에서 이미지 선택을 위한 launcher
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            mypageViewModel.uploadImage(selectedImageUri)  // uid 전달 제거
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateMypageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadUserInfo()
        setupButtons()
        observeViewModel()
    }

    private fun loadUserInfo() {
        // 현재 로그인된 유저의 uid로 정보 가져오기
        userViewModel.currentUser.value?.let { uid ->
            mypageViewModel.getUserInfo(uid)  // MypageViewModel을 통해 상세 정보 로드
        }
    }

    private fun setupButtons() {
        // 이미지 추가 버튼 클릭 리스너
        binding?.btnAddPhoto?.setOnClickListener {
            showPhotoOptionsDialog()
        }

        // 수정하기 버튼 클릭 리스너
        binding?.btnUpdate?.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun observeViewModel() {
        // 마이페이지 정보 관찰
        mypageViewModel.mypageResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is MypageResponse.Success -> {
                    binding?.apply {
                        // 기존 정보로 필드 초기화
                        txtBeforeMyName.setText(result.userInfo.name)
                        txtBeforeMyEmail.setText(result.userInfo.userEmail)
                        txtBeforeMySchool.setText(result.userInfo.school)
                        txtBeforeMyMajor.setText(result.userInfo.major)

                        // 프로필 이미지 설정
                        Glide.with(requireContext())
                            .load(result.userInfo.profileImage)
                            .circleCrop()
                            .error(R.drawable.default_image)
                            .into(txtMyBeforeImage)
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
                }
            }
        }

        // 업데이트 결과 관찰
        mypageViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UpdateResponse.Success -> {
                    Toast.makeText(context, "수정이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_updateMypageFragment_to_mypageFragment)
                }
                is UpdateResponse.Error -> {
                    Toast.makeText(context, "수정 실패: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 이미지 업로드 상태 관찰
        mypageViewModel.imageUploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ImageUploadState.Loading -> {
                    // 로딩 표시
                    Toast.makeText(context, "이미지 업로드 중...", Toast.LENGTH_SHORT).show()
                }
                is ImageUploadState.Success -> {
                    // 이미지 업로드 성공 - UI 업데이트
                    binding?.txtMyBeforeImage?.let { imageView ->
                        Glide.with(requireContext())
                            .load(state.imageUrl)
                            .circleCrop()
                            .error(R.drawable.default_image)
                            .into(imageView)
                    }
                    Toast.makeText(context, "이미지가 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
                is ImageUploadState.Error -> {
                    Toast.makeText(context, "이미지 업로드 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPhotoOptionsDialog() {
        val options = arrayOf("수정하기", "삭제하기")
        
        AlertDialog.Builder(requireContext())
            .setTitle("프로필 사진")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> getContent.launch("image/*")
                    1 -> mypageViewModel.setDefaultImage()
                }
            }
            .show()
    }

    private fun updateUserInfo() {
        binding?.apply {
            val currentUid = userViewModel.currentUser.value ?: return
            
            // 현재 표시된 프로필 이미지 URL 가져오기
            val currentProfileImage = when (val result = mypageViewModel.mypageResult.value) {
                is MypageResponse.Success -> result.userInfo.profileImage
                else -> User().profileImageUrl
            }

            val updatedUser = MyInfo(
                uid = currentUid,
                name = txtBeforeMyName.text.toString(),
                userEmail = txtBeforeMyEmail.text.toString(),
                school = txtBeforeMySchool.text.toString(),
                major = txtBeforeMyMajor.text.toString(),
                profileImage = currentProfileImage
            )
            mypageViewModel.updateUserInfo(updatedUser)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}