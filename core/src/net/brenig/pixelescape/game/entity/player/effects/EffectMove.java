package net.brenig.pixelescape.game.entity.player.effects;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.Item;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * effect that increases player vertical movement
 */
public class EffectMove extends StatusEffect {

	private static final float PLAYER_VERTICAL_VELOCITY_MOD = 1;

	public static final Item ITEM = new Item() {
		@Override
		public Drawable getItemDrawable(GameAssets assets) {
			return assets.getItemMove();
		}

		@Override
		public boolean onCollect(EntityPlayer player) {
			player.addOrUpdateEffect(new EffectMove(player));
			return true;
		}
	};

	private float timeRemaining = 10;

	public EffectMove(EntityPlayer player) {
		super(player);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta) {

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
	public void onEffectAdded(EntityPlayer player) {
		player.addYVelocityFactor(PLAYER_VERTICAL_VELOCITY_MOD);
	}

	@Override
	public void onEffectRemove(EntityPlayer player) {
		player.addYVelocityFactor(-PLAYER_VERTICAL_VELOCITY_MOD);
	}
}
