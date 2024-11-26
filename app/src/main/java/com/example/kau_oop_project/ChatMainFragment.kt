package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.kau_oop_project.databinding.FragmentChatMainBinding
import com.example.kau_oop_project.data.model.chat.ChatRoom
import android.content.res.ColorStateList
import android.graphics.Color

class ChatMainFragment : Fragment() {
    private var _binding: FragmentChatMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: ChatPagerAdapter

    /**
     * Fragment의 View를 생성하고 초기화
     * ViewBinding을 통해 레이아웃 inflate
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View가 생성된 후 호출되어 초기 설정을 수행
     * ViewPager와 버튼들의 설정을 초기화
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupButtons()
    }

    /**
     * ViewPager2 초기 설정
     * 채팅 화면 어댑터 연결 및 스와이프 기능 비활성화
     */
    private fun setupViewPager() {
        pagerAdapter = ChatPagerAdapter(this)
        binding.viewPager.apply {
            adapter = pagerAdapter
            isUserInputEnabled = false  // 스와이프 비활성화
        }
    }

    /**
     * 채팅 버튼들의 클릭 이벤트 설정
     * 일반 채팅과 오픈 채팅 전환 기능 구현
     */
    private fun setupButtons() {
        binding.btnChat.setOnClickListener {
            binding.viewPager.currentItem = 0
            updateButtonStyle(false)
        }
        
        binding.btnOpenChat.setOnClickListener {
            binding.viewPager.currentItem = 1
            updateButtonStyle(true)
        }
    }

    /**
     * 채팅 타입에 따른 버튼 스타일 업데이트
     * @param isOpenChat true일 경우 오픈채팅, false일 경우 일반채팅 스타일 적용
     */
    private fun updateButtonStyle(isOpenChat: Boolean) {
        binding.btnChat.apply {
            backgroundTintList = ColorStateList.valueOf(
                if (!isOpenChat) Color.parseColor("#3C82F5") 
                else Color.parseColor("#E2ECFE")
            )
            setTextColor(
                if (!isOpenChat) Color.WHITE 
                else Color.parseColor("#3C82F5")
            )
        }
        
        binding.btnOpenChat.apply {
            backgroundTintList = ColorStateList.valueOf(
                if (isOpenChat) Color.parseColor("#3C82F5") 
                else Color.parseColor("#E2ECFE")
            )
            setTextColor(
                if (isOpenChat) Color.WHITE 
                else Color.parseColor("#3C82F5")
            )
        }
    }

    /**
     * Fragment 제거 시 메모리 누수 방지를 위한 정리 작업
     * binding 참조 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
