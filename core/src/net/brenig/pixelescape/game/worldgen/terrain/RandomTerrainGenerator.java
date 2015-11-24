package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.Reference;

import java.util.Random;

/**
 * Generator that randomly generates terrain based on last terrain generated
 */
public class RandomTerrainGenerator extends AbstractTerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 1;

	public RandomTerrainGenerator(int weight) {
		super(weight);
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		TerrainPair pair = world.getCreateTerrainPairForGeneration();
		pair.setBot(Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.getBot() + random.nextInt(3) - 1)));
		pair.setTop(Math.max(Reference.MIN_HEIGHT, Math.min(Reference.MAX_HEIGHT, lastPair.getTop() + random.nextInt(3) - 1)));
		while(pair.getTop() + pair.getBot() > Reference.MAX_TERRAIN_SUM) {
			pair.setTop(pair.getTop() - 1);
			pair.setBot(pair.getBot() - 1);
		}
		return 1;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		return MIN_GENERATION_LENGTH;
	}
}
