package com.example.kau_oop_project.ui.post.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.*
import com.example.kau_oop_project.databinding.FragmentPostWriteBinding
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel

class PostWriteFragment : Fragment() {

    private var binding: FragmentPostWriteBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val contentList = mutableListOf<PostContent>() // 텍스트와 이미지 관리

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostWriteBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 게시글 제출 버튼 클릭 리스너
        binding?.buttonSubmit?.setOnClickListener {
            uploadPost()
        }
    }

    private fun addContent(content: PostContent) {
        contentList.add(content)
        Toast.makeText(requireContext(), "이미지가 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun uploadPost() {
        binding?.apply {
            val title = inputTitle.text.toString()
            val tag = inputTag.text.toString()
            val content = inputContent.text.toString()
            Log.d("PostWriteFragment", "current user id : ${userViewModel.currentUser.value?.uid}")
            // currentUser UID 가져오기
            userViewModel.currentUser.value?.let { user ->
                // ViewModel에 업로드 요청
                postViewModel.uploadPost(title, tag, content, user)

                // 업로드 결과 관찰
                var isToasted = false
                postViewModel.UploadResult.observe(viewLifecycleOwner) { result ->
                    if (!isToasted) {
                        isToasted=true
                        when {
                            result.isSuccess -> {
                                // 성공 시
                                Toast.makeText(requireContext(),"게시글이 등록되었습니다.",Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.postListFragment)
                            }
                            result.isFailure -> {
                                // 실패 시
                                val exception = result.exceptionOrNull()
                                Toast.makeText(requireContext(),"${exception?.message}",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } ?: run {
                Toast.makeText(requireContext(), "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
