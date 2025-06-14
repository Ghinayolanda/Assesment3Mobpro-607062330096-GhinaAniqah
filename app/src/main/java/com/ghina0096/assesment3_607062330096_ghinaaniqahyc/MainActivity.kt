package com.ghina0096.assesment3_607062330096_ghinaaniqahyc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.Assesment3_607062330096_GhinaAniqahYCTheme
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.screen.MainScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment3_607062330096_GhinaAniqahYCTheme {
                MainScreen()
            }
        }
    }
}