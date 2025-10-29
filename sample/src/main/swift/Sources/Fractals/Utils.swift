//
//  Utils.swift
//  native-lib
//
//  Created by Charles Muchene on 10/28/25.
//

import Foundation

/// With set generation parallelized, I need to block waiting for the set to be generated for use at the
/// before returning the result to the JNI layer.
///
/// Here's my attempt at doing so 😃
///
func runBlocking<T: Sendable>(operation: @escaping @Sendable () async -> T) -> T {
    let semaphore = DispatchSemaphore(value: 0)
    var result: T?
    
    Task {
        result = await operation()
        semaphore.signal()
    }
    
    semaphore.wait()
    // We can safely force-unwrap here because the semaphore guarantees `result` is set.
    return result!
}


/// This actor is only used to convince the compiler that the result can be safely accessed concurrently
actor ResultHolder<T: Sendable> {
    var value: T?
    
    func setResult(_ newValue: T) {
        self.value = newValue
    }
    
    func getResult() -> T? {
        self.value
    }
    
    func getResultOrDefault(_ defaultValue: T) -> T {
        self.getResult() ?? defaultValue
    }
}
