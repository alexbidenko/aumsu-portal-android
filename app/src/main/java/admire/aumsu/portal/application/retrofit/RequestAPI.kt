package ru.aumsu.www.application.retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import ru.aumsu.www.application.models.Authorization
import ru.aumsu.www.application.models.Message
import ru.aumsu.www.application.models.User

interface RequestAPI {

    @POST("login")
    fun authorization(@Body request: Authorization): Call<User>

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