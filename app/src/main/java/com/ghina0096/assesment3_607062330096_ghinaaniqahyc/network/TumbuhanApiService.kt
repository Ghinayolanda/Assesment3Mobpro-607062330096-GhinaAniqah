package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network

import retrofit2.http.Header
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.OpStatus
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("tumbuhan")
    suspend fun postTumbuhan(
        @Header("Authorization") userId: String,
        @Part("nama") nama: RequestBody,
        @Part("namalatin") namalatin: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus
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
