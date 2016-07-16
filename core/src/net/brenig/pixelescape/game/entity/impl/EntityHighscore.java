package net.brenig.pixelescape.game.entity.impl;

import com.badlogic.gdx.graphics.Color;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.impl.particle.EntityCrashParticle;
import net.brenig.pixelescape.render.WorldRenderer;

import java.util.Random;

/**
 * dummy entity, that renders the current highscore
 */
public class EntityHighscore extends Entity {

	private boolean isDead;

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if (getMinX() < world.getCurrentScreenEnd()) {
			final float pos = xPos - world.player.getBonusScore();
			if (world.player.getXPos() > pos) {
				final Random random = world.getRandom();
				final int yEnd = world.getTerrainTopHeightRealForCoord((int) pos);
				final int yStart = world.getTerrainBotHeightRealForCoord((int) pos);
				final int yDiff = yEnd - yStart;
				final int maxCount = yDiff / EntityCrashParticle.SIZE;
				final int yOffset = (yDiff - maxCount * EntityCrashParticle.SIZE) / 2;
				for (int i = 0; i < maxCount; i++) {
					EntityCrashParticle e = world.createEntity(EntityCrashParticle.class);
					e.setPosition(pos, i * EntityCrashParticle.SIZE + yOffset + yStart);
					e.setColor(Color.BLUE);
					e.setVelocity((random.nextFloat() - 0.5F) * 80, (random.nextFloat() - 0.5F) * 40);
					world.spawnEntity(e);
				}
				final float scoreModifier = 1 - 1 / (world.player.getXVelocity() * 0.1F);
				renderer.applyForceToScreen((2 + random.nextFloat()) * scoreModifier * 0.2F, 0);
				isDead = true;
			}
			renderer.getRenderManager().setColor(Color.BLUE);
			renderer.renderRectWorld(pos, yPos, EntityCrashParticle.SIZE, world.getWorldHeight());
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		xPos = world.getScreen().game.userData.getHighScore(world.getScreen().getGameMode()) + world.player.getXPosScreen();
		yPos = 0;
	}

	@Override
	public boolean isDead() {
		return isDead || super.isDead();
	}

	@Override
	public void reset() {
		super.reset();
		isDead = false;
	}
}
