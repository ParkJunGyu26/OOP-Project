package com.example.kau_oop_project.ui.chat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.R
import com.example.kau_oop_project.databinding.FragmentChatListBinding
import com.example.kau_oop_project.ui.chat.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class ChatListFragment : Fragment() {
    private var binding: FragmentChatListBinding? = null
    private lateinit var chatAdapter: ChatRoomAdapter
    private var isOpenChat: Boolean = false
    private val chatViewModel: ChatViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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

        // UserViewModel에서 현재 로그인한 사용자 정보 가져오기
        val currentUserId = userViewModel.currentUser.value?.uid
        Log.d("ChatListFragment", "Current User UID (from UserViewModel): $currentUserId")

        // currentUserId가 null이 아닌 경우에만 채팅방 로드
        if (currentUserId != null) {
            chatViewModel.loadChatRooms(currentUserId)
        }

        // ViewModel에서 LiveData 구독
        chatViewModel.chatRooms.observe(viewLifecycleOwner) { rooms ->
            Log.d("ChatListFragment", "Rooms loaded: $rooms")
            if (rooms.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
                binding?.recyclerView?.visibility = View.GONE
            } else {
                binding?.emptyView?.visibility = View.GONE
                binding?.recyclerView?.visibility = View.VISIBLE
                chatAdapter.submitList(rooms)
            }
        }
    }

    /**
     * RecyclerView 초기 설정 및 클릭 이벤트 처리
     */
    private fun setupRecyclerView() {
        chatAdapter = ChatRoomAdapter { chatRoom ->
            val chatRoomId = chatRoom.id
            val participantUid = chatRoom.participants.keys.firstOrNull() ?: "Unknown"
            val action = ChatMainFragmentDirections.actionChatMainFragmentToChatDetailFragment(chatRoomId, participantUid)
            findNavController().navigate(action)
        }

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            setHasFixedSize(true) // 성능 최적화
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.recyclerView?.adapter = null // RecyclerView Adapter 정리
        binding = null
    }
}