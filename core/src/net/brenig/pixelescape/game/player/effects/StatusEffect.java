package net.brenig.pixelescape.game.player.effects;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.render.GameRenderManager;
import net.brenig.pixelescape.render.WorldRenderer;

public abstract class StatusEffect {

	protected EntityPlayer player;

	public StatusEffect(EntityPlayer player) {
		this.player = player;
	}

	public abstract void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta);

	public abstract void update(float delta);

	public abstract boolean effectActive();

	/**
	 * called when effect gets removed from the player
	 */
	public void onEffectRemove(EntityPlayer player) {
	}

	/**
	 * will get called when player collides
	 *
	 * @return false when player doesn't collide due to this statuseffect
	 */
	public boolean onPlayerCollide() {
		return true;
	}

	/**
	 * called when the effect gets added to the player
	 * <p>
	 * make changes to {@link EntityPlayer} here
	 * </p>
	 */
	public void onEffectAdded(EntityPlayer player) {
	}

	/**
	 * @return time remaining, until this effect ends (return 0 or less if not applicable)
	 */
	public float getScaledTime() {
		return 0;
	}

	/**
	 * updates shaperenderer to a custom color if needed
	 * <p>
	 * used for rendering remaining duration
	 * </p>
	 *
	 * @param renderer current renderer instance
	 */
	public void updateRenderColor(GameRenderManager renderer) {
	}
}
