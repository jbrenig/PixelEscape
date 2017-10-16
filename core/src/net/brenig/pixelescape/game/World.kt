package net.brenig.pixelescape.game

import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.game.entity.EntityPoolManager
import net.brenig.pixelescape.game.entity.impl.EntityHighscore
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.worldgen.TerrainPair
import net.brenig.pixelescape.game.worldgen.WorldGenerator
import net.brenig.pixelescape.lib.CycleArray
import net.brenig.pixelescape.lib.error
import net.brenig.pixelescape.lib.utils.MathUtils
import net.brenig.pixelescape.screen.GameScreen
import java.util.*

/**
 * Main World class, handling game logic
 */
class World constructor(val screen: GameScreen, worldWidth: Int = Reference.TARGET_RESOLUTION_X) {

    /**
     * The width of the world in pixels
     */
    var worldWidth = Reference.TARGET_RESOLUTION_X
        private set

    /**
     * The Main Player Entity
     */
    var player: EntityPlayer

    /**
     * array holding terrain information
     *
     *
     * this array must not contain null
     */
    val terrain: CycleArray<TerrainPair>


    val worldGenerator: WorldGenerator

    private val entityPoolManager: EntityPoolManager

    /**
     * Getter for Entity List<br></br>
     * currently only used for rendering<br></br>
     * note: use [World.spawnEntity] to spawn entities
     */
    val entityList: MutableList<Entity> = ArrayList()

    private val entitySpawnQueue: MutableSet<Entity> = HashSet()

    /**
     * tracks how many blocks got generated (--> world-index of the newest element in the terrain-buffer)
     */
    var terrainBufferWorldIndex: Int = 0

    val random = Random()

    val worldHeight: Int
        get() = Reference.GAME_RESOLUTION_Y

    /**
     * reuse an available [TerrainPair] if possible, creates and register a new one otherwise<br></br>
     * the returned [TerrainPair] will be added to end of the currently generated terrain and can be modified directly
     */
    val createTerrainPairForGeneration: TerrainPair
        get() {
            val pair = terrain.oldest
            terrainBufferWorldIndex++
            terrain.cycleForward()
            return pair
        }

    val blockBufferSize: Int
        get() = terrain.size()

    /**
     * @return global coordinate of the left screen edge
     */
    val currentScreenStart: Float
        get() = screen.worldRenderer.worldCameraXPos

    /**
     * global world coordinate of the right end of the screen
     */
    val currentScreenEnd: Float
        get() = screen.worldRenderer.worldCameraXPos + worldWidth

    val cameraLeftLocalIndex: Int
        get() = (screen.worldRenderer.worldCameraXPos / Reference.BLOCK_WIDTH).toInt() - terrainBufferWorldIndex

    val cameraRightLocalIndex: Int
        get() = ((screen.worldRenderer.worldCameraXPos + worldWidth) / Reference.BLOCK_WIDTH).toInt() - terrainBufferWorldIndex

    init {
        this.worldWidth = worldWidth

        player = EntityPlayer(this, screen.gameMode)
        terrain = CycleArray(calculateWorldBufferSize(worldWidth), {_ -> TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT) })

        player.xPosScreen = worldWidth / 4

        //load world generators
        //Create world gen and let the GameMode register its WorldFeatureGenerators
        worldGenerator = WorldGenerator(this, screen.gameMode)
        screen.gameMode.registerWorldGenerators(worldGenerator)


        //load entity pool manager
        entityPoolManager = EntityPoolManager(this)
        //		entityPoolManager.allocateObjects(EntityCrashParticle.class, 100);

        restart()
    }

    /**
     * updates the world
     *
     * @param deltaTick time passed between two ticks
     */
    fun update(deltaTick: Float) {
        generateWorld(false)
        //remove invalid entities
        val iterator = entityList.iterator()
        while (iterator.hasNext()) {
            val e = iterator.next()
            if (e.isDead) {
                e.removeEntityOnDeath()
                iterator.remove()
                entityPoolManager.free(e)
            } else {
                if (e.update(deltaTick, screen.input, screen.gameMode)) {
                    break
                }
            }
        }
        spawnEntities()
    }

    fun spawnEntities() {
        //Spawn Entities
        for (e in entitySpawnQueue) {
            spawnEntityDo(e)
        }
        entitySpawnQueue.clear()
    }

    fun spawnEntityDo(e: Entity) {
        entityList.add(e)
    }

    /**
     * spawns the given Entity
     */
    fun spawnEntity(e: Entity) {
        entitySpawnQueue.add(e)
    }

    fun <T : Entity> createEntity(entity: Class<T>): T {
        return entityPoolManager.obtain(entity)
    }

    /**
     * resizes the world
     *
     * @param newWidth the width in pixels
     */
    fun resize(newWidth: Int) {
        this.worldWidth = newWidth
        terrain.resize(calculateWorldBufferSize(newWidth))
        player.xPosScreen = worldWidth / 4
    }

    /**
     * returns the height (in blocks) of the bottom terrain at the given index (y0 = worldHeight) (--> y-Up)
     */
    fun getTopBlockHeight(index: Int): Int {
        if (terrain.size() < index) {
            error("PixelEscape | World", "Requested world Block index is out of Bounds! ($index)")
            return Reference.FALLBACK_TERRAIN_HEIGHT
        }
        val pair = getTerrainPairForIndex(index)
        return pair.top
    }

    /**
     * returns the height (in blocks) of the top terrain at the given index (y0 = 0) (--> y-Up)
     */
    fun getBotBlockHeight(index: Int): Int {
        if (terrain.size() < index) {
            error("PixelEscape | World", "Requested world Block index is out of Bounds! ($index)")
            return Reference.FALLBACK_TERRAIN_HEIGHT
        }
        val pair = getTerrainPairForIndex(index)
        return pair.bot
    }

    /**
     * generates the world (including special world gen)
     *
     * @param fillArray set this flag to true if the whole TerrainBuffer should be filled, used to generate Terrain on game start
     */
    fun generateWorld(fillArray: Boolean) {
        val blockToGenerate = if (fillArray) blockBufferSize else calculateBlocksToGenerate()
        val generationPasses = blockToGenerate + Reference.ADDITIONAL_GENERATION_PASSES
        worldGenerator.generateWorld(blockToGenerate, generationPasses, random)
    }

    /**
     * @return the Terrain at the specified position in the buffer (local block index)
     */
    fun getTerrainPairForIndex(i: Int): TerrainPair {
        if (i < 0) {
            error("World", "Invalid World index ( $i )! Must be greater than -1!")
            return BACKUP_TERRAIN_PAIR
        }
        if (i >= terrain.size()) {
            error("World", "Invalid World index ( " + i + " )! Out of Bounds! (array size: " + terrain.size() + ")")
            return BACKUP_TERRAIN_PAIR
        }
        return terrain[i]
    }

    /**
     * returns how many blocks need to be generated
     */
    private fun calculateBlocksToGenerate(): Int {
        val blocksLeftOfVision = cameraLeftLocalIndex - Reference.TERRAIN_BUFFER_LEFT - 1
        return if (blocksLeftOfVision > Reference.TERRAIN_GENERATION_THRESHOLD) {
            blocksLeftOfVision
        } else 0
    }

    /**
     * returns the TerrainPair at the given position
     */
    fun getBlockForWorldCoordinate(x: Float): TerrainPair {
        return getTerrainPairForIndex(convertWorldCoordinateToLocalBlockIndex(x))
    }

    /**
     * restart the world, player and all world gen get reset to start a new game
     */
    fun restart() {
        terrainBufferWorldIndex = -terrain.size()
        player.reset(screen.gameMode)
        for (e in entityList) {
            e.removeEntityOnDeath()
            entityPoolManager.free(e)
        }
        entityList.clear()

        //respawn player entity
        spawnEntityDo(player)
        if (screen.game.gameSettings.showHighscoreInWorld && screen.game.userData.getHighScore(screen.gameMode) > 0) {
            val entityHighscore = createEntity(EntityHighscore::class.java)
            spawnEntityDo(entityHighscore)
        }

        //Reset world gen
        worldGenerator.reset()
    }

    /**
     * checks collision with world<br></br>
     * parameters are world coordinates
     */
    fun doesAreaCollideWithWorld(x1: Float, y1: Float, x2: Float, y2: Float): CollisionType {
        val col = doesAreaCollideWithTerrain(x1, y1, x2, y2)
        return if (col !== CollisionType.NONE) col else doesAreaCollideWithEntities(x1, y1, x2, y2)
    }

    /**
     * checks collision with entities<br></br>
     * parameters are world coordinates
     */
    private fun doesAreaCollideWithEntities(x1: Float, y1: Float, x2: Float, y2: Float): CollisionType {
        @Suppress("LoopToCallChain")
        for (entity in entityList) {
            val col = entity.doesAreaCollideWithEntity(x1, y1, x2, y2)
            if (col !== CollisionType.NONE) {
                return col
            }
        }
        return CollisionType.NONE
    }

    /**
     * checks collision with terrain<br></br>
     * parameters are world coordinates
     * <br></br>
     * note: results are not perfect
     */
    fun doesAreaCollideWithTerrain(minX: Float, minY: Float, maxX: Float, maxY: Float): CollisionType {
        val back = this.getBlockForWorldCoordinate(MathUtils.floorF(minX))
        val front = this.getBlockForWorldCoordinate(MathUtils.floorF(maxX))

        val frontBot = front.bot * Reference.BLOCK_WIDTH
        val backBot = back.bot * Reference.BLOCK_WIDTH
        val frontTop = this.worldHeight - front.top * Reference.BLOCK_WIDTH
        val backTop = this.worldHeight - back.top * Reference.BLOCK_WIDTH

        //collide
        // collide bot
        if (minY < frontBot) {
            return when {
                minY < backBot ->
                    when {
                        maxY < frontBot -> CollisionType.TERRAIN_BOT_RIGHT // at least three corners
                        maxY < backBot -> CollisionType.TERRAIN_BOT_LEFT   // left three corners
                        else -> CollisionType.TERRAIN_BOTTOM               // bottom edge
                    }
                maxY < frontBot -> CollisionType.TERRAIN_RIGHT // complete right edge
                else -> CollisionType.TERRAIN_BOT_RIGHT        // only bottom right corner
            }
        }
        if (minY < backBot) {
            return when {
                maxY < backBot -> CollisionType.TERRAIN_LEFT // complete left edge
                else -> CollisionType.TERRAIN_BOT_LEFT       // only bottom left corner
            }
        }
        // collide top
        if (maxY > frontTop) {
            return when {
                maxY > backTop -> when {
                    minY > frontTop -> CollisionType.TERRAIN_TOP_RIGHT // at least three corners
                    minY > backTop -> CollisionType.TERRAIN_TOP_LEFT   // left three corners
                    else -> CollisionType.TERRAIN_TOP                  // top edge
                }
                minY > frontTop -> CollisionType.TERRAIN_RIGHT // complete right edge
                else -> CollisionType.TERRAIN_TOP_RIGHT        // only top right corner
            }
        }
        return if (maxY > backTop) {
            when {
                minY > backTop -> CollisionType.TERRAIN_LEFT // complete left edge
                else -> CollisionType.TERRAIN_TOP_LEFT       // only top left corner
            }
        } else CollisionType.NONE
    }


    ////////////////////////////////////////////////
    // Conversion of different coordinate systems //
    ////////////////////////////////////////////////

    fun convertScreenToWorldCoordinate(screenX: Float): Float {
        return screenX + player.progress
    }

    fun convertWorldCoordToScreenCoord(x: Float): Float {
        return x - player.progress
    }

    fun convertScreenCoordToWorldBlockIndex(screenX: Float): Int {
        return (convertScreenToWorldCoordinate(screenX) / Reference.BLOCK_WIDTH).toInt()
    }

    fun convertMouseYToScreenCoordinate(mouseY: Float): Float {
        return screen.game.gameSizeY - mouseY
    }

    fun convertMouseYToWorldCoordinate(screenY: Float): Float {
        return convertMouseYToScreenCoordinate(screenY) - screen.uiPos
    }

    fun convertWorldCoordinateToLocalBlockIndex(posX: Float): Int {
        return convertWorldBlockToLocalBlockIndex((posX / Reference.BLOCK_WIDTH).toInt())
    }

    /**
     * Converts a screen Coordinate to the block buffer index of the TerrainPair at the x Position
     */
    fun convertScreenCoordToLocalBlockIndex(screenX: Float): Int {
        return (cameraLeftLocalIndex + screenX / Reference.BLOCK_WIDTH).toInt()
    }

    /**
     * Converts the global block index to a screen coordinate
     */
    fun convertWorldIndexToScreenCoordinate(index: Int): Float {
        return convertWorldCoordToScreenCoord((index * Reference.BLOCK_WIDTH).toFloat())
    }


    /**
     * Converts a World index to a local (or generator) index
     * note: local BlockIndex starts with the newest block --> left to right
     */
    fun convertWorldBlockToLocalBlockIndex(i: Int): Int {
        return i - terrainBufferWorldIndex
    }

    /**
     * Converts a local (or generator) index to a World index
     * note: local BlockIndex starts with the newest block --> left to right
     */
    fun convertLocalBlockToWorldBlockIndex(i: Int): Int {
        return terrainBufferWorldIndex + i
    }

    fun getTerrainBotHeightRealForCoord(xPos: Int): Int {
        val index = convertWorldCoordinateToLocalBlockIndex(xPos.toFloat())
        return getTerrainBotHeightReal(index)
    }

    fun getTerrainTopHeightRealForCoord(xPos: Int): Int {
        val index = convertWorldCoordinateToLocalBlockIndex(xPos.toFloat())
        return getTerrainTopHeightReal(index)
    }

    fun getTerrainBotHeightReal(localIndex: Int): Int {
        return getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH
    }

    fun getTerrainTopHeightReal(localIndex: Int): Int {
        return worldHeight - getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH
    }

    companion object {

        private val BACKUP_TERRAIN_PAIR = TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT)

        /**
         * calculates the world buffer size
         *
         * @param newWidth new width of the world in pixels (scaled)
         * @return new world width (in pixels)
         */
        private fun calculateWorldBufferSize(newWidth: Int): Int {
            return newWidth / Reference.BLOCK_WIDTH + Reference.TERRAIN_BUFFER + Reference.TERRAIN_BUFFER_LEFT
        }
    }
}
