package net.brenig.pixelescape.game.entity.player.abliity;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.screen.GameScreen;

public class AbilityBlink implements IAbility {

	private final static float maxCooldown = 10;
	private final static float range = Reference.BLOCK_WIDTH * 4;

	private float cooldown = 0;

	@Override
	public boolean onActivate(GameScreen screen, World world, EntityPlayer player) {
		cooldown = maxCooldown;
		player.increaseXPos(range);
		float oldX = screen.worldRenderer.getTargetX();
		float oldY = screen.worldRenderer.getTargetY();
		screen.worldRenderer.setPosition(screen.worldRenderer.getXPos() + range, screen.worldRenderer.getYPos());
		screen.worldRenderer.moveScreenTo(oldX, oldY, Reference.BLOCK_WIDTH * 4, 0);
		return true;
	}

	@Override
	public void update(World world, EntityPlayer player, float delta) {
		cooldown -= delta;
		if(cooldown < 0) {
			cooldown = 0;
		}
	}

	@Override
	public void render(WorldRenderer render, World world, EntityPlayer player, float delta) {

	}

	@Override
	public float cooldownRemaining() {
		return cooldown / maxCooldown;
	}

	public Drawable getDrawable(GameAssets assets) {
		return assets.getItemBlink();
	}
}
