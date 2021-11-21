package admire.aumsu.portal.application.models

class Message (
    val from: Int,
    val title: String,
    val description: String,
    val image: String,
    val id: Int?,
    val comments: ArrayList<Comment>?
)