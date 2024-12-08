package com.example.kau_oop_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.data.model.chat.ChatRoom
import com.example.kau_oop_project.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 채팅방 목록을 표시하기 위한 RecyclerView 어댑터
 * @param onItemClick 채팅방 클릭 시 실행될 콜백
 */
class ChatRoomAdapter(
    private val onItemClick: (ChatRoom) -> Unit
) : ListAdapter<ChatRoom, ChatRoomAdapter.ViewHolder>(ChatRoomDiffCallback()) {

    /**
     * ViewHolder 생성 및 바인딩 객체 초기화
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * 지정된 위치의 데이터를 ViewHolder에 바인딩
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * ViewHolder 초기화 및 클릭 이벤트 설정
         */
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        /**
         * ChatRoom 데이터를 뷰에 바인딩
         * @param chatRoom 표시할 채팅방 정보
         */
        fun bind(chatRoom: ChatRoom) {
            binding.apply {
                tvName.text = chatRoom.participants[1]
                tvMessage.text = chatRoom.lastMessage
                tvTime.text = formatTime(chatRoom.lastMessageTime)
            }
        }

        /**
         * 타임스탬프를 시간 형식(HH:mm)으로 변환
         * @param timestamp 변환할 타임스탬프
         * @return 형식화된 시간 문자열
         */
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}

/**
 * 채팅방 목록의 변경사항을 감지하는 DiffUtil 콜백
 */
private class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem == newItem
    }
} 