package com.charlesmuchene.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charlesmuchene.sample.ui.theme.SAGPSampleTheme

@Composable
fun MainScreen(title: String = "Swift Android Gradle Plugin Demo") {
    SAGPSampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Content(text = title, modifier = Modifier.padding(innerPadding))
        }
    }
}


@Composable
private fun Content(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}