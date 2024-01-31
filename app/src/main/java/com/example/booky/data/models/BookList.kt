package com.example.booky.data.models

import com.google.gson.annotations.SerializedName

data class BookList (
    @SerializedName("userId")
    var userId: User,
    @SerializedName("title")
    var title: String,
    @SerializedName("year")
    var year: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("offre")
    var offre: String,
    @SerializedName("_id")
    var id: String
)