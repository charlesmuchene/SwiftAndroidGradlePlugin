package com.charlesmuchene.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charlesmuchene.sample.R
import com.charlesmuchene.sample.ui.theme.SAGPSampleTheme

@Composable
fun MainScreen(stateHolder: StateHolder = viewModel()) {
    SAGPSampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopBar(title = stateHolder.title)
        }) { innerPadding ->
            val bitmap = stateHolder.image
            Content(bitmap = bitmap, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun Content(bitmap: ImageBitmap?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (bitmap == null) Text(text = stringResource(R.string.missing_image))
        else Image(bitmap = bitmap, contentDescription = stringResource(R.string.fractal))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) }
    )
}