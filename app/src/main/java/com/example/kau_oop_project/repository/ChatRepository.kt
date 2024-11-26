package com.example.kau_oop_project.repository

import com.example.kau_oop_project.data.model.chat.ChatMessage
import com.example.kau_oop_project.data.model.chat.ChatRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await

/**
 * 채팅 관련 데이터를 Firebase Realtime Database와 연동하여 처리하는 Repository
 */
class ChatRepository {
    // Firebase 데이터베이스 참조
    val database = Firebase.database
    val roomsRef = database.getReference("chats/rooms")
    val messagesRef = database.getReference("chats/messages")

    /**
     * 사용자의 채팅방 목록을 조회
     * @param userId 조회할 사용자 ID
     * @return 채팅방 목록을 Flow로 반환
     */
    suspend fun getChatRooms(userId: String?): Flow<List<ChatRoom>> = flow {
        withContext(Dispatchers.IO) {
            try {
                if (userId != null) {
                    val snapshot = suspendCoroutine<DataSnapshot> { continuation ->
                        roomsRef.orderByChild("participants/$userId").equalTo(true)
                            .get()
                            .addOnSuccessListener { continuation.resume(it) }
                            .addOnFailureListener { throw it }
                    }
                    val rooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                    emit(rooms)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
                // 로그 처리나 에러 핸들링 추가 가능
            }
        }
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회
     * @param chatRoomId 채팅방 ID
     * @return 메시지 목록을 Flow로 반환
     */
    suspend fun getMessages(chatRoomId: String?): Flow<List<ChatMessage>> = flow {
        withContext(Dispatchers.IO) {
            try {
                if (chatRoomId != null) {
                    val snapshot = suspendCoroutine<DataSnapshot> { continuation ->
                        messagesRef.orderByChild("chatRoomId").equalTo(chatRoomId)
                            .get()
                            .addOnSuccessListener { continuation.resume(it) }
                            .addOnFailureListener { throw it }
                    }
                    val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    emit(messages)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
                // 로그 처리나 에러 핸들링 추가 가능
            }
        }
    }

    /**
     * 새로운 메시지를 전송
     * @param message 전송할 메시지 객체
     * @return 전송 성공 여부
     */
    suspend fun sendMessage(message: ChatMessage?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (message != null) {
                    val messageRef = messagesRef.push()
                    messageRef.setValue(message).await()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 새로운 채팅방 생성
     * @param chatRoom 생성할 채팅방 정보
     * @return 생성된 채팅방의 ID, 실패시 null
     */
    suspend fun createChatRoom(chatRoom: ChatRoom?): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (chatRoom != null) {
                    val roomRef = roomsRef.push()
                    roomRef.setValue(chatRoom).await()
                    roomRef.key
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}