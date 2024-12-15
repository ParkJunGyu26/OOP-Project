package com.example.kau_oop_project.ui.chat.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * 채팅 화면의 ViewPager2를 위한 어댑터
 * 일반채팅과 오픈채팅 화면을 전환하는 역할
 */
class ChatPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    /**
     * ViewPager의 총 페이지 수 반환
     * @return 2 (일반채팅, 오픈채팅)
     */
    override fun getItemCount(): Int = 2

    /**
     * 각 position에 해당하는 Fragment 생성
     * @param position 0: 일반채팅, 1: 오픈채팅
     * @return ChatListFragment 인스턴스
     */
    override fun createFragment(position: Int): Fragment {
        return ChatListFragment.newInstance(position == 1)
    }
}