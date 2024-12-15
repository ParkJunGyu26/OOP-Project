package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kau_oop_project.databinding.FragmentChatDetailBinding
import com.example.kau_oop_project.viewmodel.ChatDetailViewModel

class ChatDetailFragment : Fragment() {

    private val viewModel: ChatDetailViewModel by viewModels()
    private var binding: FragmentChatDetailBinding? = null
    private lateinit var messageAdapter: ChatMessageAdapter
    private var participantName: String? = null

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

        participantName = arguments?.getString("name")

        binding?.tvTitle?.text = participantName ?: "알 수 없음"
        binding?.name?.text = participantName ?: "알 수 없음"

        setupRecyclerView()
        setupUI()

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.setMessages(messages)
        }

        viewModel.messageStatus.observe(viewLifecycleOwner) { isSuccess ->
            Toast.makeText(
                requireContext(),
                if (isSuccess) "Message sent!" else "Failed to send message.",
                Toast.LENGTH_SHORT
            ).show()
        }

        val chatRoomId = "room1"
        viewModel.loadMessages(chatRoomId)
    }

    private fun setupRecyclerView() {
        val currentUserId = "user1"
        messageAdapter = ChatMessageAdapter(currentUserId)

        binding?.recyclerMessages?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun setupUI() {
        binding?.btnSend?.setOnClickListener {
            val message = binding?.editMessage?.text.toString()
            if (message.isNotEmpty()) {
                val chatRoomId = "room1"
                val senderId = "user1"
                viewModel.sendMessage(chatRoomId, senderId, message)
                binding?.editMessage?.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}