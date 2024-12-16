package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.ChatMessage
import com.example.kau_oop_project.data.model.ChatRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class ChatRepository {
    private val database = Firebase.database
    private val roomsRef = database.getReference("chats/rooms")
    private val messagesRef = database.getReference("chats/messages")
    private val usersRef = database.getReference("users")

    suspend fun sendMessage(message: ChatMessage): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 기존 메시지 ID를 사용하여 메시지 저장
                val messageId = message.id.ifEmpty { messagesRef.push().key ?: throw Exception("Failed to generate message ID") }

                // 트랜잭션으로 처리하여 동시성 문제 방지
                val updates = hashMapOf<String, Any>(
                    "chats/messages/$messageId" to message,
                    "chats/rooms/${message.chatRoomId}/lastMessage" to message.message,
                    "chats/rooms/${message.chatRoomId}/lastMessageTime" to message.timestamp,
                    "chats/rooms/${message.chatRoomId}/participantName" to message.senderId
                )

                // 기존 채팅방의 정보를 업데이트
                database.reference.updateChildren(updates).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error sending message: ${e.message}")
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
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRepository", "Error fetching messages: ${error.message}")
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun createChatRoom(chatRoom: ChatRoom): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val roomRef = roomsRef.push()
                roomRef.setValue(chatRoom).await()
                Result.success(roomRef.key ?: throw Exception("Failed to create chat room"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addParticipant(chatRoomId: String, userId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val participantsRef = roomsRef.child(chatRoomId).child("participants")
                val currentParticipants = participantsRef.get().await().children.mapNotNull { it.value as? String }
                if (!currentParticipants.contains(userId)) {
                    participantsRef.setValue(currentParticipants + userId).await()
                }
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserName(uid: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = usersRef.child(uid).child("name").get().await()
                Result.success(snapshot.getValue(String::class.java) ?: "Unknown")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getChatRooms(userId: String?): Result<List<ChatRoom>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = roomsRef.get().await()
                val chatRooms = snapshot.children.mapNotNull {
                    Log.d("ChatRepository", "ChatRoom Data: ${it.value}")
                    it.getValue(ChatRoom::class.java)
                }
                Log.d("ChatRepository", "Fetched ChatRooms: $chatRooms")
                Result.success(chatRooms)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error fetching chat rooms: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun logExistingEmails() {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = usersRef.get().await()
                for (user in snapshot.children) {
                    val email = user.child("userEmail").getValue(String::class.java)
                    Log.d("EmailLog", "Existing email: $email")
                }
            } catch (e: Exception) {
                Log.e("EmailLog", "Error fetching emails: ${e.message}")
            }
        }
    }
}