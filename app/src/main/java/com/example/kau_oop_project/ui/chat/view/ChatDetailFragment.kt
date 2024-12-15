package com.example.kau_oop_project.ui.chat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val args = ChatDetailFragmentArgs.fromBundle(requireArguments())
        val chatRoomId = args.chatRoomId
        val participantUid = args.participantUid // Safe Args로 전달받은 participantUid

        // DB에서 participantUid를 통해 userName 로드
        val userRef = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("users")
            .child(participantUid)

        userRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                binding?.tvTitle?.text = userName
                binding?.name?.text = userName
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })

        setupRecyclerView()
        setupUI(chatRoomId)

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

        binding?.btnSend?.setOnClickListener {
            val message = binding?.editMessage?.text.toString()
            if (message.isNotEmpty() && senderId != null) {
                viewModel.sendMessage(chatRoomId, senderId, message)
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