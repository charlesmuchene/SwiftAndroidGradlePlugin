package com.charlesmuchene.sample.domain

class SwiftLibrary {

    init {
        System.loadLibrary("native-lib")
    }

    external fun captionFromSwift(): String

    /**
     * Generate a fractal image
     *
     * @param width The width of the image
     * @param height The height of the image
     * @param scale The scale of the fractal
     * @param cx The x-coordinate of the center of the fractal
     * @param cy The y-coordinate of the center of the fractal
     * @return A 2D array of doubles representing the fractal image pixel data
     *         as a 1D array (row-major order). The values are the Hue range: 0.0 -> 1.0
     */
    external fun generateFractal(width: Int, height: Int, scale: Double, cx: Double, cy: Double): DoubleArray

}