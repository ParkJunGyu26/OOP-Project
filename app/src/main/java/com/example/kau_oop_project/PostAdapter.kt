package com.example.kau_oop_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList: List<String>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_post_item_posting, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.titleText.text = postList[position]
    }

    override fun getItemCount() = postList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
    }
}
