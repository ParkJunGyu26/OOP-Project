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
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class ChatListFragment : Fragment() {
    private var binding: FragmentChatListBinding? = null
    private val chatViewModel: ChatViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatRoomAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        // 사용자 ID를 가져와서 채팅방 목록을 로드합니다.
        val userId = userViewModel.currentUser.value?.uid
        chatViewModel.loadChatRooms(userId) // 채팅방 목록 로드 호출
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatRoomAdapter { chatRoom ->
            val chatRoomId = chatRoom.id
            val participantName = chatRoom.participants.firstOrNull()?.name ?: "Unknown"
            val action = ChatMainFragmentDirections.actionChatMainFragmentToChatDetailFragment(chatRoomId, participantName)
            findNavController().navigate(action)
        }
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }
    private fun observeViewModel() {
        chatViewModel.chatRooms.observe(viewLifecycleOwner) { rooms ->
            Log.d("ChatListFragment", "Loaded chat rooms: $rooms")
            chatAdapter.submitList(rooms)
            binding?.emptyView?.visibility = if (rooms.isEmpty()) View.VISIBLE else View.GONE
            binding?.recyclerView?.visibility = if (rooms.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}