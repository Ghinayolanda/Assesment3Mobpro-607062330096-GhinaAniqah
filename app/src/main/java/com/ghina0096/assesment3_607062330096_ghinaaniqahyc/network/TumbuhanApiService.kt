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
import retrofit2.http.*

private const val BASE_URL = "https://3cae-182-253-194-62.ngrok-free.app/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TumbuhanApiService {
    @GET("tumbuhan")
    suspend fun getTumbuhan(
        @Header("Authorization") userId: String
    ): List<Tumbuhan>

    @Multipart
    @POST("tumbuhan")
    suspend fun postTumbuhan(
        @Header("Authorization") userId: String,
        @Part("namaTumbuhan") namaTumbuhan: RequestBody,
        @Part("namaLatin") namalatin: RequestBody,
        @Part imageId: MultipartBody.Part
    ): OpStatus

    @Multipart
    @POST("tumbuhan/{id}") // Changed from @POST
    suspend fun updateTumbuhan(
        @Path("id") id: String,
        @Header("Authorization") userId: String,
        @Part("namaTumbuhan") namaTumbuhan: RequestBody,
        @Part("namaLatin") namaLatin: RequestBody,
        @Part imageId: MultipartBody.Part? // Made nullable: The server expects this part, but it might be null if no new image is uploaded.
    ): OpStatus

        @DELETE("tumbuhan/{id}")
//        @HTTP(method = "DELETE", path = "tumbuhan", hasBody = true)
        suspend fun deleteTumbuhan(
            @Header("Authorization") userId: String,
            @Path("id") id : String
        ): OpStatus

        // DELETE with query ?id=
//        @DELETE("tumbuhan")
//        suspend fun deleteTumbuhanWithQuery(
//            @Header("Authorization") userId: String,
//            @Query("id") tumbuhanId: String
//        ): OpStatus

        // DELETE with path parameter /tumbuhan/{id}
//        @DELETE("tumbuhan/{id}")
//        suspend fun deleteTumbuhanWithPath(
//            @Header("Authorization") userId: String,
//            @Path("id") tumbuhanId: String
//        ): OpStatus

}

object TumbuhanApi {
    val service: TumbuhanApiService by lazy {
        retrofit.create(TumbuhanApiService::class.java)
    }

    fun getTumbuhanImageUrl(imageId: String): String {
        return "https://3cae-182-253-194-62.ngrok-free.app/storage/$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }
