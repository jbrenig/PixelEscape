package net.brenig.pixelescape.game.player.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingParticle
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.render.GameRenderManager
import net.brenig.pixelescape.render.WorldRenderer

class EffectShield(player: EntityPlayer) : StatusEffectTimed(player, 8F) {

    override fun render(game: PixelEscape, renderer: WorldRenderer, player: EntityPlayer, delta: Float) {
        renderer.renderManager.begin()
        renderer.renderTextureRegion(game.gameAssets.effectItemShield, (player.xPosScreen - RENDER_EFFECT_RADIUS).toFloat(), player.yPos - RENDER_EFFECT_RADIUS, RENDER_EFFECT_SIZE.toFloat(), RENDER_EFFECT_SIZE.toFloat())
    }

    override fun onPlayerCollide(): Boolean {
        if (effectActive()) {
            timeRemaining = 0f
            player.setImmortal(1f)
            val world = player.world
            for (i in 0..19) {
                val entity = world.createEntity(EntityFadingParticle::class.java)
                entity.setPosition(player.xPos + PixelEscape.rand.nextFloat() * 20 - 10, player.yPos + PixelEscape.rand.nextFloat() * 40 - 20)
                entity.setColor(Color.LIGHT_GRAY)
                entity.setFadeDuration(0.4f)
                entity.setAccelerationFactor(0.99f, 0.99f)
                entity.setVelocity(PixelEscape.rand.nextFloat() * 10 - 0.5f, (PixelEscape.rand.nextFloat() * 20 + 20) * if (entity.yPos > player.yPos) 1 else -1)
                world.spawnEntity(entity)
            }
            return false
        }
        return true
    }

    override fun updateRenderColor(renderer: GameRenderManager) {
        renderer.setColor(0.5f, 0.4f, 0.7f, 1f)
    }

    companion object {

        private val RENDER_EFFECT_RADIUS = 12
        private val RENDER_EFFECT_SIZE = RENDER_EFFECT_RADIUS * 2

        val ITEM: Item = object : Item {
            override fun getItemDrawable(assets: GameAssets): Drawable {
                return assets.itemShield
            }

            override fun onCollect(player: EntityPlayer): Boolean {
                player.addOrUpdateEffect(EffectShield(player))
                return true
            }
        }
    }
}
