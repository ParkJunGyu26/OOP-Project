package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.data.model.*
import com.example.kau_oop_project.data.model.user.User
import com.example.kau_oop_project.databinding.FragmentPostWriteBinding
import com.example.kau_oop_project.viewmodel.PostViewModel

class PostWriteFragment : Fragment() {

    private var binding: FragmentPostWriteBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()
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
            uploadPostAndNavigate()
        }
    }

    private fun addContent(content: PostContent) {
        contentList.add(content)
        Toast.makeText(requireContext(), "이미지가 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun uploadPostAndNavigate() {
        binding?.apply {
            // 제목 입력란의 텍스트를 가져옴.
            val title = titleInput.text.toString()
            // 주제 선택 스피너에서 선택된 값을 가져온다. 아직 미구현
            val tag = subjectSpinner.selectedItem?.toString() ?: ""

            // 제목이 비어 있는 경우 사용자에게 채워넣으라는 알림을 보냄
            if (title.isBlank()) {
                Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return
            }

            // 내용 입력란이 비어 있는지 확인하고, 비어 있으면 사용자에게 채워넣으라는 알림을 보냄.
            if (contentInput.text.toString().isBlank()) {
                Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return
            }

            // 내용 입력란의 텍스트를 줄 단위로 분리
            val contentLines = contentInput.text.toString().lines()

            // 각 줄을 분석하여 PostContent 객체의 리스트를 생성
            val postContents = contentLines.mapNotNull { line ->
                // 추후 이미지 업로드 기능 구현 시, true 조건을 설정해 이미지 처리합니다.
                if (false) {
                    PostContent(type = ContentType.IMAGE, content = line)
                } else {
                    PostContent(type = ContentType.TEXT, content = line)
                }
            }

            // Post 객체를 생성
            val post = Post(
                postId = "", // 게시글이 갖는 고유 ID, 아직은 미사용, 고민 중..
                postDBId = "", // 추가 데이터베이스 ID 필드 (필요한 경우 활용 가능), 마찬가지로 아직은 미사용
                postTag = tag, // 사용자가 선택한 주제 태그
                postTitle = title, // 입력된 제목
                postRecommendCount = 0, // 추천 수는 초기값으로 0 설정
                postViewCount=0, // 초기 조회수는 0
                postAuthor = User( // 나중에 user 파트가 완성되면 nowUser이 될 것
                    uid = "adminUid", // 작성자 고유 ID
                    userEmail = "admin@naver.com", // 작성자 이메일
                    school = "KAU", // 작성자 학교
                    name = "김어드민", // 작성자 이름
                    major = "관리자" // 작성자 전공
                ),
                postContent = postContents, // PostContent 리스트 전달
                postReplyList = emptyList(), // 댓글 리스트는 초기값으로 빈 리스트로 설정
                postTimeStamp = System.currentTimeMillis() // 현재 시간을 타임스탬프로 기록
            )

            // ViewModel을 사용하여 Firebase에 Post 데이터를 업로드.
            postViewModel.uploadPost(post)

            // 업로드 성공 시 사용자에게 알림을 표시하고, 게시글 목록 화면으로 이동합니다.
            Toast.makeText(requireContext(), "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.postListFragment) // PostListFragment로 이동
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
