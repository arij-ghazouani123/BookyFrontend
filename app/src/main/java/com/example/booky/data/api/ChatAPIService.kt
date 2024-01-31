package com.example.booky.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatAPIService {
    const val BASE_URL: String = "http://10.0.2.2:9090/"

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val chatService: ChatService by lazy {
        retrofit().create(ChatService::class.java)
    }


}