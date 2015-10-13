package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;

import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.game.entity.particle.EntityCrashParticle;
import net.brenig.pixelescape.game.worldgen.Barricade;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.CycleArray;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Main World class, handling game logic
 * Created by Jonas Brenig on 02.08.2015.
 */
public class World {

	public static final net.brenig.pixelescape.game.worldgen.TerrainPair BACKUP_TERRAIN_PAIR = new net.brenig.pixelescape.game.worldgen.TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT);

	/**
	 * The width of the world in pixels
	 */
	private int worldWidth = Reference.TARGET_RESOLUTION_X;

	/**
	 * The height of the world in pixels
	 */
	private int worldHeight = Reference.GAME_RESOLUTION_Y;

	/**
	 * The Main Player Entity
	 */
	public EntityPlayer player;

	/**
	 * The terrain
	 */
	public final CycleArray<TerrainPair> terrain;

//	private final List<ITerrainGenerator> terrainGenerators = new ArrayList<ITerrainGenerator>();

	private final WorldGenerator worldGenerator;
	/**
	 * obstacles
	 */
	public final CycleArray<Barricade> obstacles;

	private final List<Entity> entityList;

	/**
	 * tracks how many block got generated
	 */
	public int blocksGenerated = 0;

	private Random rand = new Random();

	/**
	 * GameScreen instance
	 */
	private GameScreen screen;

	//DEUBG Code
	private int lastIndex = -1;
	private int lastTop = -1;
	private int lastBot = -1;

	public World(GameScreen screen) {
		this(screen, Reference.TARGET_RESOLUTION_X);
	}

	public World(GameScreen screen, int worldWidth) {
		this.screen = screen;
		player = new EntityPlayer(this);
		terrain = new CycleArray<TerrainPair>(calculateWorldBufferSize(worldWidth));
		obstacles = new CycleArray<Barricade>(3);
		this.worldWidth = worldWidth;
		entityList = new ArrayList<Entity>();
		//TODO proper support for player screen positioning
		player.setXPosScreen(worldWidth / 4);
		player.reset();

		//load world generators
		worldGenerator = new WorldGenerator();
		worldGenerator.init();
	}

	/**
	 * updates the world
	 * @param deltaTick time passed between two ticks
	 *                  TODO: cap delta time
	 */
	public void update(float deltaTick) {
		player.update(deltaTick, screen.getInput());
		generateWorld(false);
		player.collideWithWorld(this);
		for (int i = 0; i < obstacles.size(); i++) {
			player.collideWithObstacle(obstacles.get(i), this);
		}
		//remove invalid generators
		Iterator<Entity> iterator = entityList.iterator();
		while (iterator.hasNext()) {
			Entity e = iterator.next();
			if(e.isDead()) {
				e.removeEntityOnDeath();
				iterator.remove();
			} else {
				e.update(deltaTick);
			}
		}

		//DEBUG Code
		if(Reference.DEBUG_WORLD_GEN_VALIDATE) {
			int index = convertScreenCoordToWorldBlockIndex(player.getXPosScreen());
			if ((index - 5) <= lastIndex && index >= lastIndex) {
				int local = convertWorldBlockToLocalBlockIndex(lastIndex);
				int top = getTopBlockHeight(local);
				int bot = getBottomBlockHeight(local);
				if (top != lastTop || bot != lastBot) {
					LogHelper.error("Error in WorldGen!");
					LogHelper.error("TerrainBuffer:");
					LogHelper.error(terrain.toString());
				}
			} else {
				int local = convertWorldBlockToLocalBlockIndex(index);
				lastIndex = index;
				lastTop = getTopBlockHeight(local);
				lastBot = getBottomBlockHeight(local);
			}
		}
	}

	public void spawnEntity(Entity e) {
		entityList.add(e);
	}

	/**
	 * resizes the world
	 * @param newWidth the width in pixels
	 */
	public void resize(int newWidth) {
		this.worldWidth = newWidth;
		terrain.resize(calculateWorldBufferSize(newWidth));
		player.setXPosScreen(worldWidth / 4);
	}

	/**
	 * calculates the world buffer size
	 * @param newWidth new width of the world in pixels (scaled)
	 * @return new world buffer width
	 */
	private static int calculateWorldBufferSize(int newWidth) {
		return (newWidth / Reference.BLOCK_WIDTH) + Reference.TERRAIN_BUFFER + Reference.TERRAIN_MIN_BUFFER_LEFT + Reference.TERRAIN_MIN_BUFFER_RIGHT;
	}

	/**
	 * returns how many blocks are generated
	 */
	public int getBlocksGenerated() {
		return blocksGenerated;
	}

	/**
	 * returns the height of the top terrain at the given index
	 */
	public int getTopBlockHeight(int index) {
		if (terrain.size() < index) {
			Gdx.app.error("PixelEscape | World", "Requested world Block index is out of Bounds!");
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
	 * returns the height of the bottom terrain at the given index
	 */
	public int getBottomBlockHeight(int index) {
		if (terrain.size() < index) {
			Gdx.app.error("PixelEscape | World", "Requested world Block index is out of Bounds!");
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		}
		TerrainPair pair = getTerrainPairForIndex(index);
		if (pair == null) {
			return Reference.FALLBACK_TERRAIN_HEIGHT;
		} else {
			return pair.getBottom();
		}
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	/**
	 * generates the world (including obstacles)
	 * @param fillArray set this flag to true if the whole TerrainBuffer should be filled, used to generate Terrain on Gamestart
	 */
	public void generateWorld(boolean fillArray) {
		int blockToGenerate = fillArray ? getBlockBufferSize() : getBlocksToGenerate();
		int generationPasses = blockToGenerate + Reference.ADDITIONAL_GENERATION_PASSES;
		worldGenerator.generateWorld(this, fillArray, blockToGenerate, generationPasses, rand);
	}

	public void generateObstacles() {
		while (obstacles.getOldest() == null || obstacles.getOldest().posX < player.getXPos() - worldWidth / 2) {
			Barricade last = obstacles.getNewest();
			int oldX = 0;
			if (last != null) {
				oldX = last.posX;
			}
			Barricade b = getCreateObstacleForGeneration();
			b.posX = (int) (oldX + worldWidth * 0.7);
			b.posY = rand.nextInt(worldHeight - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH;
		}
	}

	public TerrainPair getCreateTerrainPairForGeneration() {
		TerrainPair pair = terrain.getOldest();
		if (pair == null) {
			pair = new TerrainPair(0, 0);
			terrain.add(pair);
			return pair;
		} else {
			terrain.cycleForward();
			return pair;
		}
	}

	private Barricade getCreateObstacleForGeneration() {
		Barricade b = obstacles.getOldest();
		if (b == null) {
			b = new Barricade(0, 0);
			obstacles.add(b);
			return b;
		} else {
			obstacles.cycleForward();
			return b;
		}
	}

	/**
	 * @return the terrain at the specified index/position in the world
	 */
	private TerrainPair getTerrainPairForBlockIndex(int i) {
		return getTerrainPairForIndex(convertWorldBlockToLocalBlockIndex(i));
	}

	/**
	 * @return the Terrain at the specified position in the buffer
	 */
	private TerrainPair getTerrainPairForIndex(int i) {
		if (i < 0) {
			Gdx.app.error("PixelEscape | World", "Invalid World index! Must be greater than 0!");
			return BACKUP_TERRAIN_PAIR;
		}
		if (i >= terrain.size()) {
			Gdx.app.error("PixelEscape | World", "Invalid World index! Out of Bounds! (index: " + i + "; array size: " + terrain.size() + ")");
			return BACKUP_TERRAIN_PAIR;
		}
		TerrainPair pair = terrain.get(terrain.size() - 1 - i);
		if (pair == null) {
			return BACKUP_TERRAIN_PAIR;
		} else {
			return pair;
		}
	}

	/**
	 * returns how many blocks need to be generated<br>
	 * returns a negative value if too many blocks already got generated
	 */
	public int getBlocksToGenerate() {
		//amount of blocks to the right edge of the screen
		final int lengthToTheRight = worldWidth - player.getXPosScreen();
		int neededBlocks = (int) ((player.getXPos() + lengthToTheRight) / Reference.BLOCK_WIDTH + Reference.TERRAIN_MIN_BUFFER_RIGHT);
		int missingBlocks =  neededBlocks - blocksGenerated;
		if(missingBlocks >= 0) {
			return missingBlocks + Reference.TERRAIN_BUFFER;
		}
		return missingBlocks;
	}

	public int getBlockBufferSize() {
		return terrain.size();
	}

	/**
	 * returns the TerrainPair at the given position
	 */
	public TerrainPair getBlockForScreenPosition(float x) {
//		int index = blocksGenerated - (player.getXPos() / Reference.BLOCK_WIDTH) - player.getXPosScreen();
//		return getTerrainPairForIndex(index + x);
		return getTerrainPairForBlockIndex(convertScreenCoordToWorldBlockIndex(x));
	}

	public void onPlayerCollide() {
		player.setIsDead(true);
		for (int i = 0; i < 60; i++) {
			float x = (float) Math.sin(i) + (rand.nextFloat() - 0.5F);
			float y = (float) Math.cos(i) + (rand.nextFloat() - 0.5F);
			EntityCrashParticle e = new EntityCrashParticle(this, (float) (player.getXPosScreen() - player.getVelocity() * Gdx.graphics.getDeltaTime() + x), player.getYPos() - player.getYVelocity() * Gdx.graphics.getDeltaTime() + y);
			float xVel = (float) (x * 2 + (rand.nextFloat() - 0.5F)) * 70;
			float yVel = (float) (y * 2 + (rand.nextFloat() - 0.5F)) * 70;
			e.setVelocity(xVel, yVel);
			this.spawnEntity(e);
		}
		screen.onGameOver();
	}

	public void restart() {
		blocksGenerated = 0;
		obstacles.clear();
		generateObstacles();
		player.reset();
		for(Entity e : entityList) {
			e.removeEntityOnDeath();
		}
		entityList.clear();
	}

	public List<Entity> getEntityList() {
		return entityList;
	}


	public CollisionType doesAreaCollideWithWorld(float x1, float y1, float x2, float y2) {
		CollisionType col = doesAreaCollideWithTerrain(x1, y1, x2, y2);
		return  col != CollisionType.NONE ? col : doesAreaCollideWithObstacles(x1, y1, x2, y2);
	}

	public CollisionType doesAreaCollideWithTerrain(float x1, float y1, float x2, float y2) {
		TerrainPair back = this.getBlockForScreenPosition((int) x1);
		TerrainPair front = this.getBlockForScreenPosition((int) x2);
		//collide
		if(y1 < front.top * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_BOT_RIGHT;
		}
		if(y1 < back.top * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_BOT_LEFT;
		}
		if(y2 > this.getWorldHeight() - front.bottom * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_TOP_RIGHT;
		}
		if(y2 > this.getWorldHeight() - back.bottom * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_TOP_LEFT;
		}
		return CollisionType.NONE;
	}

	public CollisionType doesAreaCollideWithObstacles(float x1, float y1, float x2, float y2) {
		float dif = (float) (player.getXPos() - player.getXPosScreen());
		x1 += dif;
		x2 += dif;
		for (int i = 0; i < obstacles.size(); i++) {
			if(doesAreaCollideWithObstacle(obstacles.get(i), x1, y1, x2, y2)) {
				return CollisionType.OBSTACLE;
			}
		}
		return CollisionType.NONE;
	}

	public boolean doesAreaCollideWithObstacle(Barricade ob, float x1, float y1, float x2, float y2) {
		if(ob.posX - Barricade.sizeX / 2 < x2 && ob.posX + Barricade.sizeX / 2 > x1) {
			if(ob.posY - Barricade.sizeY / 2 < y2 && ob.posY + Barricade.sizeY / 2 > y1) {
				return true;
			}
		}
		return false;
	}

	////////////////////////////////////////////////
	// Conversion of different coordinate systems //
	////////////////////////////////////////////////

	public float convertScreenToWorldCoordinate(float screenX) {
		return player.getXPos() + screenX - player.getXPosScreen();
	}

	public float convertWorldCoordToScreenCoord(float x) {
		return x - player.getXPos() + player.getXPosScreen();
	}

	public int convertScreenCoordToWorldBlockIndex(float screenX) {
		return convertToWorldBlockIndex(convertScreenToWorldCoordinate(screenX));
	}

	public int convertToWorldBlockIndex(float posX) {
		return (int) (posX / Reference.BLOCK_WIDTH);
	}

	public float convertScreenYToWorldCoordinate(float screenY) {
		return screenY - Reference.GAME_UI_Y_SIZE - screen.uiPos;
	}

	/**
	 * Converts the global block index to a screen coordinate
	 */
	public float convertWorldIndexToScreenCoordinate(int index) {
		return convertWorldCoordToScreenCoord(index * Reference.BLOCK_WIDTH);
	}


	/**
	 * note: local BlockIndex starts with the newest block --> right to left
	 */
	public int convertWorldBlockToLocalBlockIndex(int i) {
		return getBlocksGenerated() - i - 1;
	}
	/**
	 * note: local BlockIndex starts with the newest block --> right to left
	 */
	public int convertLocalBlockToWorldBlockIndex(int i) {
		return getBlocksGenerated() - i - 1;
	}

	public float getXWorldPosition() {
		return player.getXPos() - player.getXPosScreen();
	}


}
