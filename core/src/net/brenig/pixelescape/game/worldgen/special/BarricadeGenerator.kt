package net.brenig.pixelescape.game.worldgen.special

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.impl.EntityBarricade
import net.brenig.pixelescape.game.worldgen.WorldGenerator
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.debug
import java.util.*

class BarricadeGenerator
/**
 * @param barricadeOffset pixels until the next barricade is generated
 */
(private val barricadeOffset: Int) : ISpecialWorldGenerator {

    private var nextBarricadePosition: Int = 0

    override fun generate(generator: WorldGenerator, world: World, rand: Random, mode: GameMode) {
        if (mode.shouldGenerateBarricades(world)) {
            if (world.currentScreenEnd + spawnOffset > nextBarricadePosition) {
                val barricade = world.createEntity(EntityBarricade::class.java)
                val newXPos = nextBarricadePosition
                val newYPos = rand.nextInt(world.worldHeight - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH
                barricade.setPosition(newXPos.toFloat(), newYPos.toFloat())
                barricade.applyWorldGenSizeModifier(generator.obstacleSizeModifier)
                updateBarricade(world, barricade, mode)
                world.spawnEntity(barricade)

                nextBarricadePosition = newXPos + barricadeOffset
            }
        }
    }

    /**
     * checks blocks around the Barricade and tries to position the barricade (y-Pos) so that it is possible to get past is on higher player speeds
     *
     * @param world    world instance
     * @param b        the barricade that needs to be moved
     * @param gameMode current gamemode
     */
    private fun updateBarricade(world: World, b: EntityBarricade, gameMode: GameMode) {
        val localIndex = world.convertWorldCoordinateToLocalBlockIndex(b.xPos)
        //LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);
        if (localIndex >= 0) {
            //LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);

            //Move barricade into level
            if (b.yPos < world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
                b.yPos = (world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH).toFloat()
            } else if (b.yPos > world.worldHeight - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
                b.yPos = (world.worldHeight - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH).toFloat()
            }

            //Leave gap for player
            val checkRadius = (world.player.xVelocity / gameMode.maxEntitySpeed * Reference.OBSTACLE_X_CHECK_RADIUS_MAX).toInt()
            if (PixelEscape.rand.nextBoolean()) {
                debug("Correcting Barricade leaving bottom gap @ x: " + b.xPos + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.xPos) + ") y: " + b.yPos)
                b.yPos = b.yPos + getAmountToCorrectBottom(world, b, checkRadius)
                debug("Corrected to y: " + b.yPos)
            } else {
                debug("Correcting Barricade leaving top gap @ x: " + b.xPos + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.xPos) + ") y: " + b.yPos)
                b.yPos = b.yPos + getAmountToCorrectTop(world, b, checkRadius)
                debug("Corrected to y: " + b.yPos)
            }
        }
    }

    private fun getAmountToCorrectBottom(world: World, b: EntityBarricade, checkRadius: Int): Float {
        val posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.xPos)
        var posY = b.yPos
        posY -= (b.sizeY / 2).toFloat()

        var correction = 0f
        for (i in -1 * checkRadius..checkRadius) {
            if (posXIndex + i < 0) {
                continue
            }
            val blockHeight = world.getBotBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH
            val neededCorrection = blockHeight + Reference.OBSTACLE_MIN_SPACE - posY
            if (neededCorrection > correction) {
                correction = neededCorrection
                debug("Correction for: x: " + b.xPos + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight)
            }
        }
        return correction
    }

    private fun getAmountToCorrectTop(world: World, b: EntityBarricade, checkRadius: Int): Float {
        val posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.xPos)
        var posY = b.yPos
        posY += (b.sizeY / 2).toFloat()

        var correction = 0f
        for (i in -1 * checkRadius..checkRadius) {
            if (posXIndex + i < 0) {
                continue
            }
            val blockHeight = world.worldHeight - world.getTopBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH
            val neededCorrection = blockHeight - Reference.OBSTACLE_MIN_SPACE - posY
            if (neededCorrection < correction) {
                correction = neededCorrection
                debug("Correction for: x: " + b.xPos + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight)
            }
        }
        return correction
    }

    override fun reset(world: World) {
        nextBarricadePosition = world.worldWidth + spawnOffset
    }

    companion object {

        private val spawnOffset = 40
    }
}
