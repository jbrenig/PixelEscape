package net.brenig.pixelescape.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.render.background.IBackgroundLayer
import java.util.*

/**
 * Helper class for rendering a [World]
 * Renders with y-Up
 */
class WorldRenderer(private val game: PixelEscape, val world: World) {

    var rendererYOffset = 0f
        private set

    var xPos = 0f
        private set

    /**
     * @return the target x position of the worldrenderer
     */
    var targetX = 0f
        private set

    private var movementSpeedX = 0f

    var screenShakeX = 0f
        private set
    var screenShakeY = 0f
        private set

    private var screenShakeForceX = 0f
    private var screenShakeForceY = 0f

    private var screenShakeTimerX = 0f
    private var screenShakeTimerY = 0f

    //gets updated every frame
    //total offsets
    var currentTotalXOffset = 0f
        private set
    var currentTotalYOffset = 0f
        private set

    private val backgroundLayers = ArrayList<IBackgroundLayer>()

    val renderManager: GameRenderManager
        get() = game.renderManager

    /**
     * @return global coordinate of this worldrenderer (left screen edge)
     */
    val worldCameraXPos: Float
        get() = -xPos - screenShakeX + world.getPlayer().progress

    fun addBackgroundLayer(layer: IBackgroundLayer) {
        backgroundLayers.add(layer)
    }

    /**
     * calculate screen shake effect
     *
     * @param delta time since last tick
     */
    private fun shakeScreen(delta: Float) {
        if (screenShakeForceX > 0) {
            screenShakeTimerX += delta * (screenShakeLengthMod + PixelEscape.rand.nextFloat())
        }
        if (screenShakeForceY > 0) {
            screenShakeTimerY += delta * (screenShakeLengthMod + PixelEscape.rand.nextFloat())
        }
        if (screenShakeTimerX >= screenShakeForceX) {
            screenShakeTimerX = 0f
            screenShakeForceX = screenShakeTimerX
            screenShakeX = screenShakeForceX
        } else {
            val difX = screenShakeForceX - screenShakeTimerX
            screenShakeX = (Math.sin((screenShakeTimerX * screenShakeSpeed + world.random.nextFloat() * screenShakeNoise).toDouble()) * difX).toFloat() * screenShakeForceMult
        }
        if (screenShakeTimerY >= screenShakeForceY) {
            screenShakeTimerY = 0f
            screenShakeForceY = screenShakeTimerY
            screenShakeY = screenShakeForceY
        } else {
            val difY = screenShakeForceY - screenShakeTimerY
            screenShakeY = (Math.sin((screenShakeTimerY * screenShakeSpeed + world.random.nextFloat() * screenShakeNoise).toDouble()) * difY).toFloat() * screenShakeForceMult
        }
    }

    /**
     * initiates a screen shake effect
     *
     * @param x force on x axis
     * @param y force on y axis
     */
    fun applyForceToScreen(x: Float, y: Float) {
        if (x * Math.PI > screenShakeForceX) {
            screenShakeForceX = (x * Math.PI).toFloat()
        }
        if (y * Math.PI > screenShakeForceY) {
            screenShakeForceY = (y * Math.PI).toFloat()
        }
    }

    /**
     * move the camera to the specified world coordinate (at the specified speed)
     */
    fun moveScreenTo(x: Float, movementSpeedX: Float) {
        this.targetX = x
        this.movementSpeedX = movementSpeedX
    }

    private fun moveScreen(delta: Float) {
        if (movementSpeedX != 0f && xPos != targetX) {
            if (targetX < xPos) {
                xPos -= Math.min(xPos - targetX, movementSpeedX * delta)
            } else {
                xPos += Math.min(targetX - xPos, movementSpeedX * delta)
            }
        }
    }

    /**
     * Renders the World
     */
    fun render(delta: Float) {
        if (game.gameDebugSettings.getBoolean("SCREEN_SHAKE")) {
            shakeScreen(delta)
        }
        moveScreen(delta)
        currentTotalXOffset = xPos + screenShakeX
        currentTotalYOffset = rendererYOffset + screenShakeY

        renderWorldBackground()
        renderEntitiesBackground(delta)
        renderWorld(delta)
        renderEntities(delta)
    }

    /**
     * renders terrain background
     */
    private fun renderWorldBackground() {
        for (layer in backgroundLayers) {
            layer.draw(this)
        }
    }

    /**
     * renders entities in background
     */
    private fun renderEntitiesBackground(delta: Float) {
        for (e in world.entityList) {
            e.renderBackground(game, this, world.screen.gameMode, delta)
        }
    }

    /**
     * renders entities
     */
    private fun renderEntities(delta: Float) {
        for (e in world.entityList) {
            e.render(game, this, world.screen.gameMode, delta)
        }
    }

    /**
     * renders terrain
     */
    private fun renderWorld(delta: Float) {
        game.renderManager.disableBlending()
        game.renderManager.begin()
        game.renderManager.setColor(0f, 0f, 0f, 1f)

        for (index in world.cameraLeftLocalIndex until world.cameraRightLocalIndex + 1) {
            world.getTerrainPairForIndex(index).render(game, world, currentTotalXOffset + getBlockPositionFromLocalIndex(index), rendererYOffset, screenShakeY, delta)
        }
        game.renderManager.enableBlending()
    }

    /**
     * renders a rectangle using [com.badlogic.gdx.graphics.glutils.ShapeRenderer] and [GameRenderManager]
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderRect(x: Float, y: Float, width: Float, height: Float) {
        game.renderManager.rect(currentTotalXOffset + x, currentTotalYOffset + y, width, height)
    }

    /**
     * same as [.renderRect], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderRectWorld(x: Float, y: Float, width: Float, height: Float) {
        renderRect(world.convertWorldCoordToScreenCoord(x), y, width, height)
    }


    /**
     * renders a [Drawable] using [GameRenderManager]
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderDrawable(drawable: Drawable, x: Float, y: Float, width: Float, height: Float) {
        drawable.draw(game.renderManager.batch, currentTotalXOffset + x, currentTotalYOffset + y, width, height)
    }

    /**
     * same as [.renderDrawable], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderDrawableWorld(drawable: Drawable, x: Float, y: Float, width: Float, height: Float) {
        renderDrawable(drawable, world.convertWorldCoordToScreenCoord(x), y, width, height)
    }

    /**
     * renders a [TextureRegion] using [GameRenderManager]
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderTextureRegion(region: TextureRegion, x: Float, y: Float, width: Float, height: Float) {
        game.renderManager.batch.draw(region, currentTotalXOffset + x, currentTotalYOffset + y, width, height)
    }

    /**
     * same as [.renderTextureRegion], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderTextureRegionWorld(region: TextureRegion, x: Float, y: Float, width: Float, height: Float) {
        renderTextureRegion(region, world.convertWorldCoordToScreenCoord(x), y, width, height)
    }

    /**
     * renders an animation in world
     */
    fun renderSimpleAnimationWorld(animation: SimpleAnimation, x: Float, y: Float, width: Float, height: Float, delta: Float) {
        renderTextureRegionWorld(animation.getFrameAfterTimePassed(delta), x, y, width, height)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderText(text: String, x: Float, y: Float) {
        renderManager.draw(text, currentTotalXOffset + x, currentTotalYOffset + y)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderText(text: String, color: Color, x: Float, y: Float) {
        renderManager.draw(text, color, currentTotalXOffset + x, currentTotalYOffset + y)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderText(text: String, color: Color, x: Float, y: Float, size: Float) {
        renderManager.draw(text, color, currentTotalXOffset + x, currentTotalYOffset + y, size)
    }

    /**
     * same as [.renderText], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderTextWorld(text: String, x: Float, y: Float) {
        renderText(text, world.convertWorldCoordToScreenCoord(x), y)
    }

    /**
     * same as [.renderText], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderTextWorld(text: String, color: Color, x: Float, y: Float) {
        renderText(text, color, world.convertWorldCoordToScreenCoord(x), y)
    }

    /**
     * same as [.renderText], but using global coordinates
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun renderTextWorld(text: String, color: Color, x: Float, y: Float, size: Float) {
        renderText(text, color, world.convertWorldCoordToScreenCoord(x), y, size)
    }

    private fun getBlockPositionFromLocalIndex(index: Int): Float {
        return world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(index))
    }

    /**
     * sets current position of the world renderer (world view)
     */
    fun setCameraXPosition(x: Float) {
        this.xPos = x
    }

    /**
     * sets the camera position of the world renderer (also gets set as target position)
     */
    fun setXCameraOffsetAbsolute(x: Float) {
        setCameraXPosition(x)
        targetX = x
        movementSpeedX = 0f
    }

    /**
     * sets the y offset of the world renderer (x offset is not supported)
     */
    fun setWorldRendererYOffset(yOffset: Float) {
        rendererYOffset = yOffset
    }

    fun onResize() {
        for (layer in backgroundLayers) {
            layer.onResize(this)
        }
    }

    companion object {

        private const val screenShakeSpeed = 8f
        private const val screenShakeLengthMod = 7f
        private const val screenShakeForceMult = 6f
        private const val screenShakeNoise = 1.4f
    }
}
