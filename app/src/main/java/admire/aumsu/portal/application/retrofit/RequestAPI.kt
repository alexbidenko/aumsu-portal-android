package admire.aumsu.portal.application.retrofit

import admire.aumsu.portal.application.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RequestAPI {

    @GET("version/{version}")
    fun compareVersion(@Path("version") token: Int): Call<Boolean>

    @POST("login")
    fun authorization(@Body request: Authorization): Call<User>

    @POST("registration")
    fun registration(@Body request: User): Call<User>

    @GET("study-groups")
    fun getStudyGroups(): Call<ArrayList<StudyGroup>>

    @GET("messages")
    fun getMessages(@Header("Authorization") token: String): Call<ArrayList<Message>>

    @GET("messages/{id}")
    fun getMessageById(@Path("id") id: Int, @Header("Authorization") token: String): Call<Message>

    @POST("messages/comment")
    fun sendComment(@Body request: Comment, @Header("Authorization") token: String): Call<Comment>

    @DELETE("messages/comment/{id}")
    fun deleteComment(@Path("id") id: Int, @Header("Authorization") token: String): Call<Boolean>

    @PUT("messages/comment/{id}")
    fun updateComment(@Path("id") id: Int, @Body comment: Comment, @Header("Authorization") token: String): Call<Comment>

    @PUT("user")
    fun updateUser(@Body request: User, @Header("Authorization") token: String): Call<User>

    @Multipart
    @PUT("user/avatar")
    fun updateAvatar(@Part avatar: MultipartBody.Part, @Header("Authorization") token: String): Call<User>

    @Multipart
    @PUT("user/password")
    fun updatePassword(
        @Part("password") password: String,
        @Part("new_password") newPassword: String,
        @Header("Authorization") token: String
    ): Call<User>

    @Multipart
    @POST("messages")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Part("title") title: String,
        @Part("description") description: String,
        @Part image: MultipartBody.Part?
    ): Call<Message>
}