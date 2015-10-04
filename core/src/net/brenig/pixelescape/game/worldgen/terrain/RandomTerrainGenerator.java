package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.Reference;

import java.util.Random;

/**
 * Created by Jonas Brenig on 15.08.2015.
 */
public class RandomTerrainGenerator extends AbstractTerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 1;

	public RandomTerrainGenerator(int weight) {
		super(weight);
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		TerrainPair pair = world.getCreateTerrainPairForGeneration();
		pair.top = Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.getTop() + random.nextInt(3) - 1));
		pair.bottom = Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.getBottom() + random.nextInt(3) - 1));
		while(pair.bottom + pair.top > Reference.MAX_TERRAIN_SUM) {
			pair.bottom--;
			pair.top--;
		}
		return 1;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		return MIN_GENERATION_LENGTH;
	}
}
