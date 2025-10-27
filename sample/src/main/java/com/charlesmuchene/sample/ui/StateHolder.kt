package com.charlesmuchene.sample.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.charlesmuchene.sample.domain.SwiftLibrary
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class StateHolder : ViewModel() {

    private val swiftLib = SwiftLibrary()

    var title by mutableStateOf("")
        private set

    var fractalImage by mutableStateOf<ImageBitmap?>(null)
    private set

    init {
        title = swiftLib.titleFromSwift()
    }

    fun generateFractal(width: Int, height: Int) {
        // TODO: Send to background thread.
        val hueArray = swiftLib.generateFractal(width = width, height = height)
        fractalImage = createImage(hueArray = hueArray, width = width, height = height)
    }

    fun createImage(hueArray: DoubleArray, width: Int, height: Int): ImageBitmap {
        val bitmap = createBitmap(width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val hue = hueArray[index].toFloat()

                // HSV to RGB/Color Conversion
                // We set Saturation and Value (Brightness) to 1.0
                val color = Color.hsv(hue * 360f, 1.0f, 1.0f)
                bitmap[x, y] = color.toArgb()
            }
        }
        return bitmap.asImageBitmap()
    }
}