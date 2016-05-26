package net.brenig.pixelescape.game.player.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

public class EffectSlow extends StatusEffectTimed {

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

	private float velocityAmount;

	private final static float xVelocityFactor = 0.2F;
	private final static float maximumVelocityDecrease = 100F;

	public EffectSlow(EntityPlayer player) {
		super(player, 10);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta) {
		//TODO slow effect??
	}

	@Override
	public void onEffectAdded(EntityPlayer player) {
		this.velocityAmount = Math.min(player.getXVelocity() * xVelocityFactor, maximumVelocityDecrease);
		player.addXVelocityModifier(-velocityAmount);
	}

	@Override
	public void onEffectRemove(EntityPlayer player) {
		player.addXVelocityModifier(velocityAmount);
	}

	@Override
	public void updateRenderColor(ShapeRenderer renderer) {
		renderer.setColor(0.3F, 0.6F, 0.5F, 0);
	}
}
