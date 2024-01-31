package com.example.booky.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("token")
    val token: String,
    @SerializedName("UserStatus")
    val UserStatus: String,
    @SerializedName("id")
    val  id: String
) :java.io.Serializable

