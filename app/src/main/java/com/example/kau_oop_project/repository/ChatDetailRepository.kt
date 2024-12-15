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

    suspend fun sendMessage(message: ChatMessage): Result<String> {
        Log.d("ChatDetailRepository", "sendMessage called with $message")

        return withContext(Dispatchers.IO) {
            try {
                val messageRef = messagesRef.push() // messagesRef 사용
                messageRef.setValue(message).await()
                Result.success(messageRef.key ?: "Unknown ID")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}