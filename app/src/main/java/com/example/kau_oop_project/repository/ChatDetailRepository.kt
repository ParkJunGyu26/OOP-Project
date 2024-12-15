package com.example.kau_oop_project.repository

import android.util.Log
import com.example.kau_oop_project.data.model.chat.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatDetailRepository {

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("chats/messages")

    /**
     * 특정 채팅방의 메시지 목록을 가져오는 함수
     * @param chatRoomId 채팅방 ID
     * @return 메시지 목록을 Flow로 반환 (예: callbackFlow 사용)
     */
    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = messagesRef.orderByChild("chatRoomId").equalTo(chatRoomId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                println("Loaded Messages: $messages") // 로그 추가
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase Error: ${error.message}") // Firebase 에러 확인
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    /**
     * 새 메시지를 채팅방에 전송하는 함수
     * @param message 전송할 메시지
     * @return 전송 성공 여부
     */
    suspend fun sendMessage(message: ChatMessage): Result<String> = withContext(Dispatchers.IO) {
        try {
            val newMessageRef = messagesRef.push()
            newMessageRef.setValue(message).await()
            Result.success(newMessageRef.key ?: "Unknown ID")
        } catch (e: Exception) {
            Log.e("ChatDetailRepository", "Error sending message", e)
            Result.failure(e)
        }
    }
}