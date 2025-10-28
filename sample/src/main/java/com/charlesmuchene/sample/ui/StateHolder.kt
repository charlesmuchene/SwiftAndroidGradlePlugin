package com.charlesmuchene.sample.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.charlesmuchene.sample.R
import com.charlesmuchene.sample.domain.SwiftLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

class StateHolder(private val dispatcher: CoroutineContext) : ViewModel() {

    private val swiftLib = SwiftLibrary()

    var title by mutableStateOf("Swift Android Gradle Plugin Demo")
        private set

    var image by mutableStateOf<ImageBitmap?>(null)
        private set

    var placeholderTextId by mutableIntStateOf(R.string.loading_image)
        private set

    // State for zoom and pan
    private var scale = 2.0
    private var cx = -0.68
    private var cy = 0.45

    init {
        viewModelScope.launch(dispatcher) { title = swiftLib.titleFromSwift() }
    }

    fun onSizeChanged(size: IntSize) {
        if (size.width == 0 || size.height == 0) {
            placeholderTextId = R.string.no_image
            return
        }

        val dimensions = min(size.width, size.height)
        viewModelScope.launch {
            while (isActive) {
                generateFractal(width = dimensions, height = dimensions)
                scale *= 0.9
                delay(50)
            }
        }
    }

    private suspend fun generateFractal(width: Int, height: Int) = withContext(dispatcher) {
        val hueArray = swiftLib.generateFractal(
            width = width,
            height = height,
            scale = scale,
            cx = cx,
            cy = cy
        )
        image = createImage(hueArray = hueArray, width = width, height = height)
    }

    fun createImage(hueArray: DoubleArray, width: Int, height: Int): ImageBitmap {
        val bitmap = createBitmap(width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val hue = hueArray[index].toFloat()
                // If inside color is the fixed-inside-color, set brightness to 0.0 for black
                val brightness = if (hue == 0.0f) 0f else 1.0f

                // HSV to RGB/Color Conversion
                val color = Color.hsv(hue * 360f, 1.0f, brightness)
                bitmap[x, y] = color.toArgb()
            }
        }
        return bitmap.asImageBitmap()
    }

    class Factory(private val dispatcher: CoroutineContext = Dispatchers.IO) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StateHolder::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StateHolder(dispatcher) as T
            }

            throw IllegalArgumentException("Unknown viewmodel class")
        }
    }
}