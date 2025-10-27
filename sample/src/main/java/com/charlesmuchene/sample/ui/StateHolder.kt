package com.charlesmuchene.sample.ui

import androidx.compose.runtime.getValue
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
import com.charlesmuchene.sample.domain.SwiftLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StateHolder(private val dispatcher: CoroutineContext) : ViewModel() {

    private val swiftLib = SwiftLibrary()

    var title by mutableStateOf("Swift Android Gradle Plugin Demo")
        private set

    var image by mutableStateOf<ImageBitmap?>(null)
        private set

    init {
        viewModelScope.launch(dispatcher) { title = swiftLib.titleFromSwift() }
    }

    fun onSizeChanged(size: IntSize) {
        if (size.width == 0 || size.height == 0) return
        generateFractal(width = size.width, height = size.height)
    }

    private fun generateFractal(width: Int, height: Int) {
        viewModelScope.launch(dispatcher) {
            val hueArray = swiftLib.generateFractal(width = width, height = height)
            image = createImage(hueArray = hueArray, width = width, height = height)
        }
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
                // We set Saturation and Value (Brightness) to 1.0
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