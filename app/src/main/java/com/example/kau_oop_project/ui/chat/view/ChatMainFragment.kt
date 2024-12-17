package com.example.kau_oop_project.ui.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kau_oop_project.databinding.FragmentChatMainBinding

class ChatMainFragment : Fragment() {
    private var binding: FragmentChatMainBinding? = null
    private lateinit var pagerAdapter: ChatPagerAdapter

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
     * Fragment 제거 시 메모리 누수 방지를 위한 정리 작업
     * binding 참조 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}