package com.example.booky.data.models

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("_id")
    var id: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("date")
    var date: String,
    @SerializedName("verified")
    var verified: String,
    @SerializedName("activationCode")
    var activationCode: String,
    @SerializedName("profilPic")
    var profilPic: String,

)
