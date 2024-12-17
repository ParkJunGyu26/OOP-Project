package com.example.kau_oop_project.ui.post.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.R
import com.example.kau_oop_project.databinding.FragmentPostListBinding
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class PostListFragment : Fragment() {

    private var binding: FragmentPostListBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FragmentPostListBinding 초기화
        binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        adapter = PostListAdapter(postViewModel) // ViewModel을 Adapter에 전달
        binding?.recyclerPosts?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerPosts?.adapter = adapter

        // btn_refresh 클릭 시 데이터 새로고침
        binding?.btnRefresh?.setOnClickListener {
            postViewModel.retrievePosts()
        }

        binding?.btnSearch?.setOnClickListener {
            val searchQuery = binding?.txtViewSearchInput?.text.toString().trim().takeIf { it.isNotEmpty() }
            // containerTags에서 모든 버튼의 텍스트 가져오기
            val tags = mutableListOf<String>()
            for (i in 0 until (binding?.containerTags?.childCount ?: 0)) {
                val tagButton = binding?.containerTags?.getChildAt(i) as? Button
                tagButton?.text?.let {
                    tags.add(it.toString()) // 버튼의 텍스트를 태그 목록에 추가
                }
            }
            Log.d("PostListFragment", "Posts search options $searchQuery $tags")
            // searchQuery와 tags가 비어 있으면 전체 게시물 조회, 아니면 필터링
            postViewModel.retrievePosts(searchInput=searchQuery, searchTags=if (tags.isEmpty()) null else tags)
        }

        binding?.btnWrite?.setOnClickListener {
            findNavController().navigate(R.id.postWriteFragment)
        }

        binding?.btnUpload?.setOnClickListener {
            Log.d("PostListFragment", "Posts search options ${userViewModel.currentUser.value}")
        }

        binding?.btnTagAdd?.setOnClickListener {
            // 다이얼로그 레이아웃을 설정
            val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_post_tag_add, null)
            val userTagInput = dialogView.findViewById<EditText>(R.id.editTextTag)
            val btnTagDone = dialogView.findViewById<Button>(R.id.btn_tag_input_complete)

            // 다이얼로그 생성
            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // 완료 버튼 클릭 시 태그 추가
            btnTagDone.setOnClickListener {
                val tag = userTagInput.text.toString()
                if (tag.isNotEmpty()) {
                    postViewModel.addTag(tag) // ViewModel에 태그 추가
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "태그를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }

        // 태그 데이터가 변경되면 UI 갱신
        postViewModel.tags.observe(viewLifecycleOwner) { tags ->
            updateTags(tags)
        }

        // 포스트 데이터가 변경되면 UI 갱신
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }


    private fun updateTags(tags: List<String>) {
        // containerTags의 모든 뷰 제거
        binding?.containerTags?.removeAllViews()

        // HorizontalScrollView 내부의 LinearLayout 가져오기
        val parentLayout = binding?.containerTags ?: return

        tags.forEach { tag ->
            // 새로운 태그 버튼 생성
            val tagButton = Button(requireContext()).apply {
                text = tag
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 여백 설정을 여러 줄로 분리
                    val leftMargin = (1 * resources.displayMetrics.density).toInt()
                    val topMargin = (3 * resources.displayMetrics.density).toInt()
                    val rightMargin = (1 * resources.displayMetrics.density).toInt()
                    val bottomMargin = (3 * resources.displayMetrics.density).toInt()
                    setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
                }

                // 태그 스타일 적용: 둥근 모양 및 색상
                setTextColor(resources.getColor(android.R.color.white, null))
                textSize = 14f // 텍스트 크기 설정
            }

            // 클릭 시 태그 삭제
            tagButton.setOnClickListener {
                postViewModel.removeTag(tag)
            }

            // LinearLayout에 버튼 추가
            parentLayout.addView(tagButton)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // binding 해제
    }
}
