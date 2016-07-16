package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityItem;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.game.player.abliity.Abilities;
import net.brenig.pixelescape.game.player.effects.EffectShield;
import net.brenig.pixelescape.game.player.effects.EffectSlow;
import net.brenig.pixelescape.game.player.effects.EffectSmallBarricades;
import net.brenig.pixelescape.game.worldgen.WeightedList;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.FilteredElementProvider;
import net.brenig.pixelescape.lib.LogHelper;

import java.util.Random;

/**
 * Generator that generates {@link EntityItem} at specified intervalls
 */
public class ItemGenerator implements ISpecialWorldGenerator {

	private static final int spawnOffset = 40;

	private final int minDistance;
	private final int variableDistance;

	private final int startMinDistance;
	private final int startVariableDistance;

	private int nextItemXPos;

	private FilteredElementProvider<Item> itemList;

	public ItemGenerator(int minDistance, int maxDistance, int startMinDistance, int startMaxDistance, FilteredElementProvider<Item> itemList) {
		this.minDistance = minDistance;
		this.variableDistance = maxDistance - minDistance;
		this.startMinDistance = startMinDistance;
		this.startVariableDistance = startMaxDistance - startMinDistance;
		this.itemList = itemList;
	}

	@Override
	public void generate(WorldGenerator generator, World world, Random rand, GameMode mode) {
		if (world.getCurrentScreenEnd() + spawnOffset > nextItemXPos) {
			EntityItem entity = world.createEntity(EntityItem.class);
			final int blockIndex = world.convertWorldCoordinateToLocalBlockIndex(nextItemXPos);
			final int minY = world.getTerrainBotHeightReal(blockIndex);
			final int maxY = world.getTerrainTopHeightReal(blockIndex);

			entity.setPosition(nextItemXPos, minY + rand.nextInt(maxY - minY));
			entity.setItem(itemList.getRandomValue(rand));

			world.spawnEntity(entity);

			LogHelper.debug("Item spawned: " + entity.getItem() + ", @x: " + entity.getXPos() + ", y: " + entity.getYPos());

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

	public static WeightedList<Item> createDefaultItemList() {
		WeightedList<Item> out = new WeightedList<Item>();
		out.add(10, Abilities.BLINK);
		out.add(5, EffectShield.ITEM);
		out.add(10, EffectSlow.ITEM);
		out.add(8, EffectSmallBarricades.ITEM);
		return out;
	}
}
