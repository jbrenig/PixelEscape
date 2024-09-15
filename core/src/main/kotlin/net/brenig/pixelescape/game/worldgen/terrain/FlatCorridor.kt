package net.brenig.pixelescape.game.worldgen.terrain

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator
import net.brenig.pixelescape.game.worldgen.TerrainPair
import java.util.*

/**
 * Generator that generates a horizontal corridor
 */
class FlatCorridor(override val weight: Int) : ITerrainGenerator {


    override fun generate(world: World, lastPair: TerrainPair, blocksToGenerate: Int, generatedBlocksIndex: Int, random: Random): Int {
        val generatedBlocks = Math.min(blocksToGenerate, MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1))
        for (i in 0 until generatedBlocks) {
            val pair = world.createTerrainPairForGeneration
            pair.top = lastPair.top
            pair.bot = lastPair.bot
        }
        return generatedBlocks
    }

    override fun getMinGenerationLength(last: TerrainPair): Int {
        return MIN_GENERATION_LENGTH
    }

    companion object {

        private const val MIN_GENERATION_LENGTH = 2
        private const val MAX_GENERATION_LENGTH = 4
    }
}
