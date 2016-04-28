package net.brenig.pixelescape.game.entity.player.effects;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.Item;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

public class EffectSlow extends StatusEffect {

	public static final Item ITEM = new Item() {
		@Override
		public Drawable getItemDrawable(GameAssets assets) {
			return assets.getItemSlow();
		}

		@Override
		public boolean onCollect(EntityPlayer player) {
			player.addEffect(new EffectSlow(player));
			return true;
		}
	};

	private float timeRemaining = 10;
	private float velocityAmount;

	private final static float xVelocityFactor = 0.2F;
	private final static float maximumVelocityDecrease = 100F;

	public EffectSlow(EntityPlayer player) {
		super(player);
		this.velocityAmount = Math.min(player.getXVelocity() * xVelocityFactor, maximumVelocityDecrease);
		player.addXVelocityModifier(-velocityAmount);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float xPos, float yPos, float delta) {
		//TODO slow effect??
	}

	@Override
	public void update(float delta) {
		timeRemaining -= delta;
		//TODO maybe gradually change player speed
	}

	@Override
	public boolean effectActive() {
		return timeRemaining > 0;
	}

	@Override
	public void onEffectRemove(EntityPlayer player) {
		player.addXVelocityModifier(velocityAmount);
	}
}
