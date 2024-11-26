package com.example.kau_oop_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kau_oop_project.databinding.FragmentOpenChatListBinding

class OpenChatListFragment : Fragment() {
    private var binding: FragmentOpenChatListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpenChatListBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
} 