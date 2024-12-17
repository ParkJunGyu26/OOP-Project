package com.example.kau_oop_project.ui.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.R
import com.example.kau_oop_project.data.model.chat.ChatRoom
import com.example.kau_oop_project.databinding.FragmentChatListBinding
import com.example.kau_oop_project.ui.chat.viewmodel.ChatViewModel

class ChatListFragment : Fragment() {
    private var binding: FragmentChatListBinding? = null
    private lateinit var chatAdapter: ChatRoomAdapter
    private var isOpenChat: Boolean = false
    private val chatViewModel: ChatViewModel by viewModels()

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
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater)
        return binding?.root
    }

    /**
     * View 생성 후 RecyclerView 설정 및 채팅 목록 로드
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isOpenChat = arguments?.getBoolean("isOpenChat") ?: false
        setupRecyclerView()
        loadChatList()
//        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
//        chatViewModel.loadChatRooms(currentUserId)   // Firebase에서 채팅방 불러오기
//
//        chatViewModel.chatRooms.observe(viewLifecycleOwner) { rooms ->
//            // 불러온 채팅방 목록을 어댑터에 반영
//            chatAdapter.submitList(rooms)
//        }
    }

    /**
     * RecyclerView 초기 설정 및 클릭 이벤트 처리
     */
    private fun setupRecyclerView() {
        chatAdapter = ChatRoomAdapter { chatRoom ->
            val bundle = Bundle().apply {
                // chatRoom.participants[1]이 상대방 유저 아이디라고 가정
                putString("name", chatRoom.participants[1])
            }

            findNavController().navigate(
                R.id.action_chatMainFragment_to_chatDetailFragment,
                bundle
            )
        }

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            setHasFixedSize(true) // 성능 최적화
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}