package net.brenig.pixelescape.game.worldgen;

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
 * Created by Jonas Brenig on 25.09.2015.
 */
public class WorldGenerator {


	private TreeMap<Integer, ITerrainGenerator> terrainGenerators = new TreeMap<Integer, ITerrainGenerator>();
	private int totalWeight = 0;

	/**
	 * registers a new TerrainGenerator
	 * @param generator The Generator to register
	 * @param weight weight
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

	public void generateWorld(World world, boolean fillArray, int blockToGenerate, int generationPasses, Random random) {
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
				if(genLength <= 0 || genLength > blockToGenerate) {
					remaingWeight -= entry.getValue().getWeight();
					iterator.remove();
				}
			}
			if(gens.size() <= 0) {
				break;
			}
			int lastRequested = world.blocksRequested;
			ITerrainGenerator gen = ceilValue(random.nextInt(remaingWeight), gens);
			int generated = gen.generate(world, old, blockToGenerate, world.getBlocksGenerated(), random);
			blockToGenerate -= generated;
			world.blocksGenerated += generated;
			generationPasses--;
			if(blockToGenerate < 0) {
				LogHelper.error("Invalid World Gen!! Generator ignoring MAX! Generator: " + gen);
			}
			if((lastRequested + generated) != world.blocksRequested) {
				LogHelper.error("Invalid World Gen!! Generator returnvalue invalid! Generator: " + gen + "; generated: " + generated + "; lastGen: " + lastRequested + "; currentGen: " + world.blocksRequested + "; blocksGenerated: " + world.blocksGenerated);
			}
		}
		world.generateObstacles();
	}

	private ITerrainGenerator ceilValue(final int key, TreeMap<Integer, ITerrainGenerator> map) {
//			ITerrainGenerator gen = gens.ceilingEntry(random.nextInt(remaingWeight)).getValue();
		int lastKey = Integer.MAX_VALUE;
		for(int i : map.keySet()) {
			if(i >= key && i <= lastKey) {
				lastKey = i;
			}
		}
		return map.get(lastKey);
	}
}
