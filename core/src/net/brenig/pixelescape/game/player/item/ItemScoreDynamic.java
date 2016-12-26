package net.brenig.pixelescape.game.player.item;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingText;
import net.brenig.pixelescape.game.player.Item;

/**
 * Item that increases the players score by a flat value
 */
public class ItemScoreDynamic implements Item {

	/**
	 * default Item, giving a 5% bonus score
	 */
	public static final ItemScoreDynamic ITEM = new ItemScoreDynamic(0.05F);

	private final float scoreMod;

	/**
	 * @param scoreMod amount of score added (factor based on player score --> 0.05F : 5% of current player score
	 */
	public ItemScoreDynamic(float scoreMod) {
		this.scoreMod = scoreMod;
	}


	@Override
	public Drawable getItemDrawable(GameAssets assets) {
		return assets.getItemScore();
	}

	@Override
	public boolean onCollect(EntityPlayer player) {
		final int score = (int) (scoreMod * (float) player.getScore());
		player.addBonusScore(score);
		EntityFadingText entity = player.getWorld().createEntity(EntityFadingText.class);
		entity.setText("+" + score, 0.8F);
		entity.setPosition(player.getXPos(), player.getYPos());
		player.getWorld().spawnEntity(entity);
		return true;
	}
}
