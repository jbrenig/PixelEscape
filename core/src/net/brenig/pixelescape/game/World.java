package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;

import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.PlayerEntity;
import net.brenig.pixelescape.game.entity.particle.EntityCrashParticle;
import net.brenig.pixelescape.game.worldgen.Barricade;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.CycleArray;
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
	public PlayerEntity player;

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

	public World(GameScreen screen) {
		this(screen, Reference.TARGET_RESOLUTION_X);
	}

	public World(GameScreen screen, int worldWidth) {
		this.screen = screen;
		player = new PlayerEntity();
		terrain = new CycleArray<TerrainPair>(calculateGenerationLength(worldWidth));
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
		terrain.resize(calculateGenerationLength(newWidth));
		player.setXPosScreen(worldWidth / 4);
	}

	/**
	 * calculates the world buffer size
	 */
	private static int calculateGenerationLength(int newWidth) {
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
		int blockToGenerate = fillArray ? terrain.size() : getBlocksToGenerate();
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
			b.posY = rand.nextInt(worldHeight - Reference.FALLBACK_TERRAIN_HEIGHT * 2) + Reference.FALLBACK_TERRAIN_HEIGHT;
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
		final int blocksRightOfPlayer = worldWidth - player.getXPosScreen();
		int seenBlocks = (player.getXPos() + blocksRightOfPlayer) / Reference.BLOCK_WIDTH + Reference.TERRAIN_MIN_BUFFER_RIGHT;
		int neededBlocks =  seenBlocks - blocksGenerated;
		if(neededBlocks >= 0) {
			return neededBlocks + Reference.TERRAIN_BUFFER;
		}
		return neededBlocks;
	}

	public int getBlockBufferSize() {
		return terrain.size();
	}

	/**
	 * returns the TerrainPair at the given position
	 */
	public TerrainPair getBlockForPosition(int x) {
		int index = blocksGenerated - (player.getXPos() / Reference.BLOCK_WIDTH) - player.getXPosScreen();
		return getTerrainPairForIndex(index + x);
	}

	public void onPlayerCollide() {
		for (int i = 0; i < 30; i++) {
			float x = (float) (Math.sin(i));
			float y = (float) Math.cos(i);
			EntityCrashParticle e = new EntityCrashParticle(this, (float) (player.getXPosScreen() - player.getVelocity() * Gdx.graphics.getDeltaTime() + x), player.getYPos() - player.getYVelocity() * Gdx.graphics.getDeltaTime() + y);
			float xVel = (float) (x * 2 + (rand.nextFloat() - 0.5)) * 10;
			float yVel = (float) (y * 2 + (rand.nextFloat() - 0.5)) * 10;
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
		TerrainPair back = this.getBlockForPosition((int) x1);
		TerrainPair front = this.getBlockForPosition((int) x2);
		//collide
		if(y1 < back.top * Reference.BLOCK_WIDTH || y1 < front.top * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_BOT;
		}
		if(y2 > this.getWorldHeight() - back.bottom * Reference.BLOCK_WIDTH || y2 > this.getWorldHeight() - front.bottom * Reference.BLOCK_WIDTH) {
			return CollisionType.TERRAIN_TOP;
		}
		return CollisionType.NONE;
	}

	public CollisionType doesAreaCollideWithObstacles(float x1, float y1, float x2, float y2) {
		float dif = (float) (player.getProgress() - player.getXPosScreen());
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
}
