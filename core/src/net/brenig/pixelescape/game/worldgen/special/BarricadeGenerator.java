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
			while (obstacles.getOldest() == null || obstacles.getOldest().getXPos() < world.getPlayer().getXPos() - world.getWorldWidth() / 2) {
				Barricade last = obstacles.getNewest();
				int oldX = 0;
				if (last != null) {
					oldX = (int) last.getXPos();
				}
				Barricade b = getCreateObstacleForGeneration(world);
				final int newXPos = (int) (oldX + world.getWorldWidth() * 0.7);
				final int newYPos = rand.nextInt(world.getWorldHeight() - Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH * 2) + Reference.OBSTACLE_MIN_HEIGHT * Reference.BLOCK_WIDTH;
				b.setPosition(newXPos, newYPos);
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
			if (!b.moved && b.getXPos() > world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH && b.getXPos() < world.getCurrentScreenEnd() + Reference.BLOCK_WIDTH * 2) {
				b.moved = true;
				updateBarricade(world, b);
			}
		}
	}

	private void updateBarricade(World world, Barricade b) {
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
			int checkRadius = (int) ((world.getPlayer().getVelocity() / Reference.MAX_ENTITY_SPEED) * Reference.OBSTACLE_X_CHECK_RADIUS_MAX);
			if (PixelEscape.rand.nextBoolean()) {
				LogHelper.debug("Correcting Barricade leaving bottom gap @ x: " + b.getXPos() + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.getXPos()) + ") y: " + b.getYPos());
				b.setYPos(b.getYPos() + getAmountToCorrectBottom(world, b, checkRadius));
				LogHelper.debug("Corrected to y: " + b.getYPos());
			} else {
				LogHelper.debug("Correcting Barricade leaving top gap @ x: " + b.getXPos() + "(" + world.convertWorldCoordinateToLocalBlockIndex(b.getXPos()) + ") y: " + b.getYPos());
				b.setYPos(b.getYPos() + getAmountToCorrectTop(world, b, checkRadius));
				LogHelper.debug("Corrected to y: " + b.getYPos());
			}
		}
	}

	private float getAmountToCorrectBottom(World world, Barricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.getXPos());
		float posY = b.getYPos();
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
				LogHelper.debug("Correction for: x: " + b.getXPos() + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight);
			}
		}
		return correction;
	}

	private float getAmountToCorrectTop(World world, Barricade b, int checkRadius) {
		int posXIndex = world.convertWorldCoordinateToLocalBlockIndex(b.getXPos());
		float posY = b.getYPos();
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
				LogHelper.debug("Correction for: x: " + b.getXPos() + ", y: " + posY + ", amount: " + correction + ", blockHeight: " + blockHeight);
			}
		}
		return correction;
	}

	@Override
	public void reset(World world) {
		obstacles.clear();
	}
}
