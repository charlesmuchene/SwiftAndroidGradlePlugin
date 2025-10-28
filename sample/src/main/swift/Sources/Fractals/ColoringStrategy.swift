//
//  ColoringStrategy.swift
//  native-lib
//
//  Created by Charles Muchene on 10/28/25.
//

import Foundation

/// Protocol for a pluggable coloring strategy.
protocol MandelbrotColoringStrategy : Sendable {
    var maxIterations: Int { get }
    
    /// Maps the escape count (Int) to a color index (e.g., Hue: 0.0 to 1.0).
    func colorIndex(forCount count: Int) -> Double
}

/// Strategy for classic, banded coloring.
struct DiscreteColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int
    let bands: Int // Number of distinct colors to cycle through
    
    init(maxIterations: Int, bands: Int = 16) {
        self.maxIterations = maxIterations
        self.bands = bands
    }
    
    func colorIndex(forCount count: Int) -> Double {
        // Inside set (did not escape) -> returns 0.0 (Black/Fixed color)
        if count >= maxIterations {
            return 0.0
        }
        
        // Outside set -> use the modulo operator to cycle through colors
        // The color index cycles from 0.0 to 1.0 based on the bands.
        return Double(count % bands) / Double(bands)
    }
}

/// Strategy for smooth, continuous coloring.
struct ContinuousColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int
    let colorScaleFactor: Double // Controls the speed of the color change
    
    init(maxIterations: Int, colorScaleFactor: Double = 5.0) {
        self.maxIterations = maxIterations
        self.colorScaleFactor = colorScaleFactor
    }
    
    func colorIndex(forCount count: Int) -> Double {
        // Inside set (did not escape) -> returns 0.0 (Black/Fixed color)
        if count >= maxIterations {
            return 0.0
        }
        
        // Outside set -> apply the smoothing calculation to the count
        let scaledCount = Double(count) * colorScaleFactor
        
        // fmod (floating point modulo) wraps the scaled count between 0.0 and 1.0.
        return fmod(scaledCount, 1.0)
    }
}

/// Strategy for coloring the interior of the set differently.
struct InsideColoringStrategy: MandelbrotColoringStrategy {
    let maxIterations: Int
    
    func colorIndex(forCount count: Int) -> Double {
        // Inside set -> Map the final iteration count to a repeating color.
        if count >= maxIterations {
            let insideBands = 8 // Fewer bands for simplicity
            // Use the maxIterations as a base and map the integer count modulo the bands.
            // (Note: To truly color the inside, you'd need the final Z value,
            // but this uses the count to demonstrate the strategy swap.)
            return Double(count % insideBands) / Double(insideBands)
        }
        
        // Outside set -> returns a fixed color (e.g., 0.5 for a blue/green)
        return 0.5
    }
}
