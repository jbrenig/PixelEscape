package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;

/**
 * Basic terrain generator functionality
 */
public abstract class AbstractTerrainGenerator implements ITerrainGenerator {

	protected int weight;

	public AbstractTerrainGenerator(int weight) {
		this.weight = weight;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
