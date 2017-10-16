package net.brenig.pixelescape.game.player.abliity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingParticle
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.screen.GameScreen

class AbilityBlink : Ability(maxCooldown) {

    override fun onActivate(screen: GameScreen, world: World, player: EntityPlayer): Boolean {
        for (i in 0..59) {
            val e = world.createEntity(EntityFadingParticle::class.java)
            e.setPosition(player.xPos + PixelEscape.rand.nextFloat() * 20 - 10, player.yPos + PixelEscape.rand.nextFloat() * 40 - 20)
            e.setColor(Color.BLUE)
            e.setFadeDuration(0.4f)
            e.setAccelerationFactor(0.99f, 0.99f)
            e.setVelocity(PixelEscape.rand.nextFloat() * 10 - 0.5f, (PixelEscape.rand.nextFloat() * 20 + 20) * if (e.yPos > player.yPos) 1 else -1)
            world.spawnEntity(e)
        }
        player.increaseXPos(range)

        val oldX = screen.worldRenderer.targetX
        screen.worldRenderer.setCameraXPosition(screen.worldRenderer.xPos + range)
        screen.worldRenderer.moveScreenTo(oldX, (Reference.BLOCK_WIDTH * 4).toFloat())
        return true
    }

    override fun getDrawable(assets: GameAssets): Drawable {
        return assets.itemBlink
    }

    companion object {

        private val maxCooldown = 10f
        private val range = (Reference.BLOCK_WIDTH * 4).toFloat()
    }
}
