package ru.aumsu.www.application.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @POST("messages")
    fun sendMessage(@Header("Authorization") token: String, @Body request: Message): Call<Message>
}