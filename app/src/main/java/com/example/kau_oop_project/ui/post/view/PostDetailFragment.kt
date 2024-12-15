package com.example.kau_oop_project.ui.post.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.data.model.Reply
import com.example.kau_oop_project.databinding.FragmentPostDetailBinding
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: Long): String {
    // 타임스탬프를 Date 객체로 변환
    val date = Date(timestamp)

    // 원하는 포맷으로 변환
    val format = SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault())

    // 포맷된 시간 문자열 반환
    return format.format(date)
}

class PostDetailFragment : Fragment() {

    private var binding: FragmentPostDetailBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 선택된 게시물 가져오기
        postViewModel.selectedPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                binding?.apply {
                    postDetailTitle.text = it.postTitle
                    postDetailTag.text = it.postTag
                    postDetailAuthor.text = it.postAuthorId
                    postDetailTimeStamp.text = "작성 시간 ${formatTimestamp(it.postTimeStamp)}"
                    postDetailRecommendCount.text = "추천 ${it.postRecommendCount.toString()}"
                    postDetailViewCount.text="조회수 ${it.postViewCount}"
                    postDetailReplyCount.text = "댓글 ${it.postReplyIdList.size.toString()}"

                    // PostContent가 TEXT인 경우 텍스트 합쳐서 표시
                    if (it.postContent.isNotEmpty()) {
                        // List<PostContent>의 모든 content를 "\n"으로 결합
                        val combinedText = it.postContent.joinToString("\n") { postContent ->
                            postContent.content
                        }
                        postDetailContent.text = combinedText
                    }
                }
            }

            binding?.btnDelete?.setOnClickListener {
                // 삭제 확인 다이얼로그 표시
                AlertDialog.Builder(requireContext())
                    .setTitle("게시글 삭제")
                    .setMessage("이 게시글을 정말 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        postViewModel.deletePost() // ViewModel에서 삭제 요청
                        // 삭제 완료 후, 이전 화면으로 이동
                        findNavController().popBackStack()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

            binding?.btnReplyInput?.setOnClickListener {
                val replyContent = binding?.inputPostReply?.text?.toString()?.trim()

                if (!replyContent.isNullOrEmpty()) {
                    val postId = postViewModel.selectedPost.value?.postId
                    if (postId != null) {
                        // Reply 객체 생성
                        val reply = Reply(
                            replyAuthorId = "nowUser",
                            replyContent = replyContent,
                            replyTimeStamp = 0L // 타임스탬프는 ViewModel에서 설정
                        )

                        // ViewModel의 uploadReply 호출
                        postViewModel.uploadReply(postId, reply)

                        // 입력 필드 초기화
                        binding?.inputPostReply?.text?.clear()
                    } else {
                        // postId가 null인 경우 처리
                        Toast.makeText(requireContext(), "게시글 정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
