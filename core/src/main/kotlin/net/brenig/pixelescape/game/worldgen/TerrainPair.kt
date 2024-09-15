package net.brenig.pixelescape.game.worldgen

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.constants.Reference

/**
 * Used to store terrain
 */
class TerrainPair(bot: Int, top: Int) {

    var bot: Int = 0
    var top: Int = 0

    init {
        this.bot = bot
        this.top = top
    }

    fun render(game: PixelEscape, world: World, x: Float, y: Float, yTranslation: Float, delta: Float) {
        game.renderManager.begin()
        //Draw Bottom (y=0) blocks
        game.renderManager.rect(x, y, Reference.BLOCK_WIDTH.toFloat(), bot * Reference.BLOCK_WIDTH + yTranslation)
        //Draw Top (y=worldHeight) blocks
        game.renderManager.rect(x, y + world.worldHeight, Reference.BLOCK_WIDTH.toFloat(), (top * Reference.BLOCK_WIDTH - yTranslation) * -1)

    }
}
