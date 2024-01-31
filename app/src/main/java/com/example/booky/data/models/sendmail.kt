package com.example.booky.data.models
import com.google.gson.annotations.SerializedName

data class sendmail (
    @SerializedName("email")
    var email: String,
    @SerializedName("activationCode")
    var activationCode: String
)
