package net.brenig.pixelescape.game.player.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingParticle;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.render.GameRenderManager;
import net.brenig.pixelescape.render.WorldRenderer;

public class EffectShield extends StatusEffectTimed {

	private static final int RENDER_EFFECT_RADIUS = 12;
	private static final int RENDER_EFFECT_SIZE = RENDER_EFFECT_RADIUS * 2;

	public static final Item ITEM = new Item() {
		@Override
		public Drawable getItemDrawable(GameAssets assets) {
			return assets.getItemShield();
		}

		@Override
		public boolean onCollect(EntityPlayer player) {
			player.addOrUpdateEffect(new EffectShield(player));
			return true;
		}
	};

	public EffectShield(EntityPlayer player) {
		super(player, 8);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, EntityPlayer player, float delta) {
		renderer.getRenderManager().begin();
		renderer.renderTextureRegion(game.getGameAssets().getEffectItemShield(), player.getXPosScreen() - RENDER_EFFECT_RADIUS, player.getYPos() - RENDER_EFFECT_RADIUS, RENDER_EFFECT_SIZE, RENDER_EFFECT_SIZE);
	}

	@Override
	public boolean onPlayerCollide() {
		if (effectActive()) {
			timeRemaining = 0;
			player.setImmortal(1);
			World world = player.getWorld();
			for (int i = 0; i < 20; i++) {
				EntityFadingParticle entity = world.createEntity(EntityFadingParticle.class);
				entity.setPosition(player.getXPos() + PixelEscape.Companion.getRand().nextFloat() * 20 - 10, player.getYPos() + PixelEscape.Companion.getRand().nextFloat() * 40 - 20);
				entity.setColor(Color.LIGHT_GRAY);
				entity.setFadeDuration(0.4F);
				entity.setAccelerationFactor(0.99F, 0.99F);
				entity.setVelocity(PixelEscape.Companion.getRand().nextFloat() * 10 - 0.5F, (PixelEscape.Companion.getRand().nextFloat() * 20 + 20) * (entity.getYPos() > player.getYPos() ? 1 : -1));
				world.spawnEntity(entity);
			}
			return false;
		}
		return true;
	}

	@Override
	public void updateRenderColor(GameRenderManager renderer) {
		renderer.setColor(0.5F, 0.4F, 0.7F, 1);
	}
}
