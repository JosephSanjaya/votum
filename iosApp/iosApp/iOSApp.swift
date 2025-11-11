import SwiftUI
import composeApp

@main
struct iOSApp: App {
    init() {
        KoinInitializer_iosKt.startKoinPlatform(context: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
