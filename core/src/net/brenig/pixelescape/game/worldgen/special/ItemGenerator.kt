package net.brenig.pixelescape.game.worldgen.special

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.impl.EntityItem
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.game.player.abliity.Abilities
import net.brenig.pixelescape.game.player.effects.EffectShield
import net.brenig.pixelescape.game.player.effects.EffectSlow
import net.brenig.pixelescape.game.player.effects.EffectSmallBarricades
import net.brenig.pixelescape.game.worldgen.WeightedList
import net.brenig.pixelescape.game.worldgen.WorldGenerator
import net.brenig.pixelescape.lib.FilteredElementProvider
import net.brenig.pixelescape.lib.debug
import java.util.*

/**
 * Generator that generates [EntityItem] at specified intervalls
 */
class ItemGenerator(private val minDistance: Int, maxDistance: Int, private val startMinDistance: Int, startMaxDistance: Int, private val itemList: FilteredElementProvider<Item>) : ISpecialWorldGenerator {

    private val variableDistance: Int = maxDistance - minDistance
    private val startVariableDistance: Int = startMaxDistance - startMinDistance

    private var nextItemXPos: Int = 0

    override fun generate(generator: WorldGenerator, world: World, rand: Random, mode: GameMode) {
        if (world.currentScreenEnd + spawnOffset > nextItemXPos) {
            val entity = world.createEntity(EntityItem::class.java)
            val blockIndex = world.convertWorldCoordinateToLocalBlockIndex(nextItemXPos.toFloat())
            val minY = world.getTerrainBotHeightReal(blockIndex)
            val maxY = world.getTerrainTopHeightReal(blockIndex)

            entity.setPosition(nextItemXPos.toFloat(), (minY + rand.nextInt(maxY - minY)).toFloat())
            entity.item = itemList.getRandomValue(rand)

            world.spawnEntity(entity)

            debug("Item spawned: " + entity.item + ", @x: " + entity.xPos + ", y: " + entity.yPos)

            calculateNextItemXPos(rand)
        }
    }

    private fun calculateNextItemXPos(random: Random) {
        nextItemXPos += minDistance + random.nextInt(variableDistance)
    }

    override fun reset(world: World) {
        nextItemXPos = world.worldWidth + startMinDistance + world.random.nextInt(startVariableDistance)
    }

    companion object {

        private const val spawnOffset = 40

        fun createDefaultItemList(): WeightedList<Item> {
            val out = WeightedList<Item>()
            out.add(10, Abilities.BLINK)
            out.add(5, EffectShield.ITEM)
            out.add(10, EffectSlow.ITEM)
            out.add(8, EffectSmallBarricades.ITEM)
            return out
        }
    }
}
