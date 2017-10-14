package net.brenig.pixelescape.render.background

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.render.WorldRenderer

/**
 * Responsible for drawing a layer of the game's background
 */
class DrawableBackgroundLayer : AbstractBackgroundLayer {

    private var width: Int = 0
    private var scrollSpeed: Float = 0.toFloat()
    private var drawable: Drawable? = null

    constructor(scrollSpeed: Float, drawable: Drawable) {
        this.scrollSpeed = scrollSpeed
        this.drawable = drawable
        width = drawable.minWidth.toInt()
    }

    constructor(width: Int, scrollSpeed: Float, drawable: Drawable) {
        this.width = width
        this.scrollSpeed = scrollSpeed
        this.drawable = drawable
    }

    override fun draw(renderer: WorldRenderer) {
        renderer.renderDrawable(drawable!!, getBackgroundWorldStart(renderer, scrollSpeed, width), 0f, renderer.world.worldWidth.toFloat(), renderer.world.worldHeight.toFloat())
    }
}
