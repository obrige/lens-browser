package com.aurora.lens.shield

import kotlin.random.Random

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
    val glRenderer: String = "Mali-G710",
    val glVersion: String = "WebGL 2.0 (OpenGL ES 3.0 Chromium)",
    val canvasNoiseSeed: Int = 0,
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
            glRenderer = "ANGLE (NVIDIA, NVIDIA GeForce RTX 3060 Direct3D11 vs_5_0 ps_5_0)",
        )

        fun varied(): ShieldProfile {
            val r = Random(System.nanoTime())
            return ShieldProfile(
                screenWidth = DEFAULT.screenWidth + r.nextInt(-2, 3),
                screenHeight = DEFAULT.screenHeight + r.nextInt(-4, 5),
                availHeight = DEFAULT.availHeight + r.nextInt(-4, 5),
                innerWidth = DEFAULT.innerWidth + r.nextInt(-2, 3),
                innerHeight = DEFAULT.innerHeight + r.nextInt(-2, 3),
                outerWidth = DEFAULT.outerWidth + r.nextInt(-2, 3),
                outerHeight = DEFAULT.outerHeight + r.nextInt(-2, 3),
                devicePixelRatio = DEFAULT.devicePixelRatio + r.nextDouble(-0.05, 0.06),
                hardwareConcurrency = DEFAULT.hardwareConcurrency + r.nextInt(-1, 2),
                deviceMemory = DEFAULT.deviceMemory + r.nextInt(-1, 2),
                effectiveType = listOf("4g", "4g", "4g", "3g").random(r),
                rtt = DEFAULT.rtt + r.nextInt(-10, 21),
                canvasNoiseSeed = r.nextInt(),
            )
        }
    }
}
