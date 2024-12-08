package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.databinding.FragmentPostListBinding
import com.example.kau_oop_project.viewmodel.PostViewModel
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.databinding.FragmentPostDetailBinding

class PostListFragment : Fragment() {

    private var binding: FragmentPostListBinding? = null
    private val postViewModel: PostViewModel by activityViewModels()
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

        binding?.btnWrite?.setOnClickListener {
            findNavController().navigate(R.id.postWriteFragment)
        }

        // 포스트 데이터 가져오기
        postViewModel.retrievePosts()

        // 포스트 데이터가 변경되면 UI 갱신
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            Toast.makeText(context, "데이터가 변경되었습니다. 총 ${posts.size}개의 게시물이 있습니다.", Toast.LENGTH_SHORT).show()
            adapter.updatePosts(posts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // binding 해제
    }
}
