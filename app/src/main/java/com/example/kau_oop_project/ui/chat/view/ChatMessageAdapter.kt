package com.example.kau_oop_project.ui.chat.view

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.chat.ChatMessage

class ChatMessageAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        println("Adapter Received Messages: ${newMessages.size}, Data: $newMessages") // Adapter 데이터 확인
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message_sent)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp_sent)

        fun bind(chatMessage: ChatMessage) {
            tvMessage.text = chatMessage.message
            tvTimestamp.text = DateFormat.format("hh:mm a", chatMessage.timestamp)
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message_received)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp_received)

        fun bind(chatMessage: ChatMessage) {
            tvMessage.text = chatMessage.message
            tvTimestamp.text = DateFormat.format("hh:mm a", chatMessage.timestamp)
        }
    }
}