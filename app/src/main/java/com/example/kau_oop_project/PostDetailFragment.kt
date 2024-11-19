package com.example.kau_oop_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

class PostDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReplyAdapter
    private lateinit var replyList: List<Pair<String, String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        recyclerView = view.findViewById(R.id.reply_list)

        replyList = listOf(
            Pair("홍길동", "이건 첫 번째 댓글입니다."),
            Pair("김철수", "이건 두 번째 댓글입니다.")
        )

        adapter = ReplyAdapter(replyList)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}
