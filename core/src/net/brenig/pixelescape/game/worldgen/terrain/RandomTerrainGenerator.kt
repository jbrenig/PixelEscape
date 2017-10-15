package net.brenig.pixelescape.game.worldgen.terrain

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.worldgen.TerrainPair
import net.brenig.pixelescape.lib.Reference
import java.util.*

/**
 * Generator that randomly generates terrain based on last terrain generated
 */
class RandomTerrainGenerator(weight: Int) : AbstractTerrainGenerator(weight) {

    override fun generate(world: World, lastPair: TerrainPair, blocksToGenerate: Int, generatedBlocksIndex: Int, random: Random): Int {
        val pair = world.createTerrainPairForGeneration
        pair.bot = Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.bot + random.nextInt(3) - 1))
        pair.top = Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.top + random.nextInt(3) - 1))
        while (pair.top + pair.bot > Reference.MAX_TERRAIN_SUM) {
            pair.top = pair.top - 1
            pair.bot = pair.bot - 1
        }
        return 1
    }

    override fun getMinGenerationLength(last: TerrainPair): Int {
        return MIN_GENERATION_LENGTH
    }

    companion object {

        private const val MIN_GENERATION_LENGTH = 1
    }
}
