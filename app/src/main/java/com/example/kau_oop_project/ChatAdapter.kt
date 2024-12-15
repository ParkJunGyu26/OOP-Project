package com.example.kau_oop_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.data.model.chat.ChatRoom
import com.example.kau_oop_project.databinding.ItemChatBinding

/**
 * 채팅방 목록을 표시하기 위한 RecyclerView 어댑터
 */
class ChatAdapter(
    private var chatRooms: MutableList<ChatRoom>,
    private val onItemClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoom: ChatRoom) {
            binding.root.setOnClickListener { onItemClick(chatRoom) }
            binding.ivProfile.setImageResource(R.drawable.user)
            binding.tvName.text = chatRoom.participants.getOrNull(1) ?: "Unknown"
            binding.tvMessage.text = chatRoom.lastMessage
            binding.tvTime.text = formatTime(chatRoom.lastMessageTime)
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    override fun getItemCount() = chatRooms.size

    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms.clear()
        chatRooms.addAll(newChatRooms)
        notifyDataSetChanged()
    }
}