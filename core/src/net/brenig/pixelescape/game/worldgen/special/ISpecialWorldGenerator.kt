package net.brenig.pixelescape.game.worldgen.special

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.worldgen.WorldGenerator
import java.util.*

/**
 * Spawn special World features<br></br>
 * Gets called every tick
 */
interface ISpecialWorldGenerator {

    /**
     * generate this element
     *
     * @param generator current worldgenerator
     * @param world     current world
     * @param rand      Random instance to use
     * @param mode      current gamemode
     */
    fun generate(generator: WorldGenerator, world: World, rand: Random, mode: GameMode)

    /**
     * reset worldgenerator (--> eg. on game restart)
     *
     * @param world the new world
     */
    fun reset(world: World)
}
