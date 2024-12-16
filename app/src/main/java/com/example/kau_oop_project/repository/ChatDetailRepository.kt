package com.example.kau_oop_project.repository

import android.util.Log
import com.example.kau_oop_project.data.model.ChatMessage
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
    private val chatRoomsRef = database.getReference("chats").child("rooms")
    data class ChatResponse<T>(
        val data: T? = null,
        val error: String? = null
    )

    suspend fun sendMessage(message: ChatMessage): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val messageId = messagesRef.push().key ?: throw Exception("Failed to generate message ID")

                // 메시지 저장
                val updates = hashMapOf<String, Any>(
                    "chats/messages/$messageId" to message,
                    "chats/rooms/${message.chatRoomId}/lastMessage" to message.message,
                    "chats/rooms/${message.chatRoomId}/lastMessageTime" to message.timestamp
                )

                database.reference.updateChildren(updates).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ChatDetailRepository", "Error sending message: ${e.message}")
                Result.failure(e)
            }
        }
    }


    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = messagesRef.orderByChild("chatRoomId").equalTo(chatRoomId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                println("Loaded Messages: $messages")
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase Error: ${error.message}")
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getUserInfo(participantUid: String): Flow<ChatRoom> = callbackFlow {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(participantUid)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomId = snapshot.child("chatRoomId").getValue(String::class.java)
                if (chatRoomId != null) {
                    val chatRoomRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId)
                    chatRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(chatRoomSnapshot: DataSnapshot) {
                            val chatRoom = chatRoomSnapshot.getValue(ChatRoom::class.java)
                            if (chatRoom != null) {
                                trySend(chatRoom).isSuccess
                            } else {
                                trySend(ChatRoom(id = "Unknown")).isSuccess
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            close(error.toException())
                        }
                    })
                } else {
                    trySend(ChatRoom(id = "Unknown")).isSuccess
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addListenerForSingleValueEvent(listener)
        awaitClose { userRef.removeEventListener(listener) }
    }

    data class ChatRoom(val id: String)
}