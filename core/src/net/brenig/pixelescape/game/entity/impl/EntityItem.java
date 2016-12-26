package net.brenig.pixelescape.game.entity.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Entity that contains one {@link net.brenig.pixelescape.game.player.Item}
 * <p>
 * when the player collects the item effects will be handled by {@link net.brenig.pixelescape.game.player.Item#onCollect(EntityPlayer)}
 * </p>
 */
public class EntityItem extends Entity {

	private final static int SIZE = 32 * 2;
	private final static int RADIUS = SIZE / 2;

	private final static int ITEM_SIZE = 18 * 2;
	private final static int ITEM_RADIUS = ITEM_SIZE / 2;

	private net.brenig.pixelescape.game.player.Item item;

	private boolean isDead = false;

	@Override
	public float getMinX() {
		return xPos - RADIUS;
	}

	@Override
	public float getMaxX() {
		return xPos + RADIUS;
	}

	@Override
	public float getMinY() {
		return yPos - RADIUS;
	}

	@Override
	public float getMaxY() {
		return yPos + RADIUS;
	}

	public void setItem(net.brenig.pixelescape.game.player.Item item) {
		this.item = item;
	}

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if (isDead) {
			return;
		}
		game.getRenderManager().begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		renderer.renderSimpleAnimationWorld(game.getGameAssets().getItemAnimatedBackground(), getMinX(), getMinY(), SIZE, SIZE, delta);
		renderer.renderDrawableWorld(item.getItemDrawable(game.getGameAssets()), xPos - ITEM_RADIUS, yPos - ITEM_RADIUS, ITEM_SIZE, ITEM_SIZE);
	}

	@Override
	public boolean update(float delta, InputManager inputManager, GameMode gameMode) {
		if (!isDead && doesEntityIntersectWithEntity(world.getPlayer())) {
			if (item.onCollect(world.getPlayer())) {
				this.isDead = true;
				LogHelper.debug("Player collected item: " + item);
			}
		}
		return false;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

	public net.brenig.pixelescape.game.player.Item getItem() {
		return item;
	}

	@Override
	public void reset() {
		super.reset();
		isDead = false;
		item = null;
	}
}
