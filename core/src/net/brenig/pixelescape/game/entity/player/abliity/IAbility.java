package net.brenig.pixelescape.game.entity.player.abliity;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.screen.GameScreen;

public interface IAbility {


	/**
	 * gets called when player tries to use ability
	 * @param world the current world
	 * @param player the current player entity
	 * @return wheter ability was executed
	 */
	boolean onActivate(GameScreen screen, World world, EntityPlayer player);

	/**
	 * gets called every tick to update ability behaviour (and cooldown, etc.)
	 * @param world the current world
	 * @param player the current player entity
	 * @param delta time passed since last tick (in seconds)
	 */
	void update(World world, EntityPlayer player, float delta);

	/**
	 * gets called every tick to render ability behaviour
	 * @param render the current WorldRenderer instance
	 * @param world the current world
	 * @param player the current player entity
	 * @param delta time passed since last tick (in seconds)
	 */
	void render(WorldRenderer render, World world, EntityPlayer player, float delta);

	/**
	 * @return percentage how much time has passed (return 0F if ability is ready and 1F when the ability got used at this moment)
	 */
	float cooldownRemaining();

	/**
	 * @return ability icon
	 */
	Drawable getDrawable();
}
