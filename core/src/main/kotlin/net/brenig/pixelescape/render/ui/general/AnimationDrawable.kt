package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import net.brenig.pixelescape.render.SimpleAnimation

/**
 *
 */
class AnimationDrawable : BaseDrawable {

    private var frameTime = 0f
    private var animation: Animation<TextureRegion>? = null

    constructor(animation: SimpleAnimation) : super() {
        this.animation = animation.animation
    }

    constructor(animation: Animation<TextureRegion>) : super() {
        this.animation = animation
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        frameTime += Gdx.graphics.deltaTime
        batch.draw(animation!!.getKeyFrame(frameTime), x, y, width, height)
    }
}
