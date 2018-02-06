package net.brenig.pixelescape.game.entity.impl.particle

import com.badlogic.gdx.graphics.Color
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.CollisionType
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.render.WorldRenderer


/**
 * Particle entity that spawns when the player dies
 */
class EntityCrashParticle : Entity() {

    private var xVel = 0f
    private var yVel = 0f

    var radius: Int = RADIUS
    private val size: Int get() = radius * 2

    var color: Color = Color.BLACK

    /**
     * set whether the particle should collide with the top terrain<br></br>
     * true is default
     */
    var collideTop = true

    override var isDead: Boolean = false
        get() {
            if (field) return true
            val renderX = world.convertWorldCoordToScreenCoord(xPos)
            field = yPos - radius >= world.worldHeight || yPos + radius <= 0 || renderX - radius >= world.worldWidth || renderX + radius <= 0
            return field
        }
        private set(value) {
            field = value
        }


    fun setVelocity(xVel: Float, yVel: Float) {
        this.xVel = xVel
        this.yVel = yVel
    }

    override fun render(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (isDead) {
            return
        }
        //Move
        xPos += xVel * delta
        yPos += yVel * delta

        //Accelerate and collide
        val collision = doesCollide()
        when (collision) {
            CollisionType.TERRAIN_BOT_RIGHT -> {
                if (xVel > 0) { bounceX() }
                else { slowDownX(delta) }

                if (yVel <= 0) { bounceY() }
                else { gravityY(delta) }
            }
            CollisionType.TERRAIN_BOT_LEFT -> {
                if (xVel < 0) { bounceX() }
                else { slowDownX(delta) }

                if (yVel <= 0) { bounceY() }
                else { gravityY(delta) }
            }
            CollisionType.TERRAIN_BOTTOM ->
                if (yVel <= 0) {
                    bounceY()
                    if (yVel != 0f) {
                        slowDownX(delta * 10)
                    } else {
                        if (xVel > ANTI_BOUNCE_X) {
                            slowDownX(delta * 10)
                        } else {
                            xVel = 0f
                        }
                    }
                } else {
                    slowDownX(delta)
                    gravityY(delta)
                }
            CollisionType.TERRAIN_RIGHT -> {
                if (xVel > 0) { bounceX() }
                else { slowDownX(delta) }

                gravityY(delta)
            }
            CollisionType.TERRAIN_LEFT -> {
                if (xVel < 0) { bounceX() }
                else { slowDownX(delta) }

                gravityY(delta)
            }
            CollisionType.TERRAIN_TOP -> {
                if (collideTop) {
                    slowDownX(delta)

                    if (yVel > 0) { bounceY() }
                    else { gravityY(delta) }
                } else {
                    slowDownX(delta)
                    gravityY(delta)
                }
            }
            CollisionType.ENTITY -> {
                if (xVel > 0) { bounceX() }
                else { slowDownX(delta) }
            }
            CollisionType.TERRAIN_TOP_RIGHT -> {
                if (collideTop) {
                    if (xVel > 0)  { bounceX() }
                    else { slowDownX(delta) }

                    if (yVel > 0) { bounceY() }
                    else { gravityY(delta) }
                } else {
                    slowDownX(delta)
                    gravityY(delta)
                }
            }
            CollisionType.TERRAIN_TOP_LEFT -> {
                if (collideTop) {
                    if (xVel < 0)  { bounceX() }
                    else { slowDownX(delta) }

                    if (yVel > 0) { bounceY() }
                    else { gravityY(delta) }
                } else {
                    slowDownX(delta)
                    gravityY(delta)
                }
            }
            CollisionType.NONE -> {
                slowDownX(delta)
                gravityY(delta)
            }
            else -> {
                slowDownX(delta)
                gravityY(delta)
            }
        }

        xVel = Math.min(gameMode.maxEntitySpeed, xVel)
        yVel = Math.min(gameMode.maxEntitySpeed, yVel)

        game.renderManager.begin()
        game.renderManager.setColor(color)
        renderer.renderRectWorld(xPos - radius, yPos - radius, size.toFloat(), size.toFloat())
    }

    private fun slowDownX(delta: Float) {
        xVel -= xVel * 0.5f * delta
    }

    private fun gravityY(delta: Float) {
        yVel += Reference.GRAVITY_ACCELERATION * delta
    }

    private fun bounceX() {
        if (Math.abs(xVel) > ANTI_BOUNCE_X) {
            this.xVel = -xVel * BOUNCE_X
        } else {
            xVel = 0f
        }
    }

    private fun bounceY() {
        if (Math.abs(yVel) > ANTI_BOUNCE_Y) {
            this.yVel = -yVel * BOUNCE_Y
        } else {
            yVel = 0f
        }
    }

    private fun doesCollide(): CollisionType {
        return world.doesAreaCollideWithWorld(xPos - radius, yPos - radius, xPos + radius, yPos + radius)
    }

    override fun reset() {
        super.reset()
        xVel = 0f
        yVel = 0f
        color = Color.BLACK
        collideTop = true
        isDead = false
        radius = RADIUS
    }

    companion object {

        const val RADIUS = 3
        const val SIZE = RADIUS * 2

        private const val BOUNCE_X = 0.4f
        private const val BOUNCE_Y = 0.1f

        private const val ANTI_BOUNCE_X = 10f
        private const val ANTI_BOUNCE_Y = 20f
    }
}
