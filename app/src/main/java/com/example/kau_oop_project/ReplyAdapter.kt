package com.example.kau_oop_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReplyAdapter(private val replyList: List<Pair<String, String>>) :
    RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_reply, parent, false)
        return ReplyViewHolder(view)
    }
    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val (name, comment) = replyList[position]
        holder.nameText.text = name
        holder.commentText.text = comment
    }
    override fun getItemCount() = replyList.size
    class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.reply_user_name)
        val commentText: TextView = itemView.findViewById(R.id.reply_content)
    }
}
