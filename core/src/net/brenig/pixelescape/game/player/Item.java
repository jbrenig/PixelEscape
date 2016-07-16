package net.brenig.pixelescape.game.player;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityItem;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;


/**
 * Interface to be implemented by all kinds of items
 * <p>
 *     used to let the player collect items via {@link EntityItem}
 * </p>
 * <p>
 *     classes implementing this interface should usually ba singletons
 * </p>
 */
public interface Item {

	/**
	 * @param assets game assets
	 * @return the {@link Drawable} that should be used to display the item in world
	 */
	Drawable getItemDrawable(GameAssets assets);

	/**
	 * gets called when the player tries to collect the item
	 *
	 * note: when false is returned this method may get called every tick in which the player still collides with the {@link EntityItem}
	 * @return true if item was collected, false otherwise
	 */
	boolean onCollect(EntityPlayer player);
}
