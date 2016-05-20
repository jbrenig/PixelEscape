package net.brenig.pixelescape.game.player.abliity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.particle.EntityFadingParticle;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

public class AbilityBlink extends Ability {

	private final static float maxCooldown = 10;
	private final static float range = Reference.BLOCK_WIDTH * 4;

	public AbilityBlink() {
		super(maxCooldown);
	}

	@Override
	public boolean onActivate(GameScreen screen, World world, EntityPlayer player) {
		for(int i = 0; i < 60; i++) {
			EntityFadingParticle e = world.createEntity(EntityFadingParticle.class);
			e.setPosition(player.getXPos() + PixelEscape.rand.nextFloat() * 20 - 10, player.getYPos() + PixelEscape.rand.nextFloat() * 40 - 20);
			e.setColor(Color.BLUE);
			e.setFadeDuration(0.4F);
			e.setAccelerationFactor(0.99F, 0.99F);
			e.setVelocity(PixelEscape.rand.nextFloat() * 10 - 0.5F, (PixelEscape.rand.nextFloat() * 20 + 20) * (e.getYPos() > player.getYPos() ? 1 : -1));
			world.spawnEntity(e);
		}
		player.increaseXPos(range);

		final float oldX = screen.worldRenderer.getTargetX();
		screen.worldRenderer.setCameraXPosition(screen.worldRenderer.getXPos() + range);
		screen.worldRenderer.moveScreenTo(oldX, Reference.BLOCK_WIDTH * 4);
		return true;
	}

	public Drawable getDrawable(GameAssets assets) {
		return assets.getItemBlink();
	}
}
