package com.example.kau_oop_project.ui.chat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.databinding.FragmentChatDetailBinding
import com.example.kau_oop_project.ui.chat.viewmodel.ChatDetailViewModel
import com.example.kau_oop_project.data.model.ChatMessage
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel

class ChatDetailFragment : Fragment() {

    private val viewModel: ChatDetailViewModel by viewModels()
    private var binding: FragmentChatDetailBinding? = null
    private lateinit var messageAdapter: ChatMessageAdapter
    private var participantName: String? = null
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val args = ChatDetailFragmentArgs.fromBundle(requireArguments())
        val chatRoomId = args.chatRoomId
        val participantUid = args.participantUid
        Log.d("ChatDetailFragment", "participantUid: $participantUid") // 상대방 이메일 주소

        // ViewModel을 통해 참가자 정보 로드
        viewModel.loadParticipantInfo(participantUid)

        viewModel.participantName.observe(viewLifecycleOwner) { name ->
            Log.d("ChatDetailFragment", "participantName: $name")
            binding?.tvTitle?.text = name
            binding?.name?.text = name
        }

        setupRecyclerView()
        setupUI(chatRoomId)

        // 메시지 로딩
        viewModel.loadMessages(chatRoomId)

        binding?.btnAdd?.setOnClickListener {
            // attachment_menu의 현재 상태 확인
            val menu = binding?.attachmentMenu
            if (menu?.visibility == View.GONE) {
                menu.visibility = View.VISIBLE
            } else {
                menu?.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.setMessages(messages)
            binding?.recyclerMessages?.scrollToPosition(messages.size - 1)
        }

        viewModel.messageStatus.observe(viewLifecycleOwner) { isSuccess ->
            Toast.makeText(
                requireContext(),
                if (isSuccess) "Message sent!" else "Failed to send message.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = userViewModel.currentUser.value?.uid ?: "defaultUserId"
        messageAdapter = ChatMessageAdapter(currentUserId)

        binding?.recyclerMessages?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun setupUI(chatRoomId: String) {
        val senderId = userViewModel.currentUser.value?.uid
        Log.d("ChatDetailFragment", "setupUI: senderId=$senderId")

        // EditText에 키 이벤트 리스너 추가
        binding?.editMessage?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Enter 키가 눌렸을 때 메시지 전송
                val messageText = binding?.editMessage?.text.toString()
                if (messageText.isNotEmpty() && senderId != null) {
                    val chatMessage = ChatMessage(
                        id = "",
                        chatRoomId = chatRoomId,
                        senderId = senderId,
                        message = messageText,
                        timestamp = System.currentTimeMillis()
                    )
                    viewModel.sendMessage(chatRoomId, senderId, messageText)
                    binding?.editMessage?.text?.clear()
                }
                true // 이벤트 소비
            } else {
                false // 다른 키 이벤트는 정상 처리
            }
        }

        binding?.btnSend?.setOnClickListener {
            val messageText = binding?.editMessage?.text.toString()
            if (messageText.isNotEmpty() && senderId != null) {
                val chatMessage = ChatMessage(
                    id = "",
                    chatRoomId = chatRoomId,
                    senderId = senderId,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
                viewModel.sendMessage(chatRoomId, senderId, messageText)
                binding?.editMessage?.text?.clear()
            } else {
                Log.e("ChatDetailFragment", "Sender ID is null or message is empty")
                Toast.makeText(requireContext(), "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}