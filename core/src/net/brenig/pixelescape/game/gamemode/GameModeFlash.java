package net.brenig.pixelescape.game.gamemode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.game.player.PlayerMovementController;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator;
import net.brenig.pixelescape.lib.Names;
import net.brenig.pixelescape.lib.Reference;

public class GameModeFlash extends GameMode {

	@Override
	public String getScoreboardName() {
		return Names.SCOREBOARD_FLASH;
	}

	@Override
	public String getGameModeName() {
		return "Flash";
	}

	@Override
	public TextureRegion getIcon(GameAssets assets) {
		return assets.getHeart(); //TODO icon
	}

	@Override
	public PlayerMovementController getPlayerMovementController() {
		return new FlashMovementController();
	}


	@Override
	public float getStartingSpeed() {
		return super.getStartingSpeed() * 4;
	}

	@Override
	public float getSpeedIncreaseFactor() {
		return super.getSpeedIncreaseFactor() * 4;
	}

	@Override
	public float getMaxEntitySpeed() {
		return super.getMaxEntitySpeed() * 1.4F;
	}

	@Override
	public void registerWorldGenerators(WorldGenerator worldGenerator) {
		worldGenerator.registerDefaultTerrainGenerators();
		worldGenerator.addSpecialGenerator(new BarricadeGenerator((int) (Reference.TARGET_RESOLUTION_X * 0.7F)));
	}

	private class FlashMovementController implements PlayerMovementController {

		@Override
		public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
			if(manager.isTouched()) {
				player.setYPosition(world.convertMouseYToWorldCoordinate(game.getScaledMouseY()));
			}
			player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
		}

		@Override
		public void reset() {

		}
	}
}
