package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.worldgen.predefined.IScoreWorldFeature;
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator;
import net.brenig.pixelescape.game.worldgen.special.ISpecialWorldGenerator;
import net.brenig.pixelescape.game.worldgen.terrain.DiagonalCorridor;
import net.brenig.pixelescape.game.worldgen.terrain.FlatCorridor;
import net.brenig.pixelescape.game.worldgen.terrain.RandomTerrainGenerator;
import net.brenig.pixelescape.game.worldgen.terrain.TerrainClosing;
import net.brenig.pixelescape.game.worldgen.terrain.TerrainOpening;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates terrain and obstacles
 */
public class WorldGenerator {

	private final World world;
	private final GameMode gameMode;

    private final WeightedList<ITerrainGenerator> terrainGenerators = new WeightedList<ITerrainGenerator>();

    private List<ISpecialWorldGenerator> specialGenerators = new ArrayList<ISpecialWorldGenerator>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<IScoreWorldFeature> specialPredefinedGenerators = new ArrayList<IScoreWorldFeature>();

	////////////////////////////////////////////////
	// Worldgen effects (--> eg. items/Gamemodes) //
	////////////////////////////////////////////////
	public float obstacleSizeModifier = 1F;


    public WorldGenerator(World world, GameMode gameMode) {
	    this.world = world;
	    this.gameMode = gameMode;
	}

    /**
     * registers a new TerrainGenerator
     *
     * @param generator The Generator to register
     */
    public void registerTerrainGenerator(ITerrainGenerator generator) {
        if (generator.getWeight() <= 0) return;
        terrainGenerators.add(generator.getWeight(), generator);
    }

    /**
     * generates the World
     *
     * @param blockToGenerate  amount of blocks that should get generated
     * @param generationPasses amount of tries to generate the world
     * @param random           world random-generator
     */
    public void generateWorld(int blockToGenerate, int generationPasses, Random random) {
        //Init available terrain gen list
        WeightedList<ITerrainGenerator> gens = terrainGenerators.createCopy();

        while (generationPasses > 0 && blockToGenerate > 0) {
            //get last terrain
            TerrainPair lastGeneratedTerrainPair = world.getTerrain().getNewest();
            if (lastGeneratedTerrainPair == null) {
                lastGeneratedTerrainPair = new TerrainPair(Reference.STARTING_TERRAIN_HEIGHT, Reference.STARTING_TERRAIN_HEIGHT);
            }

            int oldTerrainBufferIndexStart = world.terrainBufferWorldIndex;
	        ITerrainGenerator gen = gens.getRandomValueWithFilter(random, new WorldGenFilter(lastGeneratedTerrainPair, blockToGenerate));
	        if(gen == null) {
		        //no applicable generators left
		        break;
	        }
            int generated = gen.generate(world, lastGeneratedTerrainPair, blockToGenerate, world.getTerrainBufferWorldIndex(), random);
            blockToGenerate -= generated;
            generationPasses--;
            if (blockToGenerate < 0) {
                LogHelper.error("Invalid World Gen!! Generator ignoring MAX! Generator: " + gen);
            }
            if ((oldTerrainBufferIndexStart + generated) != world.terrainBufferWorldIndex) {
                LogHelper.error("Invalid World Gen!! Generator returnvalue invalid! Generator: " + gen + "; generated: " + generated + "; lastGen: " + oldTerrainBufferIndexStart + "; currentGen: " + world.terrainBufferWorldIndex);
            }
        }
	    for(ISpecialWorldGenerator gen : specialGenerators) {
		    gen.generate(this, world, random, gameMode);
	    }
    }

    public void addSpecialGenerator(ISpecialWorldGenerator gen) {
        specialGenerators.add(gen);
    }

	public void reset() {
		for(ISpecialWorldGenerator generator : specialGenerators) {
			generator.reset(world);
		}
		for(IScoreWorldFeature gen : specialPredefinedGenerators) {
			gen.reset(world);
		}
	}


	/**
	 * registers the default World-generators
	 */
	public void registerDefaultWorldGenerators() {
		registerDefaultTerrainGenerators();
		registerDefaultBarricadeGenerator();
	}

	/**
	 * registers the default terrain generators with default weights
	 */
	public void registerDefaultTerrainGenerators() {
		registerTerrainGenerator(new RandomTerrainGenerator(9));
		registerTerrainGenerator(new FlatCorridor(3));
		registerTerrainGenerator(new TerrainOpening(1));
		registerTerrainGenerator(new TerrainClosing(4));
		registerTerrainGenerator(new DiagonalCorridor(7));
	}


	/**
	 * registers the default Barricade generator
	 */
	public void registerDefaultBarricadeGenerator() {
		addSpecialGenerator(new BarricadeGenerator((int) (Reference.TARGET_RESOLUTION_X * 0.85F)));
	}

	private class WorldGenFilter implements WeightedList.Filter<ITerrainGenerator> {

		private final TerrainPair old;
		private final int blockToGenerate;

		private WorldGenFilter(TerrainPair old, int blockToGenerate) {
			this.old = old;
			this.blockToGenerate = blockToGenerate;
		}

		@Override
		public boolean isValid(ITerrainGenerator value) {
			int genLength = value.getMinGenerationLength(old);
			return !(genLength <= 0 || genLength > blockToGenerate);
		}
	}
}
