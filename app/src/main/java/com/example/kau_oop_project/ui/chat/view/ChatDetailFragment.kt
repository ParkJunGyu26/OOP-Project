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
        Log.d("ChatDetailFragment", "participantUid: $participantUid")

        viewModel.loadParticipantInfo(participantUid)

        viewModel.participantName.observe(viewLifecycleOwner) { name ->
            binding?.tvTitle?.text = name
            binding?.name?.text = name
        }

        setupRecyclerView()
        setupMessageInput(chatRoomId)

        viewModel.loadMessages(chatRoomId)

        binding?.btnAdd?.setOnClickListener {
            val menu = binding?.attachmentMenu
            menu?.visibility = if (menu?.visibility == View.GONE) View.VISIBLE else View.GONE
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

    private fun setupMessageInput(chatRoomId: String) {
        val senderId = userViewModel.currentUser.value?.uid
        Log.d("ChatDetailFragment", "setupUI: senderId=$senderId")

        binding?.editMessage?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                sendMessage(chatRoomId, senderId)
                true
            } else {
                false
            }
        }

        binding?.btnSend?.setOnClickListener {
            sendMessage(chatRoomId, senderId)
        }
    }

    private fun sendMessage(chatRoomId: String, senderId: String?) {
        val messageText = binding?.editMessage?.text.toString()
        if (messageText.isNotEmpty() && senderId != null) {
            viewModel.sendMessage(chatRoomId, senderId, messageText)
            binding?.editMessage?.text?.clear()
        } else {
            Log.e("ChatDetailFragment", "Sender ID is null or message is empty")
            Toast.makeText(requireContext(), "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
