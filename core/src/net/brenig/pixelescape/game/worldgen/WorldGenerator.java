package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.terrain.DiagonalCorridor;
import net.brenig.pixelescape.game.worldgen.terrain.FlatCorridor;
import net.brenig.pixelescape.game.worldgen.terrain.RandomTerrainGenerator;
import net.brenig.pixelescape.game.worldgen.terrain.TerrainClosing;
import net.brenig.pixelescape.game.worldgen.terrain.TerrainOpening;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Generates terrain and obstacles
 */
public class WorldGenerator {


    private TreeMap<Integer, ITerrainGenerator> terrainGenerators = new TreeMap<Integer, ITerrainGenerator>();
    private int totalWeight = 0;

    /**
     * registers a new TerrainGenerator
     *
     * @param generator The Generator to register
     */
    public void registerTerrainGenerator(ITerrainGenerator generator) {
        if (generator.getWeight() <= 0) return;
        totalWeight += generator.getWeight();
        terrainGenerators.put(totalWeight, generator);
    }

    /**
     * registers default worldgen
     */
    public void init() {
        registerTerrainGenerator(new RandomTerrainGenerator(9));
        registerTerrainGenerator(new FlatCorridor(3));
        registerTerrainGenerator(new TerrainOpening(1));
        registerTerrainGenerator(new TerrainClosing(4));
        registerTerrainGenerator(new DiagonalCorridor(7));
    }

    /**
     * generates the World
     *
     * @param world            the world to populate
     * @param blockToGenerate  amount of blocks that should get generated
     * @param generationPasses amount of tries to generate the world
     * @param random           world random-generator
     */
    public void generateWorld(World world, int blockToGenerate, int generationPasses, Random random) {
        //Init available terrain gen list
        TreeMap<Integer, ITerrainGenerator> gens = new TreeMap<Integer, ITerrainGenerator>();
        gens.putAll(terrainGenerators);
        int remaingWeight = this.totalWeight;

        while (generationPasses > 0 && blockToGenerate > 0) {
            //get last terrain
            TerrainPair old = world.terrain.getNewest();
            if (old == null) {
                old = new TerrainPair(Reference.STARTING_TERRAIN_HEIGHT, Reference.STARTING_TERRAIN_HEIGHT);
            }
            //remove invalid generators
            Iterator<Map.Entry<Integer, ITerrainGenerator>> iterator = gens.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ITerrainGenerator> entry = iterator.next();
                int genLength = entry.getValue().getMinGenerationLength(old);
                if (genLength <= 0 || genLength > blockToGenerate) {
                    remaingWeight -= entry.getValue().getWeight();
                    iterator.remove();
                }
            }
            if (gens.size() <= 0) {
                break;
            }
            int lastRequested = world.blocksRequested;
            ITerrainGenerator gen = ceilValue(random.nextInt(remaingWeight), gens);
            int generated = gen.generate(world, old, blockToGenerate, world.getBlocksGenerated(), random);
            blockToGenerate -= generated;
            //noinspection deprecation
            world.blocksGenerated += generated;
            generationPasses--;
            if (blockToGenerate < 0) {
                LogHelper.error("Invalid World Gen!! Generator ignoring MAX! Generator: " + gen);
            }
            if ((lastRequested + generated) != world.blocksRequested) {
                //noinspection deprecation
                LogHelper.error("Invalid World Gen!! Generator returnvalue invalid! Generator: " + gen + "; generated: " + generated + "; lastGen: " + lastRequested + "; currentGen: " + world.blocksRequested + "; blocksGenerated: " + world.blocksGenerated);
            }
        }
        generateObstacles(world, random);
    }

    /**
     * Helper-function to emulate {@link TreeMap#ceilingEntry(Object)}, which is not available on all platforms.
     */
    private ITerrainGenerator ceilValue(final int key, TreeMap<Integer, ITerrainGenerator> map) {
//			ITerrainGenerator gen = gens.ceilingEntry(random.nextInt(remaingWeight)).getValue();
        int lastKey = Integer.MAX_VALUE;
        for (int i : map.keySet()) {
            if (i >= key && i <= lastKey) {
                lastKey = i;
            }
        }
        return map.get(lastKey);
    }

    public void generateObstacles(World world, Random rand) {
        while (world.obstacles.getOldest() == null || world.obstacles.getOldest().posX < world.player.getXPos() - world.getWorldWidth() / 2) {
            Barricade last = world.obstacles.getNewest();
            int oldX = 0;
            if (last != null) {
                oldX = last.posX;
            }
            Barricade b = world.getCreateObstacleForGeneration();
            b.posX = (int) (oldX + world.getWorldWidth() * 0.7);
            b.posY = rand.nextInt(world.getWorldHeight() - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH;
        }
    }

    private float getAmountToCorrectBottom(World world, Barricade b, int checkRadius) {
        int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
//        float posY = world.convertMouseYToWorldCoordinate(b.posY);
        float posY = b.posY;
        posY -= Barricade.getSizeY() / 2;

        float correction = 0;
        for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
            if (posXIndex + i < 0) {
                continue;
            }
            int blockHeight = world.getBotBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
	        float neededCorrection = (blockHeight + Reference.OBSTACLE_MIN_SPACE) - posY;
            if (neededCorrection > correction) {
                correction = neededCorrection;
	            LogHelper.debug("Correction for: x: " + world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(posXIndex + i)) + "(" + (posXIndex + i) + "), y: " + blockHeight + ", amount: " + correction + ", oldY: " + posY);
            }
        }
        return correction;
    }

    private float getAmountToCorrectTop(World world, Barricade b, int checkRadius) {
        int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
        float posY = b.posY;
//        float posY = world.convertMouseYToWorldCoordinate(b.posY);
        posY += Barricade.getSizeY() / 2;

        float correction = 0;
        for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
            if (posXIndex + i < 0) {
                continue;
            }
            int blockHeight = world.getWorldHeight() - world.getTopBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
	        float neededCorrection = (blockHeight - Reference.OBSTACLE_MIN_SPACE) - posY;
            if (neededCorrection < correction) {
                correction = neededCorrection;
	            LogHelper.debug("Correction for: x: " + world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(posXIndex + i)) + "(" + (posXIndex + i) + "), y: " + blockHeight + ", amount: " + correction + ", oldY: " + posY);
            }
        }
        return correction;
    }

    public void updateBarricades(World world) {
        for (int i = 0; i < world.obstacles.size(); i++) {
            Barricade b =  world.obstacles.get(i);
            if (!b.moved && b.posX > world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH && b.posX < world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH * 2) {
	            b.moved = true;
                updateBarricade(world, b);
            }
        }
    }

    private void updateBarricade(World world, Barricade b) {
        int localIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
        //LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);
        if (!(localIndex < 0)) {
            //LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);

	        //Move barricade into level
            if (b.posY < world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
                b.posY += world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH - b.posY;
            } else if (b.posY > world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
                b.posY += (world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) - b.posY;
            }

	        //Leave gap for player
            int checkRadius = (int) ((world.player.getVelocity() / Reference.MAX_ENTITY_SPEED) * Reference.OBSTACLE_X_CHECK_RADIUS_MAX);
            if (PixelEscape.rand.nextBoolean()) {
                LogHelper.debug("Correcting Bottom Barricade @ x: " + b.posX + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.posX) + ") y: " + b.posY);
                b.posY += getAmountToCorrectBottom(world, b, checkRadius);
                LogHelper.debug("Corrected to y: " + b.posY);
            } else {
                LogHelper.debug("Correcting Top Barricade @ x: " + b.posX + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.posX) + ") y: " + b.posY);
                b.posY += getAmountToCorrectTop(world, b, checkRadius);
                LogHelper.debug("Corrected to y: " + b.posY);
            }
        }
    }
}
