package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.ui.Widget

/**
 * Simple Horizontal Spacer
 */
class HorizontalSpacer constructor(private val minWidth: Float = 0f, private val prefWidth: Float = Float.MAX_VALUE, private val maxWidth: Float = Float.MAX_VALUE) : Widget() {

    override fun getMinWidth(): Float {
        return minWidth
    }

    override fun getMaxWidth(): Float {
        return maxWidth
    }

    override fun getPrefWidth(): Float {
        return prefWidth
    }
}
