package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.databinding.FragmentChatListBinding
import com.example.kau_oop_project.data.model.chat.ChatRoom

class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatRoomAdapter
    private var isOpenChat: Boolean = false

    /**
     * 채팅 목록 Fragment 인스턴스 생성
     * @param isOpenChat 오픈채팅 여부
     */
    companion object {
        fun newInstance(isOpenChat: Boolean): ChatListFragment {
            return ChatListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isOpenChat", isOpenChat)
                }
            }
        }
    }

    /**
     * Fragment의 View 생성 및 초기화
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View 생성 후 RecyclerView 설정 및 채팅 목록 로드
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isOpenChat = arguments?.getBoolean("isOpenChat") ?: false
        setupRecyclerView()
        loadChatList()
    }

    /**
     * RecyclerView 초기 설정 및 클릭 이벤트 처리
     */
    private fun setupRecyclerView() {
        chatAdapter = ChatRoomAdapter { chatRoom ->
            val bundle = Bundle().apply {
                putString("name", chatRoom.participants[1])
            }
            findNavController().navigate(
                R.id.action_chatMainFragment_to_chatDetailFragment,
                bundle
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * 채팅방 목록 데이터 로드
     * 일반/오픈채팅 구분하여 더미 데이터 표시
     */
    private fun loadChatList() {
        val dummyData = if (isOpenChat) {
            listOf(
                ChatRoom("3", listOf("me", "오픈채팅1"), "오픈채팅방입니다", System.currentTimeMillis()),
                ChatRoom("4", listOf("me", "오픈채팅2"), "누구나 참여가능!", System.currentTimeMillis())
            )
        } else {
            listOf(
                ChatRoom("1", listOf("me", "사용자1"), "안녕하세요!", System.currentTimeMillis()),
                ChatRoom("2", listOf("me", "사용자2"), "반갑습니다!", System.currentTimeMillis())
            )
        }
        chatAdapter.submitList(dummyData)
    }

    /**
     * Fragment 소멸 시 바인딩 정리
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 