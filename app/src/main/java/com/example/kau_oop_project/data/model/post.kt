package com.example.kau_oop_project.data.model

// PostContent 데이터 클래스
data class PostContent(
    val type: ContentType=ContentType.TEXT,  // ContentType Enum 사용
    val content: String=""     // 실제 텍스트나 이미지 URL
)

// ContentType Enum
enum class ContentType {
    TEXT,
    IMAGE
}

// Reply 데이터 클래스
data class Reply(
    val replyAuthor: user=user("","","","",""),
    val replyContent: String="",
    val replyTimeStamp: Long=0
)

// ReplyList 데이터 클래스
data class ReplyList(
    val replyList: List<Reply> = emptyList()
)

// Post 데이터 클래스
data class Post(
    val postId: String="",
    val postDBId:String="",
    val postTag: String="",
    val postTitle: String="",
    val postAuthor: user=user("","","","",""),
    val postRecommendCount: Int=0,
    val postContent: List<PostContent> = emptyList(),
    val postReplyList: List<Reply> = emptyList(),
    val postTimeStamp: Long=0
)
