package net.brenig.pixelescape.game.entity.impl;

import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * dummy entity, that renders the current highscore
 */
public class EntityHighscore extends Entity {

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if(getMinX() < world.getCurrentScreenEnd()) {
			renderer.getRenderManager().setColor(Color.BLUE);
			renderer.renderRectWorld(xPos, yPos, 4, world.getWorldHeight());
		}
	}

	public void init() {
		xPos = world.getScreen().game.userData.getHighScore(world.getScreen().getGameMode());
		yPos = 0;
	}
}
