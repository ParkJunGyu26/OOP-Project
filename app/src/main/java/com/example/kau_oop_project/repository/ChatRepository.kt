package com.example.kau_oop_project.repository

import android.util.Log
import com.example.kau_oop_project.data.model.ChatMessage
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.data.model.Participant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository<T> {
    private val database = Firebase.database
    private val roomsRef = database.getReference("chats/rooms")
    private val messagesRef = database.getReference("chats/messages")
    private val usersRef = database.getReference("users")

    // 메시지 전송
    suspend fun sendMessage(message: ChatMessage): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val messageId = messagesRef.push().key ?: throw Exception("Failed to generate message ID")
                val roomPath = "chats/rooms/${message.chatRoomId}"

                // 메시지 데이터 및 채팅방 메타데이터 업데이트
                val updates = hashMapOf<String, Any>(
                    "chats/messages/$messageId" to message,
                    "$roomPath/lastMessage" to message.message,
                    "$roomPath/lastMessageTime" to message.timestamp,
                    "$roomPath/participantName" to message.senderId
                )

                database.reference.updateChildren(updates).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error sending message: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // 메시지 불러오기
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
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    // 채팅방 생성
    suspend fun createChatRoom(chatRoom: ChatRoom): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val roomPath = "chats/rooms/${chatRoom.id}"
                val chatRoomData = mapOf(
                    "id" to chatRoom.id,
                    "participants" to chatRoom.participants.map {
                        mapOf("email" to it.email, "name" to it.name)
                    },
                    "lastMessage" to "",
                    "lastMessageTime" to 0L
                )

                roomsRef.child(chatRoom.id).setValue(chatRoomData).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error creating chat room: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // 채팅방 목록 가져오기
    suspend fun getChatRooms(userId: String?): Result<List<ChatRoom>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = roomsRef.get().await()
                val chatRooms = snapshot.children.mapNotNull {
                    it.getValue(ChatRoom::class.java)
                }
                Result.success(chatRooms)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error fetching chat rooms: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // 사용자 이메일 로그 출력
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