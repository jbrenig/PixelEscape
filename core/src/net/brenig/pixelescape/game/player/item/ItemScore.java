package net.brenig.pixelescape.game.player.item;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.player.Item;

/**
 * Item that increases the players score by a flat value
 */
public class ItemScore implements Item {

	/**
	 * default Item, giving 500 flat score
	 */
	public static final ItemScore ITEM = new ItemScore(500);

	private final int score;

	/**
	 * @param score amount of score added
	 */
	public ItemScore(int score) {
		this.score = score;
	}


	@Override
	public Drawable getItemDrawable(GameAssets assets) {
		return assets.getMissingTexture();
	}

	@Override
	public boolean onCollect(EntityPlayer player) {
		//TODO add mechanic
		return true;
	}
}
