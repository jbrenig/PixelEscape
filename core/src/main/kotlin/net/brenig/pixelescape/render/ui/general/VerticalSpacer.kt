package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.ui.Widget

/**
 * Simple Vertical Spacer
 */
class VerticalSpacer constructor(private val minHeight: Float = 0f, private val prefHeight: Float = Float.MAX_VALUE, private val maxHeight: Float = Float.MAX_VALUE) : Widget() {

    override fun getMinHeight(): Float {
        return minHeight
    }

    override fun getMaxHeight(): Float {
        return maxHeight
    }

    override fun getPrefHeight(): Float {
        return prefHeight
    }
}
