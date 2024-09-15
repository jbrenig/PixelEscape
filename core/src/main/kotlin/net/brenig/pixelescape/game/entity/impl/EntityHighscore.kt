package net.brenig.pixelescape.game.entity.impl

import com.badlogic.gdx.graphics.Color
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.game.entity.impl.particle.EntityCrashParticle
import net.brenig.pixelescape.render.WorldRenderer

/**
 * dummy entity, that renders the current highscore
 */
class EntityHighscore : Entity() {

    override var isDead: Boolean = false
        get() = field || super.isDead

    var score: Int = Int.MAX_VALUE
        set(value) {
            field = value
            xPos = world.player.xPos + (value - world.player.score)
        }

    var color: Color = Color.BLUE

    init {
        xPos = score.toFloat()
        yPos = 0F
    }

    override fun update(delta: Float, inputManager: InputManager, gameMode: GameMode): Boolean {
        val target = world.player.xPos + (score - world.player.score)
        val deltaX = target - xPos
        when {
            Math.abs(deltaX) < 1 -> {}
            xPos > world.currentScreenEnd -> xPos = Math.max(world.currentScreenEnd, target)
            else -> {
                var change = 0.5F * deltaX
                if (Math.abs(change) < 1) {
                    change = Math.signum(deltaX)
                }
                xPos += delta * change
            }
        }
        yPos = 0F
        return super.update(delta, inputManager, gameMode)
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (minX < world.currentScreenEnd) {
            val pos = xPos
            if (world.player.xPos > pos && !isDead) {
                val random = world.random
                val yEnd = world.getTerrainTopHeightRealForCoord(pos.toInt())
                val yStart = world.getTerrainBotHeightRealForCoord(pos.toInt())
                val yDiff = yEnd - yStart
                val maxCount = yDiff / EntityCrashParticle.SIZE
                val yOffset = (yDiff - maxCount * EntityCrashParticle.SIZE) / 2
                for (i in 0 until maxCount) {
                    val e = world.createEntity(EntityCrashParticle::class.java)
                    e.setPosition(pos, (i * EntityCrashParticle.SIZE + yOffset + yStart).toFloat())
                    e.color = color
                    e.setVelocity((random.nextFloat() - 0.5f) * 80, (random.nextFloat() - 0.5f) * 40)
                    world.spawnEntity(e)
                }
                val scoreModifier = 1 - 1 / (world.player.xVelocity * 0.1f)
                renderer.applyForceToScreen((2 + random.nextFloat()) * scoreModifier * 0.2f, 0f)
                isDead = true
            }
            renderer.renderManager.setColor(color)
            renderer.renderRectWorld(pos, yPos, EntityCrashParticle.SIZE.toFloat(), world.worldHeight.toFloat())
        }
    }

    override fun reset() {
        super.reset()
        isDead = false
        score = Int.MAX_VALUE
        xPos = score.toFloat()
        yPos = 0F
        color = Color.BLUE
    }
}
