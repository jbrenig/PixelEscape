package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.GameScreen;
import net.brenig.pixelescape.screen.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.screen.ui.ingame.StageManagerGame;

/**
 * Displays when Player crashed
 */
public class GameOverOverlay extends Overlay implements InputProcessor {

	private static final float ANIM_TIME_GAME_OVER = 0.6F;
	private static final float TIME_TO_WAIT = 1.2F;

	private float animationProgress = 0;

	private final StageManagerGame stage;
	private final TextButton mainMenu;


	public GameOverOverlay(final GameScreen screen) {
		super(screen);
		stage = new StageManagerGame(screen);

		Table table = stage.createHeadUiLayoutTable();

		mainMenu = new TextButton("Main Menu", screen.game.getSkin());
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				screen.showMainMenu();
			}
		});
		mainMenu.setVisible(false);
		table.add(mainMenu);
		table.add(new HorizontalSpacer());
		table.add(Utils.addFullScreenButtonToTable(screen.game, Utils.addSoundAndMusicControllerToLayout(screen.game)));
	}

	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if(Reference.SCREEN_TINT_STRENGTH > 0 && animationProgress > 0) {
			renderScreenTint(Utils.easeOut(animationProgress, ANIM_TIME_GAME_OVER, 2) * Reference.SCREEN_TINT_STRENGTH);
		}
	}

	@Override
	public void render(float delta) {

		//Game Over
		screen.game.batch.begin();
		screen.game.getFont().setColor(1, 0, 0, 1);
		screen.game.getFont().getData().setScale(2, 4);
		screen.getFontLayout().setText(screen.game.getFont(), "Game Over!");
		//Slide in
		float gameOverAnim = Math.max(0, screen.world.getWorldHeight() / 2 - screen.world.getWorldHeight() / 2 * Utils.easeOut(animationProgress, ANIM_TIME_GAME_OVER, 2));
		float xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtGameOverHeight = screen.getFontLayout().height / 2;
		float yPos = ((2 * screen.world.getWorldHeight()) / 3) + txtGameOverHeight + screen.getUiPos() + gameOverAnim;
		screen.game.getFont().draw(screen.game.batch, screen.getFontLayout(), xPos, yPos);
		screen.game.batch.end();

		//Score
		screen.game.batch.begin();
		screen.game.getFont().setColor(0, 1, 0, 1);
		screen.game.getFont().getData().setScale(1.2F);
		screen.getFontLayout().setText(screen.game.getFont(), "Your score: " + screen.world.getPlayer().getScore());
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtScoreHeight = screen.getFontLayout().height / 2;
		yPos -= txtGameOverHeight + screen.game.getFont().getLineHeight() + txtScoreHeight;
		screen.game.getFont().draw(screen.game.batch, screen.getFontLayout(), xPos, yPos);
		screen.game.batch.end();

		//Highscore
		screen.game.batch.begin();
		if (screen.game.userData.getHighScore(screen.getGameMode()) == screen.world.getPlayer().getScore()) {
			screen.game.getFont().setColor(0, 1, 0, 1);
			screen.game.getFont().getData().setScale(1.2F);
			screen.getFontLayout().setText(screen.game.getFont(), "New Highscore!");
		} else {
			screen.game.getFont().setColor(0, 0, 1, 1);
			screen.game.getFont().getData().setScale(1.0F);
			screen.getFontLayout().setText(screen.game.getFont(), "Highscore: " + screen.game.userData.getHighScore(screen.getGameMode()));
		}
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtHighscoreHeight = screen.getFontLayout().height / 2;
		yPos -= screen.game.getFont().getLineHeight() + txtHighscoreHeight;
		screen.game.getFont().draw(screen.game.batch, screen.getFontLayout(), xPos, yPos);
		screen.game.batch.end();

		//Info
		if (animationProgress > TIME_TO_WAIT) {
			mainMenu.setVisible(true);
			mainMenu.invalidateHierarchy();
			if ((animationProgress - TIME_TO_WAIT) % 2 < 1.2F) {
				screen.game.batch.begin();
				screen.game.getFont().setColor(0, 1, 0, 1);
				screen.game.getFont().getData().setScale(0.8F);
				screen.getFontLayout().setText(screen.game.getFont(), "Tap to continue!");
				xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
				yPos -= txtGameOverHeight + screen.game.getFont().getLineHeight() + txtScoreHeight + screen.getFontLayout().height / 2;
				screen.game.getFont().draw(screen.game.batch, screen.getFontLayout(), xPos, yPos);
				screen.game.batch.end();
			}
		}

		animationProgress += delta;
		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.draw();
		stage.act(delta);
	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(new InputMultiplexer(stage.getInputProcessor(), this));
		screen.game.gameMusic.fadeOutToStop(0.6F);
	}

	@Override
	public void onResize(int width, int height) {
		stage.updateStageToGameBounds(width, height);
	}

	@Override
	public void resume() {}

	@Override
	public void pause() {}

	@Override
	public void dispose() {
		stage.dispose();
	}

	private void restartMusic() {
		screen.game.gameMusic.setCurrentMusic(screen.getGameMusic());
		screen.game.gameMusic.play();
	}

	@Override
	public boolean shouldHideGameUI() {
		return true;
	}

	@Override
	public boolean doesPauseGame() {
		return true;
	}

	@Override
	public boolean canHideCursor() {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (animationProgress > TIME_TO_WAIT && keycode == Input.Keys.SPACE) {
			screen.resetToEmptyOverlay();
			screen.restart();
			restartMusic();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (animationProgress > TIME_TO_WAIT && screen.game.convertToScaled(screenY) > screen.getUiSize() + screen.getUiPos()) {
			screen.resetToEmptyOverlay();
			screen.restart();
			restartMusic();
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
