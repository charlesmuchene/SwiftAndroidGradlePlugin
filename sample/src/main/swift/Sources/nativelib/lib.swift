// The Swift Programming Language
// https://docs.swift.org/swift-book

import Android
import Fractals

@_cdecl("Java_com_charlesmuchene_sample_MainActivity_textFromSwift")
public func MainActivity_contentFromSwift(env: UnsafeMutablePointer<JNIEnv?>, clazz: jclass) -> jstring {
    let hello = fractals().joined(separator: " ")
    return hello.withCString { ptr in
        env.pointee!.pointee.NewStringUTF(env, ptr)!
    }
}
