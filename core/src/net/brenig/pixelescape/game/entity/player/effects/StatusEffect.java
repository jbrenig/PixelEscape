package net.brenig.pixelescape.game.entity.player.effects;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

public abstract class StatusEffect {

	protected EntityPlayer player;

	public StatusEffect(EntityPlayer player) {
		this.player = player;
	}

	public abstract void render(PixelEscape game, WorldRenderer renderer, float xPos, float yPos, float delta);

	public abstract void update(float delta);

	public abstract boolean effectActive();

	/**
	 * called when effect gets removed from the player
	 */
	public void onEffectRemove(EntityPlayer player) {}

	/**
	 * will get called when player collides
	 * @return false when player doesn't collide due to this statuseffect
	 */
	public boolean onPlayerCollide(EntityPlayer player) {
		return true;
	}
}
