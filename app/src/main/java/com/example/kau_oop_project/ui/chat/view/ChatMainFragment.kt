package com.example.kau_oop_project.ui.chat.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kau_oop_project.databinding.FragmentChatMainBinding
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.ui.chat.viewmodel.ChatViewModel
import androidx.fragment.app.activityViewModels
import android.widget.EditText
import android.widget.Toast
import com.example.kau_oop_project.R
import com.example.kau_oop_project.ui.user.viewmodel.RegisterViewModel
import com.example.kau_oop_project.repository.UserRepository
import com.example.kau_oop_project.repository.ChatRepository
import androidx.lifecycle.lifecycleScope
import com.example.kau_oop_project.data.model.Participant
import kotlinx.coroutines.launch
import java.util.UUID

class ChatMainFragment : Fragment() {
    private var binding: FragmentChatMainBinding? = null
    private lateinit var pagerAdapter: ChatPagerAdapter
    private val viewModel: ChatViewModel by activityViewModels()
    private val userRepository = UserRepository()

    /**
     * Fragment의 View를 생성하고 초기화
     * ViewBinding을 통해 레이아웃 inflate
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatMainBinding.inflate(inflater)
        return binding?.root
    }

    /**
     * View가 생성된 후 호출되어 초기 설정을 수행
     * ViewPager와 버튼들의 설정을 초기화
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupButtons()

        // 이메일 로그 출력
        logExistingEmails()

        binding?.btnCreateChatRoom?.setOnClickListener {
            showCreateChatRoomDialog()
        }
    }

    private fun logExistingEmails() {
        viewLifecycleOwner.lifecycleScope.launch {
            ChatRepository<Any?>().logExistingEmails()
        }
    }

    private fun showCreateChatRoomDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_chat_room, null)
        builder.setView(dialogView)

        val emailInput = dialogView.findViewById<EditText>(R.id.editTextEmail)
        builder.setTitle("채팅방 생성")
            .setPositiveButton("채팅방 생성") { dialog, _ ->
                val email = emailInput.text.toString().trim()
                if (email.isNotEmpty()) {
                    checkUserAndCreateChatRoom(email)
                } else {
                    showToast("이메일을 입력해주세요.")
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun checkUserAndCreateChatRoom(email: String) {
        if (!isValidEmail(email)) {
            showToast("유효하지 않은 이메일입니다.")
            return
        }

        val emailKey = email.replace(".", ",") // '.'을 ','로 치환

        viewLifecycleOwner.lifecycleScope.launch {
            val userSnapshot = userRepository.findUserByEmail(email)
            if (userSnapshot != null && userSnapshot.exists()) {
                val chatRoomId = UUID.randomUUID().toString()
                val chatRoom = ChatRoom(
                    id = chatRoomId,
                    participants = listOf(Participant(email = email, name = userSnapshot.child("name").getValue(String::class.java) ?: "Unknown"))
                )
                viewModel.createChatRoom(chatRoom)
                viewModel.operationResult.observe(viewLifecycleOwner) { success ->
                    if (success) {
                        showToast("채팅방이 생성되었습니다.")
                    } else {
                        showToast("채팅방 생성에 실패했습니다.")
                    }
                }
            } else {
                showToast("해당 이메일의 사용자가 존재하지 않습니다.")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * ViewPager2 초기 설정
     * 채팅 화면 어댑터 연결 및 스와이프 기능 비활성화
     */
    private fun setupViewPager() {
        pagerAdapter = ChatPagerAdapter(this)
        binding?.viewPager?.apply {
            adapter = pagerAdapter
            isUserInputEnabled = false  // 스와이프 비활성화
        }
    }

    /**
     * 일반 채팅과 오픈 채팅 전환 기능 구현
     */
    private fun setupButtons() {
        binding?.btnChat?.setOnClickListener {
            binding?.viewPager?.currentItem = 0
        }

        binding?.btnOpenChat?.setOnClickListener {
            binding?.viewPager?.currentItem = 1
        }
    }

    /**
     * Fragment 제거 시 메모리 누 방지를 위한 정리 작업
     * binding 참조 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}