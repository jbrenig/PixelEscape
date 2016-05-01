package net.brenig.pixelescape.game.entity.player.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.game.entity.player.EntityPlayer;

/**
 * Base class for status effects that have a timer
 */
public abstract class StatusEffectTimed extends StatusEffect {

	private final float duration;
	protected float timeRemaining;

	public StatusEffectTimed(EntityPlayer player, float duration) {
		super(player);
		this.duration = duration;
		this.timeRemaining = duration;
	}

	@Override
	public void update(float delta) {
		timeRemaining -= delta;
	}

	@Override
	public boolean effectActive() {
		return timeRemaining > 0;
	}

	@Override
	public float getScaledTime() {
		return timeRemaining / duration;
	}

	@Override
	public void updateRenderColor(ShapeRenderer renderer) {
		renderer.setColor(0.4F, 0.4F, 0.4F, 0);
	}
}
