package admire.aumsu.portal.application.retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import admire.aumsu.portal.application.models.Authorization
import admire.aumsu.portal.application.models.Comment
import admire.aumsu.portal.application.models.Message
import admire.aumsu.portal.application.models.User

interface RequestAPI {

    @POST("login")
    fun authorization(@Body request: Authorization): Call<User>

    @POST("registration")
    fun registration(@Body request: User): Call<User>

    @GET("messages/last")
    fun getLastMessage(@Header("Authorization") token: String): Call<Message>

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
    @POST("messages")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Part("title") title: String,
        @Part("description") description: String,
        @Part image: MultipartBody.Part?
    ): Call<Message>
}