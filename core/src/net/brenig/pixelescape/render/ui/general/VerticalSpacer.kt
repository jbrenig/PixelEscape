package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.ui.Widget

/**
 * Simple Vertical Spacer
 */
class VerticalSpacer @JvmOverloads constructor(private val minHeight: Float = 0f, private val prefHeight: Float = java.lang.Float.MAX_VALUE, private val maxHeight: Float = java.lang.Float.MAX_VALUE) : Widget() {

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
