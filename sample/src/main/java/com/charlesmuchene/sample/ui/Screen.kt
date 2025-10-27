package com.charlesmuchene.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
            var contentSize by remember { mutableStateOf<IntSize?>(null) }
            LaunchedEffect(contentSize) {
                contentSize?.let(stateHolder::onSizeChanged)
            }
            Content(bitmap = bitmap, modifier = Modifier.padding(innerPadding)) { contentSize = it }
        }
    }
}

@Composable
private fun Content(
    bitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    onSizeChanged: (IntSize) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .onSizeChanged(onSizeChanged),
        contentAlignment = Alignment.Center
    ) {
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