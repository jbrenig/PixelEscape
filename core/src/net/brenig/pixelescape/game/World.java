package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;

import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.EntityPoolManager;
import net.brenig.pixelescape.game.entity.particle.EntityCrashParticle;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.CycleArray;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Main World class, handling game logic
 */
public class World {

	private static final TerrainPair BACKUP_TERRAIN_PAIR = new TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT);

	/**
	 * The width of the world in pixels
	 */
	private int worldWidth = Reference.TARGET_RESOLUTION_X;

	public EntityPlayer player;

	/**
	 * array holding terrain information
	 * <p>
	 * this array must not contain null
	 * </p>
	 */
	private final CycleArray<TerrainPair> terrain;


	private final WorldGenerator worldGenerator;

	private final EntityPoolManager entityPoolManager;

	private final List<Entity> entityList;
	private final Set<Entity> entitySpawnQueue;

	/**
	 * tracks how many block got generated
	 */
	@Deprecated
	public int blocksGeneratedOLD = 0;

	/**
	 * tracks how many blocks got generated (--> world-index of the newest element in the terrain-buffer)
	 */
	public int terrainBufferWorldIndex;

	private final Random random = new Random();

	/**
	 * GameScreen instance
	 */
	private final GameScreen screen;

	//DEUBG Code
	//TODO remove
	private int lastIndex = -1;
	private int lastTop = -1;
	private int lastBot = -1;

	public World(GameScreen screen) {
		this(screen, Reference.TARGET_RESOLUTION_X);
	}

	public World(GameScreen screen, int worldWidth) {
		this.screen = screen;
		this.worldWidth = worldWidth;

		player = new EntityPlayer(this, screen.getGameMode());
		terrain = new CycleArray<TerrainPair>(calculateWorldBufferSize(worldWidth));
		//fill terrain buffer
		for(int i = 0; i < terrain.size(); i++) {
			terrain.add(new TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT));
		}

		entityList = new ArrayList<Entity>();
		entitySpawnQueue = new HashSet<Entity>();
		player.setXPosScreen(worldWidth / 4);

		//load world generators
		//Create world gen and let the GameMode register its WorldFeatureGenerators
		worldGenerator = new WorldGenerator(this, screen.getGameMode());
		screen.getGameMode().registerWorldGenerators(worldGenerator);


		//load entity pool manager
		entityPoolManager = new EntityPoolManager(this);
		entityPoolManager.allocateObjects(EntityCrashParticle.class, 100);

		restart();
	}

	/**
	 * updates the world
	 *
	 * @param deltaTick time passed between two ticks
	 */
	public void update(float deltaTick) {
		generateWorld(false);
		//remove invalid entities
		Iterator<Entity> iterator = entityList.iterator();
		while (iterator.hasNext()) {
			Entity e = iterator.next();
			if (e.isDead()) {
				e.removeEntityOnDeath();
				iterator.remove();
				entityPoolManager.free(e);
			} else {
				if(e.update(deltaTick, screen.getInput())) {
					break;
				}
			}
		}
		spawnEntities();
	}

	public void spawnEntities() {
		//Spawn Entities
		for(Entity e : entitySpawnQueue) {
			spawnEntityDo(e);
		}
		entitySpawnQueue.clear();
	}

	public void spawnEntityDo(Entity e) {
		entityList.add(e);
	}

	@Deprecated
	private void debugValidateWorldGen() {
		//TODO: remove legacy debug options
		//DEBUG Code
		if (GameDebugSettings.get("DEBUG_WORLD_GEN_VALIDATE")) {
			int index = convertScreenCoordToWorldBlockIndex(player.getXPosScreen());
			if ((index - 5) <= lastIndex && index >= lastIndex) {
				int local = convertWorldBlockToLocalBlockIndex(lastIndex);
				int top = getTopBlockHeight(local);
				int bot = getBotBlockHeight(local);
				if (top != lastTop || bot != lastBot) {
					LogHelper.debug("WorldGen", "BlocksGenerated: " + getTerrainBufferWorldIndex() + "; BlocksRequested: " + terrainBufferWorldIndex, null);
					LogHelper.error("Error in WorldGen!");
					LogHelper.error("TerrainBuffer:");
					LogHelper.error(terrain.toString());
				}
			} else {
				int local = convertWorldBlockToLocalBlockIndex(index);
				lastIndex = index;
				lastTop = getTopBlockHeight(local);
				lastBot = getBotBlockHeight(local);
			}
		}
	}

	/**
	 * spawns the given Entity
	 */
	public void spawnEntity(Entity e) {
		entitySpawnQueue.add(e);
	}

	public <T extends Entity> T createEntity(Class<T> entity) {
		return entityPoolManager.obtain(entity);
	}

	/**
	 * resizes the world
	 *
	 * @param newWidth the width in pixels
	 */
	public void resize(int newWidth) {
		this.worldWidth = newWidth;
		final int oldSize = terrain.size();
		terrain.resize(calculateWorldBufferSize(newWidth));
		if(oldSize < terrain.size()) {
			//fill new entries
			for(int i = oldSize; i < terrain.size(); i++) {
				terrain.set(i, new TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT));
			}
		}
		player.setXPosScreen(worldWidth / 4);
	}

	/**
	 * calculates the world buffer size
	 *
	 * @param newWidth new width of the world in pixels (scaled)
	 * @return new world width (in pixels)
	 */
	private static int calculateWorldBufferSize(int newWidth) {
		return (newWidth / Reference.BLOCK_WIDTH) + Reference.TERRAIN_BUFFER + Reference.TERRAIN_BUFFER_LEFT;
	}

	/**
	 * returns how many blocks are generated
	 */
	public int getTerrainBufferWorldIndex() {
		return terrainBufferWorldIndex;
	}

	/**
	 * returns the height (in blocks) of the bottom terrain at the given index (y0 = worldHeight) (--> y-Up)
	 */
	public int getTopBlockHeight(int index) {
		if (terrain.size() < index) {
			Gdx.app.error("PixelEscape | World", "Requested world Block index is out of Bounds! (" + index + ")");
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		}
		TerrainPair pair = getTerrainPairForIndex(index);
		if (pair == null) {
			Gdx.app.error("PixelEscape | World", "Requested Terrain is not available! Falling back to Default!");
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		} else {
			return pair.getTop();
		}
	}

	/**
	 * returns the height (in blocks) of the top terrain at the given index (y0 = 0) (--> y-Up)
	 */
	public int getBotBlockHeight(int index) {
		if (terrain.size() < index) {
			Gdx.app.error("PixelEscape | World", "Requested world Block index is out of Bounds! (" + index + ")");
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		}
		TerrainPair pair = getTerrainPairForIndex(index);
		if (pair == null) {
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		} else {
			return pair.getBot();
		}
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public int getWorldHeight() {
		/*
	  The height of the world in pixels
	 */
		return Reference.GAME_RESOLUTION_Y;
	}

	/**
	 * generates the world (including special world gen)
	 *
	 * @param fillArray set this flag to true if the whole TerrainBuffer should be filled, used to generate Terrain on Gamestart
	 */
	public void generateWorld(boolean fillArray) {
		int blockToGenerate = fillArray ? getBlockBufferSize() : calculateBlocksToGenerate();
		int generationPasses = blockToGenerate + Reference.ADDITIONAL_GENERATION_PASSES;
		worldGenerator.generateWorld(blockToGenerate, generationPasses, random);
	}

	/**
	 * reuse an available {@link TerrainPair} if possible, creates and register a new one otherwise<br></br>
	 * the returned {@link TerrainPair} will be added to end of the currently generated terrain and can be modified direcly
	 */
	public TerrainPair getCreateTerrainPairForGeneration() {
		TerrainPair pair = terrain.getOldest();
		if(pair == null) {
			LogHelper.warn("Invalid TerrainPair! (null)");
			pair = new TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT);
			terrain.set(0, pair);
		}
		terrainBufferWorldIndex++;
		terrain.cycleForward();
		return pair;
	}

	/**
	 * @return the Terrain at the specified position in the buffer (local block index)
	 */
	public TerrainPair getTerrainPairForIndex(int i) {
		if (i < 0) {
			LogHelper.error("World", "Invalid World index ( " + i + " )! Must be greater than -1!");
			return BACKUP_TERRAIN_PAIR;
		}
		if (i >= terrain.size()) {
			LogHelper.error("World", "Invalid World index ( " + i + " )! Out of Bounds! (array size: " + terrain.size() + ")");
			return BACKUP_TERRAIN_PAIR;
		}
		TerrainPair pair = terrain.get(i);
		if (pair == null) {
			return BACKUP_TERRAIN_PAIR;
		} else {
			return pair;
		}
	}

	/**
	 * returns how many blocks need to be generated
	 */
	private int calculateBlocksToGenerate() {
		int blocksLeftOfVision = getCameraLeftLocalIndex() - Reference.TERRAIN_BUFFER_LEFT - 1;
		if(blocksLeftOfVision > Reference.TERRAIN_GENERATION_THRESHOLD) {
			return blocksLeftOfVision;
		}
		return 0;
	}

	public int getBlockBufferSize() {
		return terrain.size();
	}

	/**
	 * returns the TerrainPair at the given position
	 */
	public TerrainPair getBlockForWorldCoordinate(float x) {
		return getTerrainPairForIndex(convertWorldCoordinateToLocalBlockIndex(x));
	}

	public GameScreen getScreen() {
		return screen;
	}

	/**
	 * restart the world, player and all world gen get reset to start a new game
	 */
	public void restart() {
		//noinspection deprecation
		blocksGeneratedOLD = 0;
		terrainBufferWorldIndex = -terrain.size();
		player.reset(screen.getGameMode());
		for (Entity e : entityList) {
			e.removeEntityOnDeath();
			entityPoolManager.free(e);
		}
		entityList.clear();

		//respawn player entity
		spawnEntityDo(player);

		//Reset world gen
		worldGenerator.reset();
	}

	/**
	 * Getter for Entity List<br></br>
	 * currently only used for rendering<br></br>
	 * note: use {@link World#spawnEntity(Entity)} to spawn entities
	 */
	public List<Entity> getEntityList() {
		return entityList;
	}


	/**
	 * checks collision with world<br></br>
	 * parameters are world coordinates
	 */
	public CollisionType doesAreaCollideWithWorld(float x1, float y1, float x2, float y2) {
		CollisionType col = doesAreaCollideWithTerrain(x1, y1, x2, y2);
		return col != CollisionType.NONE ? col : doesAreaCollideWithEntities(x1, y1, x2, y2);
	}

	/**
	 * checks collision with entities<br></br>
	 * parameters are world coordinates
	 */
	private CollisionType doesAreaCollideWithEntities(float x1, float y1, float x2, float y2) {
		for(Entity entity : entityList) {
			CollisionType col = entity.doesAreaCollideWithEntity(x1, y1, x2, y2);
			if(col != CollisionType.NONE) {
				return col;
			}
		}
		return CollisionType.NONE;
	}

	/**
	 * checks collision with terrain<br></br>
	 * parmetters are world coordinates
	 */
	public CollisionType doesAreaCollideWithTerrain(float x1, float y1, float x2, float y2) {
		TerrainPair back = this.getBlockForWorldCoordinate((int) x1);
		TerrainPair front = this.getBlockForWorldCoordinate((int) x2);
		//collide
		if (y1 < front.getBot() * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_BOT_RIGHT;
		}
		if (y1 < back.getBot() * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_BOT_LEFT;
		}
		if (y2 > this.getWorldHeight() - front.getTop() * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_TOP_RIGHT;
		}
		if (y2 > this.getWorldHeight() - back.getTop() * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_TOP_LEFT;
		}
		return CollisionType.NONE;
	}


	////////////////////////////////////////////////
	// Conversion of different coordinate systems //
	////////////////////////////////////////////////

	public float convertScreenToWorldCoordinate(float screenX) {
		return screenX + player.getProgress();
	}

	public float convertWorldCoordToScreenCoord(float x) {
		return x - player.getProgress();
	}

	public int convertScreenCoordToWorldBlockIndex(float screenX) {
		return (int) (convertScreenToWorldCoordinate(screenX) / Reference.BLOCK_WIDTH);
	}

	public float convertMouseYToScreenCoordinate(float mouseY) {
		return Gdx.graphics.getHeight() - mouseY;
	}

	public float convertMouseYToWorldCoordinate(float screenY) {
		return convertMouseYToScreenCoordinate(screenY) - screen.getUiPos();
	}

	public int convertWorldCoordinateToLocalBlockIndex(float posX) {
		return convertWorldBlockToLocalBlockIndex((int) (posX / Reference.BLOCK_WIDTH));
	}

	public float getCurrentScreenStart() {
		return screen.worldRenderer.getWorldCameraXPos();
	}

	public float getCurrentScreenEnd() {
		return screen.worldRenderer.getWorldCameraXPos() + getWorldWidth();
	}

	/**
	 * Converts a screen Coordinate to the block buffer index of the TerrainPair at the x Position
	 */
	public int convertScreenCoordToLocalBlockIndex(float screenX) {
		return (int) (getCameraLeftLocalIndex() + (screenX / Reference.BLOCK_WIDTH));
	}

	/**
	 * Converts the global block index to a screen coordinate
	 */
	public float convertWorldIndexToScreenCoordinate(int index) {
		return convertWorldCoordToScreenCoord(index * Reference.BLOCK_WIDTH);
	}

	public int getCameraLeftLocalIndex() {
		return ((int) (screen.worldRenderer.getWorldCameraXPos() / Reference.BLOCK_WIDTH)) - terrainBufferWorldIndex;
	}

	public int getCameraRightLocalIndex() {
		return ((int) ((screen.worldRenderer.getWorldCameraXPos() + getWorldWidth()) / Reference.BLOCK_WIDTH)) - terrainBufferWorldIndex;
	}


	/**
	 * Converts a World index to a local (or generator) index
	 * note: local BlockIndex starts with the newest block --> right to left
	 */
	public int convertWorldBlockToLocalBlockIndex(int i) {
		return i - terrainBufferWorldIndex;
	}

	/**
	 * Converts a local (or generator) index to a World index
	 * note: local BlockIndex starts with the newest block --> right to left
	 */
	public int convertLocalBlockToWorldBlockIndex(int i) {
		return terrainBufferWorldIndex + i;
	}

	/**
	 * The Main Player Entity
	 */
	public EntityPlayer getPlayer() {
		return player;
	}



	/**
	 * The terrain
	 */
	public CycleArray<TerrainPair> getTerrain() {
		return terrain;
	}

	public int getTerrainBotHeightRealForCoord(int xPos) {
		final int index = convertWorldCoordinateToLocalBlockIndex(xPos);
		return getTerrainBotHeightReal(index);
	}

	public int getTerrainTopHeightRealForCoord(int xPos) {
		final int index = convertWorldCoordinateToLocalBlockIndex(xPos);
		return getTerrainTopHeightReal(index);
	}

	public int getTerrainBotHeightReal(int localIndex) {
		return getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH;
	}

	public int getTerrainTopHeightReal(int localIndex) {
		return getWorldHeight() - (getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH);
	}

	public Random getRandom() {
		return random;
	}
}
