package net.brenig.pixelescape.render.background

import net.brenig.pixelescape.render.WorldRenderer

/**
 * Draws something in the game's background
 */
interface IBackgroundLayer {

    fun draw(renderer: WorldRenderer)

    /**
     * @return where a texture would need to be rendered to be `ratio` time slower than the foreground
     */
    fun getBackgroundWorldStart(renderer: WorldRenderer, ratio: Float, textureSize: Int): Float

    fun onResize(renderer: WorldRenderer)
}
