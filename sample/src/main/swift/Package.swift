// swift-tools-version: 6.3

import PackageDescription

let package = Package(
    name: "native-lib",
    products: [.library(name: "native-lib", type: .dynamic, targets: ["nativelib"]) ],
    targets: [.target(name: "nativelib")]
)
