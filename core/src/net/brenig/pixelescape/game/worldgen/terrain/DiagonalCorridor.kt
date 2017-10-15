package net.brenig.pixelescape.game.worldgen.terrain

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.worldgen.TerrainPair
import java.util.*

/**
 * Generator that generates diagonal corridors
 */
class DiagonalCorridor(weight: Int) : AbstractTerrainGenerator(weight) {

    override fun generate(world: World, lastPair: TerrainPair, blocksToGenerate: Int, generatedBlocksIndex: Int, random: Random): Int {
        var up = random.nextBoolean()
        if (up) {
            if (lastPair.bot - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
                up = false
            }
        } else {
            if (lastPair.top - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
                up = true
            }
        }
        val generateAmount = Math.min(blocksToGenerate, Math.min(MAX_GENERATION_LENGTH, (if (up) lastPair.bot else lastPair.top) - MIN_HEIGHT + 1))
        if (generateAmount <= 0) {
            return 0
        }
        for (i in 0 until generateAmount) {
            val pair = world.createTerrainPairForGeneration
            if (up) {
                pair.bot = lastPair.bot - i
                pair.top = lastPair.top + i
            } else {
                pair.bot = lastPair.bot + i
                pair.top = lastPair.top - i
            }
        }
        return generateAmount
    }

    override fun getMinGenerationLength(last: TerrainPair): Int {
        return if (last.top - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT && last.bot - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT) {
            0
        } else MIN_GENERATION_LENGTH
    }

    companion object {

        private const val MIN_GENERATION_LENGTH = 4
        private const val MAX_GENERATION_LENGTH = 20

        /**
         * maximal Terrain height
         */
        private const val MIN_HEIGHT = 3
    }
}
