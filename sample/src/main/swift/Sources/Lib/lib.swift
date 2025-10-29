// The Swift Programming Language
// https://docs.swift.org/swift-book

import Android
import Fractals

@_cdecl("Java_com_charlesmuchene_sample_domain_SwiftLibrary_captionFromSwift")
public func SwiftLibrary_captionFromSwift(env: UnsafeMutablePointer<JNIEnv?>, clazz: jclass) -> jstring {
    let title = "Mandelbrot Set from Swift 😎"
    return title.withCString { ptr in
        env.pointee!.pointee.NewStringUTF(env, ptr)!
    }
}

@_cdecl("Java_com_charlesmuchene_sample_domain_SwiftLibrary_generateFractal")
public func SwiftLibrary_generateFractal(env: UnsafeMutablePointer<JNIEnv?>, clazz: jclass, width: jint, height: jint, scale: jdouble, cx: jdouble, cy: jdouble) -> jdoubleArray {

    let data = generateFractal(width: Int(width), height: Int(height), scale: Double(scale), cx: Double(cx), cy: Double(cy))

    guard let javaArray = env.pointee?.pointee.NewDoubleArray(env, jsize(data.count)) else {
        // Find the `java.lang.OutOfMemoryError` class
        let exceptionClass = env.pointee?.pointee.FindClass(env, "java/lang/OutOfMemoryError")
        if let exceptionClass = exceptionClass {
            let message = "Failed to allocate memory for double array in JNI"
            message.withCString { cString in
                _ = env.pointee?.pointee.ThrowNew(env, exceptionClass, cString)
            }
        }
        // It's important to return a value, even though an exception is pending.
        // The JVM will handle the exception on the Java/Kotlin side.
        return jdoubleArray(bitPattern: 0)!
    }

    // Get a pointer to the elements of the new Java array.
    //    'isCopy' will be set to JNI_TRUE if a copy was made.
    let elements = env.pointee?.pointee.GetDoubleArrayElements(env, javaArray, nil)

    // Copy the data from the Swift array to the Java array's memory.
    //    We use a loop for clarity, but memcpy could also be used for performance.
    // TODO: Use memcpy
    for i in 0..<data.count {
        elements?[i] = data[i]
    }

    // Release the pointer, copying the modified elements back into the Java array.
    //    Mode 0 means copy back and free the buffer.
    env.pointee?.pointee.ReleaseDoubleArrayElements(env, javaArray, elements, 0)

    return javaArray
}
