package net.brenig.pixelescape.game.player.item;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.player.Item;

/**
 * Item that adds one life to the players remaining lives
 */
public class ItemLife implements Item {

	public static final ItemLife ITEM = new ItemLife();

	@Override
	public Drawable getItemDrawable(GameAssets assets) {
		return assets.getHeartDrawable();
	}

	@Override
	public boolean onCollect(EntityPlayer player) {
		player.setExtraLives(player.getExtraLives() + 1);
		return true;
	}
}
