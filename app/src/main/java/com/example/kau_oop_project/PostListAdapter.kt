package com.example.kau_oop_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.databinding.ItemPostBinding
import com.example.kau_oop_project.data.model.Post

class PostListAdapter : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    private var posts: List<Post> = emptyList() // 초기화된 리스트

    inner class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTitle.text = post.postTitle
            binding.postTag.text = post.postTag
            binding.postImageType.setImageResource(R.drawable.ic_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        posts.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    // submitList를 호출하면 리스트가 변경되며 RecyclerView가 자동으로 갱신됨
    fun submitList(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged() // 리스트가 갱신되었을 때 어댑터에 알림
    }
}
