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
    private val chatRooms: List<ChatRoom>,
    private val onItemClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    /**
     * 채팅방 아이템을 위한 ViewHolder 클래스
     */
    inner class ChatViewHolder(private val binding: ItemChatBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        /**
         * ChatRoom 데이터를 뷰에 바인딩하고 클릭 이벤트 설정
         */
        fun bind(chatRoom: ChatRoom) {
            binding.root.setOnClickListener { onItemClick(chatRoom) }
            binding.ivProfile.setImageResource(R.drawable.user)
            binding.tvName.text = chatRoom.participants[1]
            binding.tvMessage.text = chatRoom.lastMessage
            binding.tvTime.text = formatTime(chatRoom.lastMessageTime)
        }

        /**
         * 타임스탬프를 "HH:mm" 형식의 시간으로 변환
         */
        private fun formatTime(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }

    /**
     * ViewHolder 생성 및 바인딩 객체 초기화
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    /**
     * 지정된 위치의 데이터를 ViewHolder에 바인딩
     */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    /**
     * 전체 아이템 개수 반환
     */
    override fun getItemCount() = chatRooms.size
} 