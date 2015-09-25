package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;
import net.brenig.pixelescape.game.worldgen.TerrainPair;

import java.util.Random;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public class TerrainClosing implements ITerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 2;
	private static final int MAX_GENERATION_LENGTH = 6;

	/**
	 * maximal Terrain height
	 */
	private static final int MAX_HEIGHT = 6;

	/**
	 * This value describes the maximum value of the sum of both top and bottom terrain<br>
	 * This Cannot be more than two times MAX_HEIGHT
	 */
	private static final int MIN_CORRIDOR_HEIGHT = 12;

	private int weight;

	public TerrainClosing(int i) {
		this.weight = i;
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		int max = Math.min(blocksToGenerate,  MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1));
		int generated = 0;
		int bot = lastPair.bottom + 1;
		int top = lastPair.top + 1;
		while(bot <= MAX_HEIGHT && top <= MAX_HEIGHT && bot + top <= MIN_CORRIDOR_HEIGHT && generated < max) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			pair.top = top;
			pair.bottom = bot;
			top++;
			bot++;
			generated++;
		}
		return generated;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		int bot = last.bottom + MIN_GENERATION_LENGTH;
		int top = last.top +  MIN_GENERATION_LENGTH;
		if(bot > MAX_HEIGHT || top > MAX_HEIGHT || bot + top > MIN_CORRIDOR_HEIGHT) {
			return 0;
		}
		return MIN_GENERATION_LENGTH;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
