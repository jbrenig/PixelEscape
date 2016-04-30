package net.brenig.pixelescape.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Entity that contains one {@link Item}
 * <p>
 *     when the player collects the item effects will be handled by {@link Item#onCollect(EntityPlayer)}
 * </p>
 */
public class EntityItem extends Entity {

	private final static int SIZE = 18 * 2;
	private final static int RADIUS = SIZE / 2;

	private final static int BACKGROUND_SIZE_MOD = 14 * 2;
	private final static int BACKGROUND_OFFSET = BACKGROUND_SIZE_MOD / 2;

	private Item item;

	private boolean isDead = false;

	public EntityItem(World world) {
		super(world);
	}

	@Override
	public float getMinX() {
		return progress - RADIUS;
	}

	@Override
	public float getMaxX() {
		return progress + RADIUS;
	}

	@Override
	public float getMinY() {
		return yPos - RADIUS;
	}

	@Override
	public float getMaxY() {
		return yPos + RADIUS;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, float delta) {
		if(isDead) {
			return;
		}
		game.getRenderManager().begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		renderer.renderSimpleAnimationWorld(game.getGameAssets().getItemAnimatedBackground(), getMinX() - BACKGROUND_OFFSET, getMinY() - BACKGROUND_OFFSET, SIZE + BACKGROUND_SIZE_MOD, SIZE + BACKGROUND_SIZE_MOD, delta);
		renderer.renderDrawableWorld(item.getItemDrawable(game.getGameAssets()), getMinX(), getMinY(), SIZE, SIZE);
	}

	@Override
	public boolean update(float delta, InputManager inputManager) {
		if(!isDead && doesEntityIntersectWithEntity(world.getPlayer())) {
			if(item.onCollect(world.getPlayer())) {
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

	public Item getItem() {
		return item;
	}

	@Override
	public void reset() {
		super.reset();
		isDead = false;
		item = null;
	}
}
