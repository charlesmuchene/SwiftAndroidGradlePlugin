//
// Created by Charles Muchene on 10/27/25.
//

import Foundation

/**
 A complex number a+bi
 */
struct Complex {
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

/**
 Calculates the escape count for a given point 'c' in the Mandelbrot set.
 
 - Parameters:
 - c: The complex number corresponding to the point in the complex plane.
 - maxIterations: The maximum number of iterations to perform.
 - escapeRadiusSquared: The square of the escape radius (default is 4.0, as |z|^2 > 4.0 is equivalent to |z| > 2.0).
 - Returns: The number of iterations until escape, or maxIterations if the point did not escape.
 */
func mandelbrotEscapeCount(
    c: Complex,
    maxIterations: Int,
    escapeRadiusSquared: Double = 4.0
) -> Double {
    var z = Complex(real: 0.0, imag: 0.0)

    // The sequence starts with z_0 = 0
    var iteration = 0

    while iteration < maxIterations {
        // z_{n+1} = z_n^2 + c
        let zSquared = z.squared()
        z.real = zSquared.real + c.real
        z.imag = zSquared.imag + c.imag

        // Check the escape condition: |z| > 2, which is equivalent to |z|^2 > 4.
        if z.magnitudeSquared > escapeRadiusSquared {
            // The point 'c' has escaped. Return the current iteration count.
            return Double(iteration + 1) // +1 because we escaped on the (iteration + 1)-th step
        }

        iteration += 1
    }

    // The point 'c' did not escape within the maximum number of iterations.
    // This point is likely inside the Mandelbrot set.
    return Double(maxIterations)
}

/**
 Maps an integer pixel coordinate to a complex number 'c' within a defined fractal view.
 
 - Parameters:
 - pixelX: The x-coordinate (column) of the pixel.
 - pixelY: The y-coordinate (row) of the pixel.
 - width: The total pixel width of the rendering area.
 - height: The total pixel height of the rendering area.
 - xMin: The minimum real value (left edge) of the complex view.
 - xMax: The maximum real value (right edge) of the complex view.
 - yMin: The minimum imaginary value (bottom edge) of the complex view.
 - yMax: The maximum imaginary value (top edge) of the complex view.
 - Returns: The Complex number 'c' corresponding to the pixel location.
 */
func mapPixelToComplex(
    pixelX: Int,
    pixelY: Int,
    width: Int,
    height: Int,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double
) -> Complex {
    // Convert pixel coordinates to a normalized value (0.0 to 1.0)
    let normX = Double(pixelX) / Double(width)
    let normY = Double(pixelY) / Double(height) // Note: Pixel Y is usually top-down, which we handle below.

    // Map normalized values to the complex number range (Real Part)
    // Real value = xMin + normX * (xMax - xMin)
    let cReal = xMin + normX * (xMax - xMin)

    // Map normalized values to the complex number range (Imaginary Part)
    // Note: The y-axis in the complex plane (imaginary) usually increases upwards,
    // while pixel rows (pixelY) typically increase downwards (top to bottom).
    // To correct this, we use (1.0 - normY) to flip the vertical mapping.
    let cImag = yMin + (1.0 - normY) * (yMax - yMin)

    return Complex(real: cReal, imag: cImag)
}

/// Generates a 2D array of escape counts for a defined rendering area.
///
/// - Parameters:
///    - width: The total pixel width of the grid.
///    - height: The total pixel height of the grid.
///    - maxIterations: The maximum number of iterations for the Mandelbrot calculation.
///    - xMin: The minimum real value (left edge) of the complex view.
///    - xMax: The maximum real value (right edge) of the complex view.
///    - yMin: The minimum imaginary value (bottom edge) of the complex view.
///    - yMax: The maximum imaginary value (top edge) of the complex view.
/// - Returns: A 2D array (Array<Array<Int>>) where grid[y][x] is the escape count.
func generateMandelbrotGrid(
    width: Int,
    height: Int,
    maxIterations: Int,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double
) -> [[Int]] {
    // Initialize the grid with the specified height (rows)
    var grid: [[Int]] = Array(repeating: Array(repeating: 0, count: width), count: height)

    // Loop through every pixel in the grid (row by row, then column by column)
    for y in 0..<height {
        for x in 0..<width {
            // Map the pixel coordinate (x, y) to a complex number c
            let c = mapPixelToComplex(
                pixelX: x,
                pixelY: y,
                width: width,
                height: height,
                xMin: xMin,
                xMax: xMax,
                yMin: yMin,
                yMax: yMax
            )

            // Calculate the escape count for that complex number
            let count = mandelbrotEscapeCount(c: c, maxIterations: maxIterations)

            // Store the result in the grid at the corresponding (y, x) position
            grid[y][x] = Int(count)
        }
    }

    return grid
}

/// Protocol for a pluggable coloring strategy.
protocol MandelbrotColoringStrategy {
    var maxIterations: Int { get }

    /// Maps the escape count (Int or Double) to a color index (e.g., Hue: 0.0 to 1.0).
    func colorIndex(forCount count: Double) -> Double
}

/// Strategy for classic, banded coloring.
struct DiscreteColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int
    let bands: Int // Number of distinct colors to cycle through

    init(maxIterations: Int, bands: Int = 16) {
        self.maxIterations = maxIterations
        self.bands = bands
    }

    func colorIndex(forCount count: Double) -> Double {
        let intCount = Int(count.rounded())

        // Inside set (did not escape) -> returns 0.0 (Black/Fixed color)
        if intCount >= maxIterations {
            return 0.0
        }

        // Outside set -> use the modulo operator to cycle through colors
        // The color index cycles from 0.0 to 1.0 based on the bands.
        return Double(intCount % bands) / Double(bands)
    }
}

/// Strategy for smooth, continuous coloring.
struct ContinuousColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int
    let colorScaleFactor: Double // Controls the speed of the color change

    init(maxIterations: Int, colorScaleFactor: Double = 10.0) {
        self.maxIterations = maxIterations
        self.colorScaleFactor = colorScaleFactor
    }

    func colorIndex(forCount count: Double) -> Double {
        // Inside set (did not escape) -> returns 0.0 (Black/Fixed color)
        if count >= Double(maxIterations) {
            return 0.0
        }

        // Outside set -> apply the smoothing calculation to the count
        let scaledCount = count * colorScaleFactor

        // fmod (floating point modulo) wraps the scaled count between 0.0 and 1.0.
        return fmod(scaledCount, 1.0)
    }
}

/// Strategy for coloring the interior of the set differently.
struct InsideColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int

    func colorIndex(forCount count: Double) -> Double {
        let intCount = Int(count.rounded())

        // Inside set -> Map the final iteration count to a repeating color.
        if intCount >= maxIterations {
            let insideBands = 8 // Fewer bands for simplicity
            // Use the maxIterations as a base and map the integer count modulo the bands.
            // (Note: To truly color the inside, you'd need the final Z value,
            // but this uses the count to demonstrate the strategy swap.)
            return Double(intCount % insideBands) / Double(insideBands)
        }

        // Outside set -> returns a fixed color (e.g., 0.5 for a blue/green)
        return 0.5
    }
}

func renderMandelbrot(
    width: Int,
    height: Int,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double,
    strategy: MandelbrotColoringStrategy
) -> [[Double]] {
    // Note: Since ContinuousColoring requires a Double count, we'll stick to
    // using mandelbrotSmoothedCount and let the strategy handle rounding if needed.
    let maxIterations = strategy.maxIterations

    var finalHueGrid: [[Double]] = Array(repeating: Array(repeating: 0.0, count: width), count: height)

    for y in 0..<height {
        for x in 0..<width {
            let c = mapPixelToComplex(/* ... parameters ... */
                                      pixelX: x, pixelY: y, width: width, height: height,
                                      xMin: xMin, xMax: xMax, yMin: yMin, yMax: yMax
            )

            // Get the raw (smoothed) escape count
            let rawCount = mandelbrotEscapeCount(c: c, maxIterations: maxIterations)

            // Pass the count to the chosen strategy to get the final color index
            let colorHue = strategy.colorIndex(forCount: rawCount)

            finalHueGrid[y][x] = colorHue
        }
    }

    return finalHueGrid
}

func prepareDataForJNI(grid: [[Double]]) -> [Double] {
    // Flatten the 2D grid into a 1D array (row-major order)
    grid.flatMap {
        $0
    }
}

public func generateFractal(width: Int, height: Int) -> [Double] {
    let iterations = 100 // TODO: Set this from other side of bridge
   // let discreteStrategy = DiscreteColoringStrategy(maxIterations: iterations, bands: 10)
    let continuousStrategy = ContinuousColoringStrategy(maxIterations: iterations, colorScaleFactor: 8.0)
   // let insideStrategy = InsideColoringStrategy(maxIterations: iterations)
    let xMin = -2.0
    let xMax = 1.0
    let yMin = -1.5
    let yMax = 1.5
    let renderedDiscreteGrid = renderMandelbrot(
        width: width,
        height: height,
        xMin: xMin,
        xMax: xMax,
        yMin: yMin,
        yMax: yMax,
        strategy: continuousStrategy
    )
    return prepareDataForJNI(grid: renderedDiscreteGrid)
}
