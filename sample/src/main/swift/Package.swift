// swift-tools-version: 6.2

import PackageDescription

let package = Package(
    name: "native-lib",
    products: [
        .library(name: "Fractals", targets: ["Fractals"]),
        .library(name: "native-lib", type: .dynamic, targets: ["Lib"])
    ],
    targets: [.target(name: "Lib", dependencies: ["Fractals"]), .target(name: "Fractals")]
)
