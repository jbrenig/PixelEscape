package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;
import net.brenig.pixelescape.game.worldgen.TerrainPair;

import java.util.Random;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public class FlatCorridor implements ITerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 2;
	private static final int MAX_GENERATION_LENGTH = 4;


	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		int generatedBlocks = Math.min(blocksToGenerate, MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1));
		for(int i = 0; i < generatedBlocks; i++) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			pair.bottom = lastPair.bottom;
			pair.top = lastPair.top;
		}
		return generatedBlocks;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		return MIN_GENERATION_LENGTH;
	}
}
