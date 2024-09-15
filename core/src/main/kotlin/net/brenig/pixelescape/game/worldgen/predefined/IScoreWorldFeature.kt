package net.brenig.pixelescape.game.worldgen.predefined

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.entity.Entity

/**
 * one-time world generator, that generates something at a specific predefined location
 */
interface IScoreWorldFeature {

    /**
     * @return the world coordinate which, when visible should trigger the spawn of the WorldFeature-Entity
     */
    val spawnX: Float

    /**
     * create the entity to be spawned
     */
    fun createWorldFeature(): Entity

    fun reset(world: World)

}
