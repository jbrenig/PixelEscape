package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.TerrainPair;

import java.util.Random;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public class DiagonalCorridor extends AbstractTerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 4;
	private static final int MAX_GENERATION_LENGTH = 20;

	/**
	 * maximal Terrain height
	 */
	private static final int MIN_HEIGHT = 3;

	public DiagonalCorridor(int weight) {
		super(weight);
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		boolean up = random.nextBoolean();
		if(up) {
			if(lastPair.top - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
				up = false;
			}
		} else {
			if(lastPair.bottom - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
				up = true;
			}
		}
		int generateAmount = Math.min(blocksToGenerate, Math.min(MAX_GENERATION_LENGTH, (up ? lastPair.top : lastPair.bottom) - MIN_HEIGHT + 1));
		for(int i = 0; i < generateAmount; i++) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			if(up) {
				pair.top = lastPair.top - (i);
				pair.bottom = lastPair.bottom + (i);
			} else {
				pair.top = lastPair.top + (i);
				pair.bottom = lastPair.bottom - (i);
			}
		}
		return generateAmount;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		if(last.bottom - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT && last.top - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT) {
			return 0;
		}
		return MIN_GENERATION_LENGTH;
	}
}
