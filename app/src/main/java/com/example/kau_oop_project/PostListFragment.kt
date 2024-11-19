package com.example.kau_oop_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var postList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_post_list, container, false)

        recyclerView = view.findViewById(R.id.post_list)

        // 게시글 목록 예시
        postList = listOf(
            "게시글 1 제목",
            "게시글 2 제목",
            "게시글 3 제목"
        )

        adapter = PostAdapter(postList)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}