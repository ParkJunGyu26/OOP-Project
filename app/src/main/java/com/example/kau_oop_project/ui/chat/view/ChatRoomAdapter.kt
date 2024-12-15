package com.example.kau_oop_project.ui.chat.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.databinding.ItemChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
            binding.root.setOnClickListener { onItemClick(chatRoom) }
            binding.ivProfile.setImageResource(R.drawable.user)

            val participantUid = chatRoom.participants.keys.firstOrNull() ?: "Unknown"

            // DB에서 uid에 해당하는 user 정보 가져오기
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(participantUid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    binding.tvName.text = userName
                }

                override fun onCancelled(error: DatabaseError) {
                    // 에러 처리 로직
                }
            })

            binding.tvMessage.text = chatRoom.lastMessage
            binding.tvTime.text = formatTime(chatRoom.lastMessageTime)
        }

        /**
         * 타임스탬프를 시간 형식(HH:mm)으로 변환
         * @param timestamp 변환할 타임스탬프
         * @return 형식화된 시간 문자열
         */
        private fun formatTime(timestamp: Long): String {
            // timestamp가 0일 경우 예외 처리
            if (timestamp == 0L) return ""
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
        // lastMessage나 lastMessageTime이 변경되면 false 반환
        return oldItem.id == newItem.id &&
                // oldItem.participants == newItem.participants &&
                oldItem.lastMessage == newItem.lastMessage &&
                oldItem.lastMessageTime == newItem.lastMessageTime
    }
}