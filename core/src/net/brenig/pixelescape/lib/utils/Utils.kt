package net.brenig.pixelescape.lib.utils

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

/**
 * general utilities
 */
object Utils {



}

/**
 * NinePatchDrawables use their total size as minimum size by default
 * This helper function resizes them to their minimum, so they can be resized to be smaller than their total size
 *
 * @return the given, minimized Ninepatch
 */
fun NinePatchDrawable.minimize(): Drawable {
    this.minHeight = this.patch.bottomHeight + this.patch.topHeight
    this.minWidth = this.patch.leftWidth + this.patch.rightWidth
    return this
}
