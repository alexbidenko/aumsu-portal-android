package admire.aumsu.portal.application.retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import admire.aumsu.portal.application.models.Authorization
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

    @Multipart
    @POST("messages")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Part("title") title: String,
        @Part("description") description: String,
        @Part image: MultipartBody.Part?
    ): Call<Message>
}