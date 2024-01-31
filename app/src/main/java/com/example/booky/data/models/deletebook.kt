package com.example.booky.data.models

import com.google.gson.annotations.SerializedName

data class deletebook (
    @SerializedName("acknowledged")
    var acknowledged: Boolean,
    @SerializedName("deletedCount")
    var deletedCount: Int

)