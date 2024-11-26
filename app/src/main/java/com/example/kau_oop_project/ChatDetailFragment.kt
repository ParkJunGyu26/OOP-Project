package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.databinding.FragmentChatDetailBinding
import com.example.kau_oop_project.viewmodel.ChatDetailViewModel
import com.example.kau_oop_project.viewmodel.ChatViewModel

class ChatDetailFragment : Fragment() {
    private val viewModel: ChatDetailViewModel by viewModels()
    private var binding: FragmentChatDetailBinding? = null

    /**
     * Fragment의 View를 생성하고 초기화
     * ViewBinding을 통해 레이아웃을 inflate하여 반환
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatDetailBinding.inflate(inflater)
        return binding?.root
    }

    /**
     * View 생성 완료 후 호출되어 초기 설정 수행
     * UI 요소와 첨부파일 메뉴 설정을 초기화
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupAttachmentMenu()
    }

    /**
     * 채팅방의 기본 UI 요소들을 설정
     * - 뒤로가기 버튼
     * - 채팅방 제목
     * - 메시지 전송 버튼
     */
    private fun setupUI() {
        val userName = arguments?.getString("name") ?: "채팅"
        
        binding?.apply {
            tvTitle.text = userName
            name.text = userName
        }

        binding?.btnBack?.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding?.btnSend?.setOnClickListener {
            val message = binding?.editMessage?.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding?.editMessage?.text?.clear()
            }
        }

        viewModel.messageStatus.observe(viewLifecycleOwner) { isSuccess ->
            binding?.btnSend?.apply {
                isEnabled = isSuccess  // 버튼 활성화/비활성화
                alpha = if (isSuccess) 1.0f else 0.5f  // 버튼 투명도 조절
            }
        }
    }

    /**
     * 첨부파일 메뉴 관련 기능 설정
     * 버튼 클릭 시 메뉴를 토글하고 아이콘 변경
     */
    private fun setupAttachmentMenu() {
        var isMenuOpen = false
        
        binding?.btnAdd?.setOnClickListener {
            // 상태 토글 (true → false 또는 false → true)
            isMenuOpen = !isMenuOpen

            // 메뉴 표시/숨김 설정
            binding?.attachmentMenu?.visibility = if (isMenuOpen) View.VISIBLE else View.GONE

             // 버튼 아이콘 변경 (열림/닫힘 상태에 따라)
            binding?.btnAdd?.setImageResource(if (isMenuOpen) R.drawable.down else R.drawable.add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
} 