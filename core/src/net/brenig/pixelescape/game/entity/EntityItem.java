package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.render.WorldRenderer;

public class EntityItem extends Entity {

	private final static int SIZE = 32;
	private final static int RADIUS = SIZE / 2;

	private IAbility ability;

	public EntityItem(World world) {
		super(world);
	}

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

	public void setAbility(IAbility ability) {
		this.ability = ability;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, float x, float y, float delta) {
		renderer.renderDrawableAbsolute(ability.getDrawable(game.getGameAssets()), xPos - 9, yPos - 9, 18, 18);
	}

	@Override
	public boolean update(float delta, InputManager inputManager) {
		world.getPlayer().doesAreaIntersectWithEntity(0, 0, 0, 0);
		return false;
	}

	@Override
	public boolean isDead() {
		return false;
	}
}
