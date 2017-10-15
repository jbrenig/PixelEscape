package net.brenig.pixelescape.game.player.effects;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * effect that causes barricades to be smaller for some time
 */
public class EffectSmallBarricades extends StatusEffectTimed {

	public static final Item ITEM = new Item() {
		@Override
		public Drawable getItemDrawable(GameAssets assets) {
			return assets.getItemSmallBarricades();
		}

		@Override
		public boolean onCollect(EntityPlayer player) {
			player.addOrUpdateEffect(new EffectSmallBarricades(player));
			return true;
		}
	};

	public EffectSmallBarricades(EntityPlayer player) {
		super(player, 10);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta) {

	}

	@Override
	public void onEffectAdded(EntityPlayer player) {
		player.getWorld().getWorldGenerator().setObstacleSizeModifier(player.getWorld().getWorldGenerator().getObstacleSizeModifier() - 0.2F);
	}

	@Override
	public void onEffectRemove(EntityPlayer player) {
		player.getWorld().getWorldGenerator().setObstacleSizeModifier(player.getWorld().getWorldGenerator().getObstacleSizeModifier() + 0.2F);
	}
}
