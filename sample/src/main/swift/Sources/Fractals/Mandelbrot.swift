//
//  File.swift
//  native-lib
//
//  Created by Charles Muchene on 10/28/25.
//

/// Generates a 2D array of escape counts for a defined rendering area.
///
/// - Parameters:
///    - width: The total pixel width of the grid.
///    - height: The total pixel height of the grid.
///    - xMin: The minimum real value (left edge) of the complex view.
///    - xMax: The maximum real value (right edge) of the complex view.
///    - yMin: The minimum imaginary value (bottom edge) of the complex view.
///    - yMax: The maximum imaginary value (top edge) of the complex view.
///    - strategy: The coloring strategy to be applied
/// - Returns: A 2D array (Array<Array<Int>>) where grid[y][x] is the escape count.
func generateMandelbrotGrid(
    width: Int,
    height: Int,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double,
    strategy: MandelbrotColoringStrategy
) async -> [[Double]] {
    let maxIterations = strategy.maxIterations

    var hueGrid: [[Double]] = Array(repeating: Array(repeating: 0.0, count: width), count: height)

    await withTaskGroup(of: (Int, [Double]).self) { group in
        // Create a concurrent task for each row of pixels.
        for y in 0..<height {
            group.addTask { [width, height, xMin, xMax, yMin, yMax, maxIterations, strategy] in
                var hueRow = Array(repeating: 0.0, count: width)
                // For each pixel in this row...
                for x in 0..<width {
                    // Map the pixel coordinate (x, y) to a complex number 'c'
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

                    // Determine the color hue from the escape count
                    hueRow[x] = strategy.colorIndex(forCount: count)
                }
                return (y, hueRow)
            }
        }

        // As each task completes, collect the results and place them into hue grid
        for await (y, row) in group {
            hueGrid[y] = row
        }
    }

    return hueGrid
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
fileprivate func mapPixelToComplex(
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

/**
 Calculates the escape count for a given point 'c' in the Mandelbrot set.
 
 - Parameters:
 - c: The complex number corresponding to the point in the complex plane.
 - maxIterations: The maximum number of iterations to perform.
 - escapeRadiusSquared: The square of the escape radius (default is 4.0, as |z|^2 > 4.0 is equivalent to |z| > 2.0).
 - Returns: The number of iterations until escape, or maxIterations if the point did not escape.
 */
fileprivate func mandelbrotEscapeCount(
    c: Complex,
    maxIterations: Int,
    escapeRadiusSquared: Double = 4.0
) -> Int {
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
            return iteration + 1 // +1 because we escaped on the (iteration + 1)-th step
        }

        iteration += 1
    }

    // The point 'c' did not escape within the maximum number of iterations.
    // This point is likely inside the Mandelbrot set.
    return maxIterations
}
