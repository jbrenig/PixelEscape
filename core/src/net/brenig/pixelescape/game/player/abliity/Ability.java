package net.brenig.pixelescape.game.player.abliity;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.screen.GameScreen;

import java.io.Serializable;

public abstract class Ability implements Item, Serializable {


	private final float cooldown;

	public Ability() {
		this.cooldown = 0;
	}

	public Ability(float cooldown) {
		this.cooldown = cooldown;
	}

	/**
	 * gets called when player tries to use ability
	 *
	 * @param world  the current world
	 * @param player the current player entity
	 * @return whether ability was executed (if true is returned the player will have "used" the ability and might lose his item)
	 */
	public abstract boolean onActivate(GameScreen screen, World world, EntityPlayer player);

	/**
	 * @return cooldown time between uses of this item
	 */
	public float getCooldown() {
		return cooldown;
	}

	/**
	 * @param assets game assets
	 * @return ability icon
	 */
	public abstract Drawable getDrawable(GameAssets assets);

	/**
	 * called when player tries to collect this item
	 * <p>
	 * it will not yet be added to the players itemslot
	 * </p>
	 *
	 * @param player hte player that collected this item
	 * @return true if this ability can be collected
	 * @see Item
	 */
	@Override
	public boolean onCollect(EntityPlayer player) {
		player.addAbility(this, 1);
		return true;
	}

	@Override
	public Drawable getItemDrawable(GameAssets assets) {
		return getDrawable(assets);
	}
}
