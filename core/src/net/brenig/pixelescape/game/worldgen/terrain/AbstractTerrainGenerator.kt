package net.brenig.pixelescape.game.worldgen.terrain

import net.brenig.pixelescape.game.worldgen.ITerrainGenerator

/**
 * Basic terrain generator functionality
 */
abstract class AbstractTerrainGenerator(override val weight: Int) : ITerrainGenerator {
}
