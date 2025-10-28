//
// Created by Charles Muchene on 10/27/25.
//

import Foundation

public func generateFractal(width: Int, height: Int, scale: Double, cx: Double, cy: Double) -> [Double] {
    let iterations = 50 // Reduced for faster rendering during zoom

    let strategy = DiscreteColoringStrategy(maxIterations: iterations)

    // Calculate the aspect ratio to prevent stretching
    let aspectRatio = Double(width) / Double(height)

    // Calculate the visible range in the complex plane based on scale
    // 'scale' determines the half-width of the view. Smaller scale = more zoom.
    let halfWidth = scale
    let halfHeight = scale / aspectRatio

    // Calculate the boundaries of the complex plane view
    let xMin = cx - halfWidth
    let xMax = cx + halfWidth
    let yMin = cy - halfHeight
    let yMax = cy + halfHeight

    let renderedGrid = generateMandelbrotGrid(
        width: width,
        height: height,
        xMin: xMin,
        xMax: xMax,
        yMin: yMin,
        yMax: yMax,
        strategy: strategy
    )
    return prepareDataForJNI(grid: renderedGrid)
}

fileprivate func prepareDataForJNI(grid: [[Double]]) -> [Double] {
    // Flatten the 2D grid into a 1D array (row-major order)
    grid.flatMap {
        $0
    }
}
