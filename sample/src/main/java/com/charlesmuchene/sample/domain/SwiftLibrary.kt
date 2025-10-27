package com.charlesmuchene.sample.domain

class SwiftLibrary {

    init {
        System.loadLibrary("native-lib")
    }

    external fun titleFromSwift(): String

    external fun generateFractal(width: Int, height: Int): DoubleArray

}