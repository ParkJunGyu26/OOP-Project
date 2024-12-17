package com.example.kau_oop_project.ui.chat.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.ChatRoom
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

            // participants Map의 첫 번째 키 가져오기 (예: 사용자 ID)
            val participantName = chatRoom.participants.firstOrNull()?.name ?: "Unknown"
            binding.tvName.text = participantName

            // 마지막 메시지와 시간 바인딩
            binding.tvMessage.text = chatRoom.lastMessage.ifEmpty { "메시지가 없습니다." }
            binding.tvTime.text = formatTime(chatRoom.lastMessageTime) //
        }

        private fun formatTime(timestamp: Long?): String {
            if (timestamp == null || timestamp == 0L) {
                return "시간 없음"
            }
            val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
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
        Log.d("ChatAdapter", "Updated chatRooms: $newChatRooms")
        chatRooms.clear()
        chatRooms.addAll(newChatRooms)
        notifyDataSetChanged()
    }
}