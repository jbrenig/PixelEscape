package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.EntityItem;
import net.brenig.pixelescape.game.entity.player.abliity.AbilityBlink;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.game.worldgen.WeightedList;

import java.util.Random;

/**
 * Generator that generates {@link net.brenig.pixelescape.game.entity.EntityItem} at specified intervalls
 */
public class ItemGenerator implements ISpecialWorldGenerator {

	private static final int spawnOffset = 40;

	private final int minDistance;
	private final int variableDistance;

	private final int startMinDistance;
	private final int startVariableDistance;

	private int nextItemXPos;

	private WeightedList<IAbility> abilityList;

	public ItemGenerator(int minDistance, int maxDistance, int startMinDistance, int startMaxDistance, WeightedList<IAbility> abilityList) {
		this.minDistance = minDistance;
		this.variableDistance = maxDistance - minDistance;
		this.startMinDistance = startMinDistance;
		this.startVariableDistance = startMaxDistance - startMinDistance;
		this.abilityList = abilityList;
	}

	@Override
	public void generate(World world, Random rand, GameMode mode) {
		if(world.getCurrentScreenEnd() + spawnOffset < nextItemXPos) {
			EntityItem entity = world.createEntity(EntityItem.class);
			final int blockIndex = world.convertWorldCoordinateToLocalBlockIndex(nextItemXPos);
			final int minY = world.getTerrainBotHeightReal(blockIndex);
			final int maxY = world.getTerrainTopHeightReal(blockIndex);

			entity.setPosition(nextItemXPos, minY + rand.nextInt(maxY - minY));
			entity.setAbility(abilityList.getRandomValue(rand));

			world.spawnEntity(entity);

			calculateNextItemXPos(rand);
		}
	}

	private void calculateNextItemXPos(Random random) {
		nextItemXPos += minDistance + random.nextInt(variableDistance);
	}

	@Override
	public void reset(World world) {
		nextItemXPos = world.getWorldWidth() + startMinDistance + world.getRandom().nextInt(startVariableDistance);
	}

	public WeightedList<IAbility> createDefaultAbilityList() {
		WeightedList<IAbility> out = new WeightedList<IAbility>();
		out.add(10, new AbilityBlink());
		return out;
	}
}
