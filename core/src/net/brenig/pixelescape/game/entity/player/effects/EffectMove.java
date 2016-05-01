package net.brenig.pixelescape.game.entity.player.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.Item;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * effect that increases player vertical movement
 */
public class EffectMove extends StatusEffectTimed {

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

	public EffectMove(EntityPlayer player) {
		super(player, 10);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta) {

	}

	@Override
	public void onEffectAdded(EntityPlayer player) {
		player.addYVelocityFactor(PLAYER_VERTICAL_VELOCITY_MOD);
	}

	@Override
	public void onEffectRemove(EntityPlayer player) {
		player.addYVelocityFactor(-PLAYER_VERTICAL_VELOCITY_MOD);
	}

	@Override
	public void updateRenderColor(ShapeRenderer renderer) {
		renderer.setColor(0.7F, 0.6F, 0.1F, 0);
	}
}
