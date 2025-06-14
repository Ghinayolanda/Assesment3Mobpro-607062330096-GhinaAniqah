package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.TumbuhanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Tumbuhan>())
        private set

    init {
        retrieveData()
    }

    private fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                data.value = TumbuhanApi.service.getTumbuhan()
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
}
