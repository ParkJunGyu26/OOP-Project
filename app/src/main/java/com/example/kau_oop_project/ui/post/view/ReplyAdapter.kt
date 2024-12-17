package com.example.kau_oop_project.ui.post.view

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.kau_oop_project.databinding.ItemReplyBinding
import com.example.kau_oop_project.ui.post.viewmodel.PostViewModel
import androidx.fragment.app.activityViewModels
import com.example.kau_oop_project.R
import android.widget.Button
import com.bumptech.glide.Glide
import com.example.kau_oop_project.data.model.Reply
import com.example.kau_oop_project.ui.user.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReplyAdapter(private val userViewModel: UserViewModel,private val postViewModel:PostViewModel) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    private var replyList: List<Reply> = emptyList()

    // ViewHolder 생성
    inner class ReplyViewHolder(private val binding: ItemReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply) {
            // 댓글 작성자와 내용 설정
            val replyUserName=userViewModel.postUsersInfoList.value?.get(reply.replyAuthorId)?.name
            val replyUserProfileImageUrl=userViewModel.postUsersInfoList.value?.get(reply.replyAuthorId)?.profileImage
            binding.replyUserName.text = replyUserName // 댓글 작성자
            Glide.with(binding.userProfileImage.context)
                .load(replyUserProfileImageUrl) // 이미지 URL
                .error(R.drawable.default_image) // 로드 실패 시 보여줄 기본 이미지
                .into(binding.userProfileImage)
            binding.replyContent.text = reply.replyContent // 댓글 내용
            binding.replyWrittenTime.text=formatTime(reply.replyTimeStamp) // 작성 시간

            itemView.setOnClickListener {
                // 다이얼로그 레이아웃 설정
                val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.popup_reply_option, null)
                val btnStartChat = dialogView.findViewById<Button>(R.id.btn_start_chat)
                val btnDeleteReply = dialogView.findViewById<Button>(R.id.btn_delete_reply)

                // 다이얼로그 생성
                val dialog = android.app.AlertDialog.Builder(itemView.context)
                    .setView(dialogView)
                    .create()

                // 유저 검사
                val currentUserId = userViewModel.currentUser.value
                val replyUserId = reply.replyAuthorId

                if (currentUserId == replyUserId) {
                    btnDeleteReply.visibility = View.VISIBLE   // 댓글 삭제 버튼 보이기
                    btnStartChat.visibility = View.GONE       // 1:1 채팅 버튼 숨기기
                } else {
                    btnDeleteReply.visibility = View.GONE    // 댓글 삭제 버튼 숨기기
                    btnStartChat.visibility = View.VISIBLE    // 1:1 채팅 버튼 보이기
                }

                // "1:1 채팅 시작하기" 버튼 클릭
                btnStartChat.setOnClickListener {
                    val currentUserId=userViewModel.currentUser.value
                    val replyUserId=reply.replyAuthorId

                    Log.d("ReplyAdapter", "now user : $currentUserId, reply user id : $replyUserId")
                    dialog.dismiss()
                }

                // "댓글 삭제하기" 버튼 클릭
                btnDeleteReply.setOnClickListener {
                    postViewModel.deleteReply(reply.replyId) // 댓글 삭제 로직 호출
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }



    fun formatTime(timeInMillis: Long): String {
        // Date 객체로 변환
        val date = Date(timeInMillis)

        // 원하는 형식으로 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    // 댓글 리스트 업데이트
    fun updateReplyList(newReplyList: List<Reply>) {
        replyList = newReplyList
        notifyDataSetChanged() // 데이터 변경 시 UI 갱신
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val binding = ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bind(replyList[position])
    }

    override fun getItemCount(): Int = replyList.size
}

