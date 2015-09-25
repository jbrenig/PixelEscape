package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.terrain.FlatCorridor;
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
	public void registerTerrainGenerator(ITerrainGenerator generator, int weight) {
		if (weight <= 0) return;
		terrainGenerators.put(weight, generator);
		totalWeight += weight;
	}

	/**
	 * registers default worldgen
	 */
	public void init() {
		registerTerrainGenerator(new net.brenig.pixelescape.game.worldgen.terrain.RandomTerrainGenerator(), 10);
		registerTerrainGenerator(new FlatCorridor(), 1);
	}

	public void generateWorld(World world, boolean fillArray, int blockToGenerate, int generationPasses, Random random) {
		//Init available terrain gen list
		TreeMap<Integer, ITerrainGenerator> gens = new TreeMap<Integer, ITerrainGenerator>();
		gens.putAll(terrainGenerators);
		int totalWeight = this.totalWeight;

		while (generationPasses > 0 && blockToGenerate > 0) {
			//get last terrain
			TerrainPair old = world.terrain.getNewest();
			if (old == null) {
				old = new TerrainPair(Reference.FALLBACK_TERRAIN_HEIGHT, Reference.FALLBACK_TERRAIN_HEIGHT);
			}
			//remove invalid generators
			Iterator<Map.Entry<Integer, ITerrainGenerator>> iterator = gens.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, ITerrainGenerator> entry = iterator.next();
				if(entry.getValue().getMinGenerationLength(old) > blockToGenerate) {
					totalWeight -= entry.getKey();
					iterator.remove();
				}
			}
			if(gens.size() <= 0) {
				break;
			}
			ITerrainGenerator gen = gens.ceilingEntry(random.nextInt(totalWeight)).getValue();
			int generated = gen.generate(world, old, blockToGenerate, world.blocksGenerated, random);
			blockToGenerate -= generated;
			world.blocksGenerated += generated;
			generationPasses++;
		}
		if (fillArray && world.getBlocksToGenerate() < 0) {
			world.blocksGenerated += world.getBlocksToGenerate();
		}
		world.generateObstacles();
	}
}
