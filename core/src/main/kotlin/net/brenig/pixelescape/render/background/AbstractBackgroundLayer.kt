package net.brenig.pixelescape.render.background

import net.brenig.pixelescape.render.WorldRenderer

/**
 *
 */
abstract class AbstractBackgroundLayer : IBackgroundLayer {
    override fun getBackgroundWorldStart(renderer: WorldRenderer, ratio: Float, textureSize: Int): Float {
        return renderer.worldCameraXPos + renderer.worldCameraXPos * ratio % textureSize
    }

    override fun onResize(renderer: WorldRenderer) {

    }
}
