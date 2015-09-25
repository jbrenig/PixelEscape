package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;
import net.brenig.pixelescape.game.worldgen.TerrainPair;

import java.util.Random;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public class TerrainOpening implements ITerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 2;
	private static final int MAX_GENERATION_LENGTH = 6;

	/**
	 * minimal Terrain height
	 */
	private static final int MIN_HEIGHT = 3;

	/**
	 * This value describes the minimum value of the sum of both top and bottom terrain<br>
	 * This needs to be at least two times MIN_HEIGHT
	 */
	private static final int MAX_CORRIDOR_HEIGHT = 6;

	private int weight;

	public TerrainOpening(int i) {
		this.weight = i;
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		int max = Math.min(blocksToGenerate,  MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1));
		int generated = 0;
		int bot = lastPair.bottom - 1;
		int top = lastPair.top - 1;
		while(bot >= MIN_HEIGHT && top >= MIN_HEIGHT && bot + top >= MAX_CORRIDOR_HEIGHT && generated < max) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			pair.top = top;
			pair.bottom = bot;
			top--;
			bot--;
			generated++;
		}
		return generated;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		int bot = last.bottom - MIN_GENERATION_LENGTH;
		int top = last.top -  MIN_GENERATION_LENGTH;
		if(bot < MIN_HEIGHT || top < MIN_HEIGHT || bot + top < MAX_CORRIDOR_HEIGHT) {
			return 0;
		}
		return MIN_GENERATION_LENGTH;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
