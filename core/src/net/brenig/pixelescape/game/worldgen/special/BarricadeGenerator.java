package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.game.worldgen.Barricade;
import net.brenig.pixelescape.lib.CycleArray;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;

import java.util.Random;

public class BarricadeGenerator implements ISpecialWorldGenerator {

	private final CycleArray<Barricade> obstacles;

	public BarricadeGenerator() {
		obstacles = new CycleArray<Barricade>(3);
	}

	@Override
	public void generate(World world, Random rand, GameMode mode) {

		if(mode.shouldGenerateBarricades(world)) {
			while (obstacles.getOldest() == null || obstacles.getOldest().posX < world.getPlayer().getXPos() - world.getWorldWidth() / 2) {
				Barricade last = obstacles.getNewest();
				int oldX = 0;
				if (last != null) {
					oldX = last.posX;
				}
				Barricade b = getCreateObstacleForGeneration(world);
				b.posX = (int) (oldX + world.getWorldWidth() * 0.7);
				b.posY = rand.nextInt(world.getWorldHeight() - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH;
			}
		}

		updateBarricades(world);
	}

	public Barricade getCreateObstacleForGeneration(World world) {
		Barricade b = obstacles.getOldest();
		if (b == null) {
			b = new Barricade(world);
			obstacles.add(b);
			world.spawnEntity(b);
			return b;
		} else {
			obstacles.cycleForward();
			b.moved = false;
			return b;
		}
	}

	public void updateBarricades(World world) {
		for (int i = 0; i < obstacles.size(); i++) {
			Barricade b =  obstacles.get(i);
			if(b == null) continue;
			if (!b.moved && b.posX > world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH && b.posX < world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH * 2) {
				b.moved = true;
				updateBarricade(world, b);
			}
		}
	}

	private void updateBarricade(World world, Barricade b) {
		int localIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
		//LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);
		if (!(localIndex < 0)) {
			//LogHelper.debug("Index convert from: " + b.posX + ", to: " + localIndex);

			//Move barricade into level
			if (b.posY < world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
				b.posY += world.getBotBlockHeight(localIndex) * Reference.BLOCK_WIDTH - b.posY;
			} else if (b.posY > world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) {
				b.posY += (world.getWorldHeight() - world.getTopBlockHeight(localIndex) * Reference.BLOCK_WIDTH) - b.posY;
			}

			//Leave gap for player
			int checkRadius = (int) ((world.getPlayer().getVelocity() / Reference.MAX_ENTITY_SPEED) * Reference.OBSTACLE_X_CHECK_RADIUS_MAX);
			if (PixelEscape.rand.nextBoolean()) {
				LogHelper.debug("Correcting Bottom Barricade @ x: " + b.posX + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.posX) + ") y: " + b.posY);
				b.posY += getAmountToCorrectBottom(world, b, checkRadius);
				LogHelper.debug("Corrected to y: " + b.posY);
			} else {
				LogHelper.debug("Correcting Top Barricade @ x: " + b.posX + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.posX) + ") y: " + b.posY);
				b.posY += getAmountToCorrectTop(world, b, checkRadius);
				LogHelper.debug("Corrected to y: " + b.posY);
			}
		}
	}

	private float getAmountToCorrectBottom(World world, Barricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
//        float posY = world.convertMouseYToWorldCoordinate(b.posY);
		float posY = b.posY;
		posY -= Barricade.getSizeY() / 2;

		float correction = 0;
		for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
			if (posXIndex + i < 0) {
				continue;
			}
			int blockHeight = world.getBotBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
			float neededCorrection = (blockHeight + Reference.OBSTACLE_MIN_SPACE) - posY;
			if (neededCorrection > correction) {
				correction = neededCorrection;
				LogHelper.debug("Correction for: x: " + world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(posXIndex + i)) + "(" + (posXIndex + i) + "), y: " + blockHeight + ", amount: " + correction + ", oldY: " + posY);
			}
		}
		return correction;
	}

	private float getAmountToCorrectTop(World world, Barricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.posX);
		float posY = b.posY;
//        float posY = world.convertMouseYToWorldCoordinate(b.posY);
		posY += Barricade.getSizeY() / 2;

		float correction = 0;
		for (int i = (-1) * checkRadius; i <= checkRadius; i++) {
			if (posXIndex + i < 0) {
				continue;
			}
			int blockHeight = world.getWorldHeight() - world.getTopBlockHeight(posXIndex + i) * Reference.BLOCK_WIDTH;
			float neededCorrection = (blockHeight - Reference.OBSTACLE_MIN_SPACE) - posY;
			if (neededCorrection < correction) {
				correction = neededCorrection;
				LogHelper.debug("Correction for: x: " + world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(posXIndex + i)) + "(" + (posXIndex + i) + "), y: " + blockHeight + ", amount: " + correction + ", oldY: " + posY);
			}
		}
		return correction;
	}

	@Override
	public void reset() {
		obstacles.clear();
	}
}
