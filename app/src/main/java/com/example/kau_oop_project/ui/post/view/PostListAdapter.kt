package com.example.kau_oop_project.ui.post.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.databinding.ItemPostBinding
import com.example.kau_oop_project.data.model.Post
import com.example.kau_oop_project.R
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class PostListAdapter(private val postViewModel: PostViewModel) : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    private var posts: List<Post> = emptyList()

    inner class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTitle.text = post.postTitle
            binding.postTag.text = "[ ${post.postTag} ]"
            binding.postImageType.setImageResource(R.drawable.ic_image)
            binding.postViewTime.text="조회 ${post.postViewCount.toString()}"
            binding.postRecommendTime.text="추천 ${post.postRecommendCount.size}"

            itemView.setOnClickListener {
                // 게시물 선택 시 ViewModel에 저장
                postViewModel.incrementViewCount(post.postId)
                postViewModel.retrievePostById(post.postId)
                // PostDetailFragment로 이동
                itemView.findNavController().navigate(R.id.postDetailFragment)
            }
        }
    }

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged() // 데이터 변경 시 UI 갱신
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}