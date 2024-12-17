package com.example.kau_oop_project.ui.post.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.Reply
import com.example.kau_oop_project.data.model.User
import com.example.kau_oop_project.databinding.FragmentPostDetailBinding
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel
import com.example.kau_oop_project.ui.post.view.ReplyAdapter
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class PostDetailFragment : Fragment() {

    private var binding: FragmentPostDetailBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: ReplyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FragmentPostDetailBinding 초기화
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 댓글 RecyclerView 설정
        adapter = ReplyAdapter(userViewModel,postViewModel)
        binding?.replyList?.layoutManager = LinearLayoutManager(context)
        binding?.replyList?.adapter = adapter

        postViewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            when {
                result.isSuccess -> {
                    Toast.makeText(requireContext(), "작업이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
                result.isFailure -> {
                    val exception = result.exceptionOrNull()
                    Toast.makeText(requireContext(), "${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 선택된 게시물 데이터 관찰 및 UI 업데이트
        postViewModel.nowPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                // 댓글을 로드
                postViewModel.retrieveReplies(it.postId)
                loadPost(it.postId)

                // 게시물 작성자 정보를 얻기 위해 userViewModel에 요청
                val postAuthorId = it.postAuthorId
                userViewModel.getUsers(listOf(postAuthorId), 1) // 게시물 작성자 정보 가져오기

                // 댓글 작성자 정보 로드 (중복 제거)
                val replyAuthorIds = postViewModel.replies.value?.map { reply->reply.replyAuthorId }?.distinct() ?: emptyList()
                userViewModel.getUsers(replyAuthorIds, 2) // 댓글 작성자 정보 가져오기
            }
            Log.d("PostDetailFragment", "RRRRRRR : ${postViewModel.replies.value}")

            // 필요한 uid들 저장
            val currentUserId = userViewModel.currentUser.value
            val postAuthorId = post?.postAuthorId

            // userViewModel의 currentUser와 postViewModel의 nowPost의 postAuthorId 비교하여 버튼 가시성 설정
            if (currentUserId == postAuthorId) {
                // 현재 사용자와 게시물 작성자가 같을 때 버튼 보이게
                binding?.btnDelete?.visibility = View.VISIBLE
                binding?.btnFix?.visibility=View.VISIBLE
            } else {
                // 현재 사용자와 게시물 작성자가 다를 때 버튼 숨기기
                binding?.btnDelete?.visibility = View.GONE
                binding?.btnFix?.visibility=View.GONE
            }
        }

        // 댓글 리스트 변경을 관찰
        postViewModel.replies.observe(viewLifecycleOwner) { replies ->
            replies?.let { reply ->
                val replyAuthorIds = postViewModel.replies.value?.map { it.replyAuthorId } ?: emptyList()
                userViewModel.getUsers(replyAuthorIds.distinct(),2)
                adapter.updateReplyList(reply) // 댓글 리스트를 어댑터에 전달
            }
        }

        // 댓글 입력 버튼 클릭 리스너 설정
        binding?.btnReplyInput?.setOnClickListener {
            // 댓글 내용 가져오기
            val replyContent = binding?.inputPostReply?.text?.toString()?.trim() ?: ""

            // 댓글이 작성되었을 때
            userViewModel.currentUser.value?.let { user ->
                // 댓글 업로드 요청
                postViewModel.uploadReply(replyContent, user)
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

        binding?.btnRecommend?.setOnClickListener {
            userViewModel.currentUser.value?.let {
                postViewModel.incrementRecommendCount(it)
            }
        }

        binding?.btnFix?.setOnClickListener{
            findNavController().navigate(R.id.postWriteFragment)
        }
    }

    private fun loadPost(postId: String) {
        // 게시글 상세 정보 로드
        postViewModel.nowPost.value?.let { post ->
            binding?.apply {
                // 게시물 상세 정보 설정
                val postUserName=userViewModel.postUsersInfoList.value?.get(post.postAuthorId)?.name
                val postUserProfileImageUrl=userViewModel.postUsersInfoList.value?.get(post.postAuthorId)?.profileImage
                postDetailTitle.text = "[ ${post.postTitle} ]"
                postDetailTag.text = post.postTag
                postDetailAuthorProfile
                binding?.let{
                    Glide.with(it.postDetailAuthorProfile.context)
                        .load(postUserProfileImageUrl) // 이미지 URL
                        .error(R.drawable.default_image) // 로드 실패 시 보여줄 기본 이미지
                        .into(it.postDetailAuthorProfile)
                }
                postDetailAuthor.text = postUserName
                postDetailTimeStamp.text = "작성 시간 ${postViewModel.formatTime(post.postTimeStamp)}"
                postDetailRecommendCount.text = "추천 ${post.postRecommendCount}"
                postDetailViewCount.text = "조회수 ${post.postViewCount}"

                // 게시물 내용 표시
                if (post.postContent.isNotEmpty()) {
                    postDetailContent.text = post.postContent.joinToString("\n") { content -> content.content }
                } else {
                    postDetailContent.text = "내용이 없습니다."
                }
            }
        } ?: run {
            // 게시글 데이터가 없을 경우 처리
            Toast.makeText(requireContext(), "게시글 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // binding 해제
    }
}
