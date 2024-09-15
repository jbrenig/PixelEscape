@file:JvmName("Lwjgl3Launcher")

package net.brenig.pixelescape.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import net.brenig.pixelescape.PixelEscape

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(PixelEscape(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("PixelEscape")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(256, 128, 96, 64, 48, 32).map { "window_rounded_$it.png" }.toTypedArray()))
    })
}
