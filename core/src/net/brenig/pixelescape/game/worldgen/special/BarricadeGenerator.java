package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityBarricade;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.LogHelperKt;
import net.brenig.pixelescape.lib.Reference;

import java.util.Random;

public class BarricadeGenerator implements ISpecialWorldGenerator {

	private static final int spawnOffset = 40;

	private final int barricadeOffset;

	private int nextBarricadePosition;

	/**
	 * @param barricadeOffset pixels until the next barricade is generated
	 */
	public BarricadeGenerator(int barricadeOffset) {
		this.barricadeOffset = barricadeOffset;
	}

	@Override
	public void generate(WorldGenerator generator, World world, Random rand, GameMode mode) {
		if (mode.shouldGenerateBarricades(world)) {
			if (world.getCurrentScreenEnd() + spawnOffset > nextBarricadePosition) {
				EntityBarricade barricade = world.createEntity(EntityBarricade.class);
				final int newXPos = nextBarricadePosition;
				final int newYPos = rand.nextInt(world.getWorldHeight() - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH;
				barricade.setPosition(newXPos, newYPos);
				barricade.applyWorldGenSizeModifier(generator.obstacleSizeModifier);
				updateBarricade(world, barricade, mode);
				world.spawnEntity(barricade);

				nextBarricadePosition = (newXPos + barricadeOffset);
			}
		}
	}

	/**
	 * checks blocks around the Barricade and tries to position the barricade (y-Pos) so that it is possible to get past is on higher player speeds
	 *
	 * @param world    world instance
	 * @param b        the barricade that needs to be moved
	 * @param gameMode current gamemode
	 */
	private void updateBarricade(World world, EntityBarricade b, GameMode gameMode) {
		int localIndex = world.convertWorldCoordinateToLocalBlockIndex(b.getXPos());
		//LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);
		if (!(localIndex < 0)) {
			//LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);

			//Move barricade into level
			if (b.getYPos() < world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
				b.setYPos(world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH);
			} else if (b.getYPos() > world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
				b.setYPos(world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH);
			}

			//Leave gap for player
			int checkRadius = (int) ((world.getPlayer().getXVelocity() / gameMode.getMaxEntitySpeed()) * Reference.OBSTACLE_X_CHECK_RADIUS_MAX);
			if (PixelEscape.Companion.getRand().nextBoolean()) {
				LogHelperKt.debug("Correcting Barricade leaving bottom gap @ x: " + b.getXPos() + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.getXPos()) + ") y: " + b.getYPos());
				b.setYPos(b.getYPos() + getAmountToCorrectBottom(world, b, checkRadius));
				LogHelperKt.debug("Corrected to y: " + b.getYPos());
			} else {
				LogHelperKt.debug("Correcting Barricade leaving top gap @ x: " + b.getXPos() + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.getXPos()) + ") y: " + b.getYPos());
				b.setYPos(b.getYPos() + getAmountToCorrectTop(world, b, checkRadius));
				LogHelperKt.debug("Corrected to y: " + b.getYPos());
			}
		}
	}

	private float getAmountToCorrectBottom(World world, EntityBarricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.getXPos());
		float posY = b.getYPos();
		posY -= b.getSizeY() / 2;

		float correction = 0;
		for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
			if (posXIndex + i < 0) {
				continue;
			}
			int blockHeight = world.getBotBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
			float neededCorrection = (blockHeight + Reference.OBSTACLE_MIN_SPACE) - posY;
			if (neededCorrection > correction) {
				correction = neededCorrection;
				LogHelperKt.debug("Correction for: x: " + b.getXPos() + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight);
			}
		}
		return correction;
	}

	private float getAmountToCorrectTop(World world, EntityBarricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.getXPos());
		float posY = b.getYPos();
		posY += b.getSizeY() / 2;

		float correction = 0;
		for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
			if (posXIndex + i < 0) {
				continue;
			}
			int blockHeight = world.getWorldHeight() - world.getTopBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
			float neededCorrection = (blockHeight - Reference.OBSTACLE_MIN_SPACE) - posY;
			if (neededCorrection < correction) {
				correction = neededCorrection;
				LogHelperKt.debug("Correction for: x: " + b.getXPos() + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight);
			}
		}
		return correction;
	}

	@Override
	public void reset(World world) {
		nextBarricadePosition = world.getWorldWidth() + spawnOffset;
	}
}
