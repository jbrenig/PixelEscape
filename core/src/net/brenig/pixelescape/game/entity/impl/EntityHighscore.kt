package net.brenig.pixelescape.game.entity.impl

import com.badlogic.gdx.graphics.Color
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.World
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

    override var world: World
        get() = super.world
        set(world) {
            super.world = world
            xPos = (world.screen.game.userData.getHighScore(world.screen.gameMode) + world.player.xPosScreen).toFloat()
            yPos = 0f
        }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (minX < world.currentScreenEnd) {
            val pos = xPos - world.player.bonusScore
            if (world.player.xPos > pos) {
                val random = world.random
                val yEnd = world.getTerrainTopHeightRealForCoord(pos.toInt())
                val yStart = world.getTerrainBotHeightRealForCoord(pos.toInt())
                val yDiff = yEnd - yStart
                val maxCount = yDiff / EntityCrashParticle.SIZE
                val yOffset = (yDiff - maxCount * EntityCrashParticle.SIZE) / 2
                for (i in 0 until maxCount) {
                    val e = world.createEntity(EntityCrashParticle::class.java)
                    e.setPosition(pos, (i * EntityCrashParticle.SIZE + yOffset + yStart).toFloat())
                    e.setColor(Color.BLUE)
                    e.setVelocity((random.nextFloat() - 0.5f) * 80, (random.nextFloat() - 0.5f) * 40)
                    world.spawnEntity(e)
                }
                val scoreModifier = 1 - 1 / (world.player.xVelocity * 0.1f)
                renderer.applyForceToScreen((2 + random.nextFloat()) * scoreModifier * 0.2f, 0f)
                isDead = true
            }
            renderer.renderManager.setColor(Color.BLUE)
            renderer.renderRectWorld(pos, yPos, EntityCrashParticle.SIZE.toFloat(), world.worldHeight.toFloat())
        }
    }

    override fun reset() {
        super.reset()
        isDead = false
    }
}
