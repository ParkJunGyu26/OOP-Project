package com.example.kau_oop_project.ui.chat.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomAdapter(private val onItemClick: (ChatRoom) -> Unit) : ListAdapter<ChatRoom, ChatRoomAdapter.ViewHolder>(ChatRoomDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(chatRoom: ChatRoom) {
            // 데이터 바인딩
            // name은 user에서 불러와야 함
            binding.tvMessage.text = chatRoom.lastMessage
            
            // 시간 포맷팅
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 원하는 형식으로 변경
            val formattedTime = dateFormat.format(Date(chatRoom.lastMessageTime))
            binding.tvTime.text = formattedTime
        }
    }
}

private class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem == newItem
    }
}