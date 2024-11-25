package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.databinding.FragmentPostListBinding
import com.example.kau_oop_project.viewmodel.PostViewModel
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.data.model.PostContent
import com.example.kau_oop_project.data.model.ContentType
import com.example.kau_oop_project.data.model.user

class PostListFragment : Fragment() {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!
    private lateinit var postViewModel: PostViewModel
    private lateinit var adapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // RecyclerView 설정
        adapter = PostListAdapter() // LiveData를 직접 어댑터에 전달하지 않고, ViewModel에서 가져옴
        binding.recyclerPosts.layoutManager = LinearLayoutManager(context)
        binding.recyclerPosts.adapter = adapter

        // 포스트 데이터 가져오기
        postViewModel.retrievePosts()

        // 포스트 데이터가 변경되면 UI 갱신
        postViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            posts?.let {
                adapter.submitList(it) // LiveData가 변할 때마다 어댑터에 리스트 업데이트
            }
        })

        // btn_refresh 클릭 시 데이터 새로고침
        binding.btnRefresh.setOnClickListener {
            postViewModel.retrievePosts() // 데이터 갱신
        }

        // btn_upload 클릭 시 새로운 포스트 업로드
        binding.btnUpload.setOnClickListener {
            val newPost = Post(
                postId = "", // Firebase에서 자동 생성된 ID로 처리됨
                postDBId = "", // 필요시 DB ID로 처리 가능
                postTag = "Sample Tag",
                postTitle = "Sample Post",
                postAuthor = user("admin", "admin@naver.com", "KAU", "김어드민", "관리자"),
                postContent = PostContent(ContentType.TEXT, "Sample content"),
                postReplyList = null,
                timeStamp = System.currentTimeMillis()
            )
            postViewModel.uploadPost(newPost) // 새로운 포스트 업로드
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
