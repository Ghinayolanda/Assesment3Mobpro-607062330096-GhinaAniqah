package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.ApiStatus
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.TumbuhanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Tumbuhan>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = TumbuhanApi.service.getTumbuhan(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, namaTumbuhan: String, namaLatin: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = TumbuhanApi.service.postTumbuhan(
                    userId,
                    namaTumbuhan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    namaLatin.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(id : String,userId: String, namaTumbuhan: String, namaLatin: String, bitmap: Bitmap) {
        Log.e("MainViewModel","data : $userId , $namaLatin , $namaTumbuhan")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = TumbuhanApi.service.updateTumbuhan(
                    id,
                    userId,
                    namaTumbuhan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    namaLatin.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(id : String, userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = TumbuhanApi.service.deleteTumbuhan(id , userId)

                if (result.status == "success"){
                    retrieveData(userId)
                }else{
                    val errorMsg = "Gagal Menghapus : ${result.message}"
                    Log.e("MainViewModel",errorMsg)
                    errorMessage.value = errorMsg
                }
            }catch (e : Exception){
                val errorMsg = "Error : ${e.message}"

                Log.e("MainViewModel", errorMsg,e)
                errorMessage.value = errorMsg
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData("imageId", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}
