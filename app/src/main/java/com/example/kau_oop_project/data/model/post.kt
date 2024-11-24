// Author 데이터 클래스
data class Author(
    val name: String,
    val profileImageUrl: String
)

// PostContent 데이터 클래스
data class PostContent(
    val type: ContentType,  // ContentType Enum 사용
    val content: String     // 실제 텍스트나 이미지 URL
)

// ContentType Enum
enum class ContentType {
    TEXT,
    IMAGE
}

// Reply 데이터 클래스
data class Reply(
    val replyAuthor: Author,
    val replyContent: String
)

// ReplyList 데이터 클래스
data class ReplyList(
    val replyList: ArrayList<Reply>
)

// Post 데이터 클래스
data class Post(
    val postId: String,
    val postAuthor: Author,
    val postContent: PostContent,
    val postReplyList: ReplyList
)
