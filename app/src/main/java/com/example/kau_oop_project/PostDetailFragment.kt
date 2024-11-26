package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kau_oop_project.databinding.FragmentPostDetailBinding
import com.example.kau_oop_project.viewmodel.LoginViewModel
import com.example.kau_oop_project.viewmodel.PostViewModel
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
                    postDetailAuthor.text = it.postAuthor.name
                    postDetailTimeStamp.text = "작성 시간 ${formatTimestamp(it.postTimeStamp)}"
                    postDetailRecommendCount.text = "추천 ${it.postRecommendCount.toString()}"
                    postDetailViewCount.text="조회수 ${it.postViewCount}"
                    postDetailReplyCount.text = "댓글 ${it.postReplyList.size.toString()}"

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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
