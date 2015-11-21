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
			if(lastPair.getBot() - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
				up = false;
			}
		} else {
			if(lastPair.getTop() - MIN_GENERATION_LENGTH < MIN_HEIGHT) {
				up = true;
			}
		}
		int generateAmount = Math.min(blocksToGenerate, Math.min(MAX_GENERATION_LENGTH, (up ? lastPair.getBot() : lastPair.getTop()) - MIN_HEIGHT + 1));
		if(generateAmount <= 0) {
			return 0;
		}
		for(int i = 0; i < generateAmount; i++) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			if(up) {
				pair.setBot(lastPair.getBot() - (i));
				pair.setTop(lastPair.getTop() + (i));
			} else {
				pair.setBot(lastPair.getBot() + (i));
				pair.setTop(lastPair.getTop() - (i));
			}
		}
		return generateAmount;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		if(last.getTop() - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT && last.getBot() - MIN_GENERATION_LENGTH + 1 < MIN_HEIGHT) {
			return 0;
		}
		return MIN_GENERATION_LENGTH;
	}
}
