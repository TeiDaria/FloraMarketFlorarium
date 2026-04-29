package com.example.floramarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.floramarket.viewmodel.FlowerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Создаем ViewModel
                val viewModel: FlowerViewModel = viewModel()
                // Запускаем наше приложение
                FlowerApp(viewModel = viewModel)
            }
        }
    }
}