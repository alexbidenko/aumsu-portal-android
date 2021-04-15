package ru.aumsu.www.application.models

import com.fasterxml.jackson.annotation.JsonProperty

class User (
    @JsonProperty("first_name") val firstName: String?,
    @JsonProperty("last_name") val lastName: String?,
    val login: String,
    val password: String,
    val token: String,
    val avatar: String?,
    val status: String,
    val patronymic: String?,
    val id: Int?
)