package admire.aumsu.portal.application.models

import com.google.gson.annotations.SerializedName

class Comment (
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("message_id")
    val messageId: Int,
    val content: String,
    var user: User?
)
