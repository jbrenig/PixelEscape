package net.brenig.pixelescape.game.entity.player.effects;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.Item;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;

public class EffectShield extends StatusEffect {


	public static final Item ITEM = new Item() {
		@Override
		public Drawable getItemDrawable(GameAssets assets) {
			//TODO proper textures
			return assets.getMissingTexture();
		}

		@Override
		public boolean onCollect(EntityPlayer player) {
			player.addEffect(new EffectSlow(player));
			return true;
		}
	};

	private float timeRemaining = 8F;

	public EffectShield(EntityPlayer player) {
		super(player);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, float xPos, float yPos, float delta) {
		//TODO render effect
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
	public boolean onPlayerCollide(EntityPlayer player) {
		if(effectActive()) {
			timeRemaining = 0;
			player.setImmortal(1);
			return false;
		}
		return true;
	}
}
