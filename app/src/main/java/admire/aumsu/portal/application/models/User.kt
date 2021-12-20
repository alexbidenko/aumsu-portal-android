package admire.aumsu.portal.application.models

class User (
    val firstName: String?,
    val lastName: String?,
    val login: String,
    val password: String,
    val token: String,
    val avatar: String?,
    val status: String,
    val patronymic: String?,
    val studyGroupId: Int?,
    val id: Int?
) {
    var studyGroup: StudyGroup? = null
}