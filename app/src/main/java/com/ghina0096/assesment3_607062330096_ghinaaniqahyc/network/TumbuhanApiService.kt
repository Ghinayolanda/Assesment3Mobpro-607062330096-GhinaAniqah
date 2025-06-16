package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network

import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://bee8-2402-5680-8761-96b9-1459-4801-872-2aaa.ngrok-free.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TumbuhanApiService {
    @GET("tumbuhan")
    suspend fun getTumbuhan(): List<Tumbuhan>
}

object TumbuhanApi {
    val service: TumbuhanApiService by lazy {
        retrofit.create(TumbuhanApiService::class.java)
    }

    fun getTumbuhanImageUrl(imageId: String): String {
        return "${BASE_URL}storage/$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }
