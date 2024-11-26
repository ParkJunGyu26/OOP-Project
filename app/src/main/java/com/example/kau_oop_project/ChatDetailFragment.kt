package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.databinding.FragmentChatDetailBinding

class ChatDetailFragment : Fragment() {
    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    /**
     * Fragment의 View를 생성하고 초기화
     * ViewBinding을 통해 레이아웃을 inflate하여 반환
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.tvTitle.text = arguments?.getString("name") ?: "채팅"
        
        binding.btnSend.setOnClickListener {
//            val message = binding.etMessage.text.toString()
//            if (message.isNotEmpty()) {
//                // TODO: 메시지 전송 구현
//                binding.etMessage.text.clear()
//            }
        }
    }

    /**
     * 첨부파일 메뉴 관련 기능 설정
     * 버튼 클릭 시 메뉴를 토글하고 아이콘 변경
     */
    private fun setupAttachmentMenu() {
        var isMenuOpen = false
        
        binding.btnAdd.setOnClickListener {
            isMenuOpen = !isMenuOpen
            binding.attachmentMenu.visibility = if (isMenuOpen) View.VISIBLE else View.GONE
            binding.btnAdd.setImageResource(if (isMenuOpen) R.drawable.down else R.drawable.add)
        }
    }

    /**
     * Fragment 제거 시 메모리 누수 방지를 위한 정리
     * binding 참조 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 