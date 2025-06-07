package com.example.my_api.data.remote

import com.example.my_api.domain.Character
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface GenshinApi {
    @GET("characters")
    suspend fun getCharacters(): List<String>

    @GET("characters/{name}")
    suspend fun getCharacterDetails(@Path("name") name: String): Character
}

object RetrofitInstance {
    val api: GenshinApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://genshin.jmp.blue/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GenshinApi::class.java)
    }
}