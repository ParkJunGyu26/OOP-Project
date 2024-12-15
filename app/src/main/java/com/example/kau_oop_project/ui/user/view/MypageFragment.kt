package com.example.kau_oop_project.ui.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kau_oop_project.databinding.FragmentMypageBinding
import com.example.kau_oop_project.ui.user.viewmodel.MypageViewModel
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import com.example.kau_oop_project.data.model.response.MypageResponse
import androidx.navigation.fragment.findNavController
import com.example.kau_oop_project.R

class MypageFragment : Fragment() {
    private var binding: FragmentMypageBinding? = null
    private val mypageViewModel: MypageViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupButtons()
        
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding?.apply {
                    txtMyName.text = it.name
                    // 나중에 이미지 처리
                }
            }
        }
    }

    private fun setupButtons() {
        binding?.apply {
            btnMypageToUpdate.setOnClickListener {
                findNavController().navigate(R.id.action_mypageFragment_to_updateMypageFragment)
            }

            btnMypageToLog.setOnClickListener {
                findNavController().navigate(R.id.action_mypageFragment_to_myLogFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}