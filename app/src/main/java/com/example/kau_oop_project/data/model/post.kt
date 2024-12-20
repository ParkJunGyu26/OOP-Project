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
    val replyId:String="",
    val replyParentPostId: String="",
    val replyAuthorId: String="",
    val replyContent: String="",
    val replyTimeStamp: Long=0
)

// ReplyList 데이터 클래스


// Post 데이터 클래스
data class Post(
    val postId:String="",
    val postTag: String="",
    val postTitle: String="",
    val postAuthorId: String="",
    var postRecommendCount:List<String> = emptyList(),
    var postViewCount:Int=0,
    val postContent: List<PostContent> = emptyList(),
    val postReplyIdList: List<String> = emptyList(),
    val postTimeStamp: Long=0
)

