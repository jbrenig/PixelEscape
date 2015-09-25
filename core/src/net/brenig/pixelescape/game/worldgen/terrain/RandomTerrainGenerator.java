package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;
import net.brenig.pixelescape.game.worldgen.TerrainPair;

import java.util.Random;

/**
 * Created by Jonas Brenig on 15.08.2015.
 */
public class RandomTerrainGenerator implements ITerrainGenerator {

	private int weight;

	public RandomTerrainGenerator(int i) {
		this.weight = i;
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		TerrainPair pair = world.getCreateTerrainPairForGeneration();
		pair.top = Math.max(1, Math.min(6, lastPair.getTop() + random.nextInt(3) - 1));
		pair.bottom = Math.max(1, Math.min(6, lastPair.getBottom() + random.nextInt(3) - 1));
		return 1;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		return 1;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
