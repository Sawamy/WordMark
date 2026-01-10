package com.example.wordmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.wordmark.ui.StudyRecordScreen
import com.example.wordmark.ui.theme.WordMarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordMarkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyRecordScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
