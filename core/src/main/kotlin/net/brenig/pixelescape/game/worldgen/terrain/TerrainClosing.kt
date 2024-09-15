package net.brenig.pixelescape.game.worldgen.terrain

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator
import net.brenig.pixelescape.game.worldgen.TerrainPair
import net.brenig.pixelescape.game.data.constants.Reference
import java.util.*

/**
 * Generator that generates a closing corridor
 */
class TerrainClosing(override val weight: Int) : ITerrainGenerator {

    override fun generate(world: World, lastPair: TerrainPair, blocksToGenerate: Int, generatedBlocksIndex: Int, random: Random): Int {
        val max = Math.min(blocksToGenerate, MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1))
        var generated = 0
        var bot = lastPair.top
        var top = lastPair.bot
        while (bot <= Reference.MAX_HEIGHT && top <= Reference.MAX_HEIGHT && bot + top <= Reference.MAX_TERRAIN_SUM && generated < max) {
            val pair = world.createTerrainPairForGeneration
            pair.bot = top
            pair.top = bot
            top++
            bot++
            generated++
        }
        return generated
    }

    override fun getMinGenerationLength(last: TerrainPair): Int {
        val bot = last.top + MIN_GENERATION_LENGTH - 1
        val top = last.bot + MIN_GENERATION_LENGTH - 1
        return if (bot > Reference.MAX_HEIGHT || top > Reference.MAX_HEIGHT || bot + top > Reference.MAX_TERRAIN_SUM) {
            0
        } else MIN_GENERATION_LENGTH
    }

    companion object {

        private const val MIN_GENERATION_LENGTH = 3
        private const val MAX_GENERATION_LENGTH = 6
    }
}
