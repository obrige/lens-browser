package com.aurora.lens.shield

data class ShieldProfile(
    val userAgent: String = "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36",
    val platform: String = "Linux armv8l",
    val vendor: String = "Google Inc.",
    val vendorSub: String = "",
    val product: String = "Gecko",
    val productSub: String = "20030107",
    val appName: String = "Netscape",
    val appVersion: String = "5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36",
    val appCodeName: String = "Mozilla",
    val language: String = "en-US",
    val languages: List<String> = listOf("en-US", "en"),
    val hardwareConcurrency: Int = 8,
    val deviceMemory: Int = 8,
    val maxTouchPoints: Int = 5,
    val devicePixelRatio: Double = 2.75,
    val screenWidth: Int = 1080,
    val screenHeight: Int = 2400,
    val availWidth: Int = 1080,
    val availHeight: Int = 2268,
    val colorDepth: Int = 24,
    val pixelDepth: Int = 24,
    val innerWidth: Int = 412,
    val innerHeight: Int = 915,
    val outerWidth: Int = 412,
    val outerHeight: Int = 915,
    val effectiveType: String = "4g",
    val downlink: Double = 10.0,
    val rtt: Int = 50,
    val timezone: String = "America/New_York",
    val glVendor: String = "ARM",
    val glRenderer: String = "Mali-G78",
    val glVersion: String = "WebGL 2.0 (OpenGL ES 3.0 Chromium)",
) {
    companion object {
        val DEFAULT = ShieldProfile()

        val DESKTOP = ShieldProfile(
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            platform = "Win32",
            screenWidth = 1920, screenHeight = 1080,
            availWidth = 1920, availHeight = 1040,
            innerWidth = 1440, innerHeight = 800,
            outerWidth = 1440, outerHeight = 900,
            maxTouchPoints = 0,
            devicePixelRatio = 1.0,
        )
    }
}
