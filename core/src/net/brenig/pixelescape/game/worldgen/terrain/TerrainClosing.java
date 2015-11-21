package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.Reference;

import java.util.Random;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public class TerrainClosing implements ITerrainGenerator {

	private static final int MIN_GENERATION_LENGTH = 3;
	private static final int MAX_GENERATION_LENGTH = 6;

	private int weight;

	public TerrainClosing(int i) {
		this.weight = i;
	}

	@Override
	public int generate(World world, TerrainPair lastPair, int blocksToGenerate, int generatedBlocksIndex, Random random) {
		int max = Math.min(blocksToGenerate,  MIN_GENERATION_LENGTH + random.nextInt(MAX_GENERATION_LENGTH - MIN_GENERATION_LENGTH + 1));
		int generated = 0;
		int bot = lastPair.getTop();
		int top = lastPair.getBot();
		while(bot <= Reference.MAX_HEIGHT && top <= Reference.MAX_HEIGHT && bot + top <= Reference.MAX_TERRAIN_SUM && generated < max) {
			TerrainPair pair = world.getCreateTerrainPairForGeneration();
			pair.setBot(top);
			pair.setTop(bot);
			top++;
			bot++;
			generated++;
		}
		return generated;
	}

	@Override
	public int getMinGenerationLength(TerrainPair last) {
		int bot = last.getTop() + MIN_GENERATION_LENGTH - 1;
		int top = last.getBot() +  MIN_GENERATION_LENGTH - 1;
		if(bot > Reference.MAX_HEIGHT || top > Reference.MAX_HEIGHT || bot + top > Reference.MAX_TERRAIN_SUM) {
			return 0;
		}
		return MIN_GENERATION_LENGTH;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
