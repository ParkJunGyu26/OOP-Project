package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.chat.ChatMessage
import com.example.kau_oop_project.data.model.chat.ChatRoom
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

    /**
     * 사용자의 채팅방 목록을 실시간으로 조회
     * @param userId 조회할 사용자 ID
     * @return 채팅방 목록을 Flow로 반환
     */
    fun getChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = snapshot.children.mapNotNull { roomSnapshot ->
                    roomSnapshot.getValue(ChatRoom::class.java)?.let { room ->
                        room.copy(id = roomSnapshot.key ?: "")
                    }
                }.filter { it.participants.contains(userId) }
                trySend(rooms).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        roomsRef.addValueEventListener(listener)
        awaitClose { roomsRef.removeEventListener(listener) }
    }


    /**
     * 특정 채팅방의 메시지 목록을 실시간으로 조회
     * @param chatRoomId 채팅방 ID
     * @return 메시지 목록을 Flow로 반환
     */
    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = messagesRef.orderByChild("chatRoomId").equalTo(chatRoomId)

        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("ChatRepository", "Error fetching messages: ${error.message}")
            }
        }

        query.addValueEventListener(listener)

        awaitClose { query.removeEventListener(listener) }
    }

    /**
     * 새로운 메시지를 전송
     * @param message 전송할 메시지 객체
     * @return 전송 성공 여부
     */
    suspend fun sendMessage(message: ChatMessage): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val messageRef = messagesRef.push()
                messageRef.setValue(message).await()

                // 메시지를 전송한 뒤 채팅방의 마지막 메시지 업데이트
                roomsRef.child(message.chatRoomId).updateChildren(
                    mapOf(
                        "lastMessage" to message.message,
                        "lastMessageTime" to message.timestamp
                    )
                ).await()
                true
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error sending message", e)
                false
            }
        }
    }

    /**
     * 새로운 채팅방 생성
     * @param chatRoom 생성할 채팅방 정보
     * @return 생성된 채팅방의 ID, 실패 시 null
     */
    suspend fun createChatRoom(chatRoom: ChatRoom): String? {
        return withContext(Dispatchers.IO) {
            try {
                val roomRef = roomsRef.push()
                roomRef.setValue(chatRoom).await()
                roomRef.key
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error creating chat room", e)
                null
            }
        }
    }

    /**
     * 특정 채팅방에 새로운 참가자를 추가
     * @param chatRoomId 채팅방 ID
     * @param userId 추가할 사용자 ID
     * @return 추가 성공 여부
     */
    suspend fun addParticipant(chatRoomId: String, userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val participantsRef = roomsRef.child(chatRoomId).child("participants")
                val currentParticipants = participantsRef.get().await().children.mapNotNull { it.value as? String }
                if (!currentParticipants.contains(userId)) {
                    participantsRef.setValue(currentParticipants + userId).await()
                }
                true
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error adding participant", e)
                false
            }
        }
    }
}