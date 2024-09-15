package net.brenig.pixelescape.render

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * simple animation using [com.badlogic.gdx.graphics.g2d.Animation]
 */
class SimpleAnimation constructor(cols: Int, rows: Int, texture: TextureRegion, frameTime: Float, playMode: Animation.PlayMode = Animation.PlayMode.LOOP) {

    private var frameTime = 0f
    val animation: Animation<TextureRegion>

    init {
        animation = createAnimationFromTexture(cols, rows, texture, frameTime)
        animation.playMode = playMode
    }

    /**
     * renders the animation
     *
     * @param renderer renderer instance
     * @param xPos     the x-position on screen
     * @param yPos     the y-position on screen
     * @param width    width of the animation
     * @param height   height of the animation
     * @param delta    time since last frame
     */
    fun render(renderer: GameRenderManager, xPos: Float, yPos: Float, width: Float, height: Float, delta: Float) {
        frameTime += delta
        renderer.begin()
        renderer.draw(animation.getKeyFrame(frameTime), xPos, yPos, width, height)
    }

    /**
     * renders the animation
     *
     * @param renderer renderer instance
     * @param xPos     the x-position on screen
     * @param yPos     the y-position on screen
     * @param delta    time since last frame
     */
    fun render(renderer: GameRenderManager, xPos: Float, yPos: Float, delta: Float) {
        frameTime += delta
        renderer.begin()
        renderer.draw(animation.getKeyFrame(frameTime), xPos, yPos)
    }

    /**
     * increments internal frame timer and returns current frame
     */
    fun getFrameAfterTimePassed(delta: Float): TextureRegion {
        frameTime += delta
        return animation.getKeyFrame(frameTime)
    }

    companion object {

        /**
         * creates an [Animation] from the given texture
         */
        fun createAnimationFromTexture(cols: Int, rows: Int, texture: TextureRegion, frameTime: Float): Animation<TextureRegion> {
            val tmp = texture.split(texture.regionWidth / cols, texture.regionHeight / rows)
            val frames = arrayOfNulls<TextureRegion>(cols * rows)
            var index = 0
            for (y in 0 until rows) {
                for (x in 0 until cols) {
                    frames[index++] = tmp[y][x]
                }
            }
            return Animation<TextureRegion>(frameTime, *frames)
        }
    }
}
