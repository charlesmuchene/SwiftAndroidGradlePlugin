//
//  File.swift
//  native-lib
//
//  Created by Charles Muchene on 10/28/25.
//

/**
 A complex number a+bi
 */
internal struct Complex : Sendable {
    var real: Double
    var imag: Double
    
    /// Calculates the square of the complex number: (a + bi)^2 = (a^2 - b^2) + 2abi
    func squared() -> Complex {
        let newReal = real * real - imag * imag
        let newImag = 2.0 * real * imag
        return Complex(real: newReal, imag: newImag)
    }
    
    /// Calculates the magnitude squared: |z|^2 = a^2 + b^2.
    /// Used for the escape condition as it avoids a computationally expensive square root.
    var magnitudeSquared: Double {
        return real * real + imag * imag
    }
}
