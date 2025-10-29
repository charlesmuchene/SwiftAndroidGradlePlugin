package com.charlesmuchene.sample.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charlesmuchene.sample.R
import com.charlesmuchene.sample.ui.theme.SAGPSampleTheme

@Composable
fun MainScreen(stateHolder: StateHolder = viewModel(factory = StateHolder.Factory())) {
    SAGPSampleTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar { stateHolder.reset() } }) { innerPadding ->
            Content(stateHolder = stateHolder, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun Content(stateHolder: StateHolder, modifier: Modifier = Modifier) {
    val bitmap = stateHolder.image
    var contentSize by remember { mutableStateOf<IntSize?>(null) }
    LaunchedEffect(contentSize) {
        contentSize?.let(stateHolder::generateInitialFractal)
    }

    val animatedScale = animateImage(contentSize, stateHolder)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .onSizeChanged {
                contentSize = it
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = bitmap == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) { Text(text = stringResource(stateHolder.placeholderTextId)) }

        AnimatedVisibility(
            visible = bitmap != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut(),
        ) {
            bitmap?.let {
                FractalImage(
                    bitmap = it,
                    animatedScale = animatedScale,
                    caption = stateHolder.caption
                )
            }
        }
    }
}

@Composable
private fun FractalImage(
    bitmap: ImageBitmap,
    animatedScale: Animatable<Float, AnimationVector1D>,
    caption: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val elevation = 16.dp
        Image(
            bitmap = bitmap,
            contentDescription = caption,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .shadow(elevation = elevation)
                .clip(RoundedCornerShape(elevation))
                .graphicsLayer {
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = caption, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun animateImage(
    size: IntSize?,
    stateHolder: StateHolder,
): Animatable<Float, AnimationVector1D> {
    val zoomFactor = remember { StateHolder.ZOOM_FACTOR.toFloat() }
    val animatedScale = remember { Animatable(1.0f) }

    LaunchedEffect(stateHolder.image) {
        if (size == null) return@LaunchedEffect

        animatedScale.animateTo(
            targetValue = zoomFactor,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        stateHolder.generateNextFractal(width = size.width, height = size.height)
        animatedScale.snapTo(1.0f)
    }

    return animatedScale
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(modifier: Modifier = Modifier, onReset: () -> Unit) {
    TopAppBar(
        modifier = modifier.shadow(elevation = 4.dp),
        title = { Text(text = stringResource(R.string.title)) },
        actions = {
            IconButton(onClick = onReset, modifier = Modifier) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_reset),
                    contentDescription = stringResource(R.string.reset),
                )
            }
        }
    )
}