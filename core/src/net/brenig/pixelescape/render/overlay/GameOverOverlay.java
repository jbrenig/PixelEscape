package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Displays when Player crashed
 */
public class GameOverOverlay extends OverlayWithUi implements InputProcessor {

	private static final float ANIM_TIME_GAME_OVER = 0.6F;
	private static final float TIME_TO_WAIT = 1.2F;

	private float animationProgress = 0;

	private final TextButton mainMenu;
	private final TextButton restartGame;

	private final int highscore;


	public GameOverOverlay(final GameScreen screen) {
		super(screen);
		highscore = screen.game.userData.getHighScore(screen.getGameMode());


		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		Table table = stage.createHeadUiLayoutTable();
		table.add(new HorizontalSpacer());
		table.add(Utils.addFullScreenButtonToTable(screen.game, Utils.addSoundAndMusicControllerToLayout(screen.game)));

		Table content = stage.createContentUiLayoutTable();
		content.add(new VerticalSpacer());
		content.row().expandX().center();

		mainMenu = new TextButton("Main Menu", screen.game.getSkin());
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				screen.showMainMenu();
			}
		});
		mainMenu.setVisible(false);
		content.add(mainMenu).right().bottom().padBottom(40).padRight(10);


		restartGame = new TextButton("Retry", screen.game.getSkin());
		restartGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				restartGame();
			}
		});
		restartGame.setVisible(false);
		content.add(restartGame).left().bottom().padBottom(40).padLeft(10).width(mainMenu.getWidth());

		stage.getRootTable().layout();

		mainMenu.setVisible(false);
		restartGame.setVisible(false);
		float menuX = mainMenu.getX();
		float restartX = restartGame.getX();
		mainMenu.addAction(Actions.sequence(
				Actions.delay(TIME_TO_WAIT),
				Actions.moveTo(menuX + 1000, mainMenu.getY()),
				Actions.visible(true),
				Actions.moveTo(menuX, mainMenu.getY(), 0.8F, Interpolation.swing)));
		restartGame.addAction(Actions.sequence(
				Actions.delay(TIME_TO_WAIT + 0.2F),
				Actions.moveTo(restartX + 500, restartGame.getY()),
				Actions.visible(true),
				Actions.moveTo(restartX, restartGame.getY(), 0.8F, Interpolation.swing)));
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
		screen.game.getRenderManager().begin();
		screen.game.getFont().setColor(1, 0, 0, 1);
		screen.game.getFont().getData().setScale(2, 4);
		screen.getFontLayout().setText(screen.game.getFont(), "Game Over!");
		//Slide in
		float gameOverAnim = Math.max(0, screen.world.getWorldHeight() / 2 - screen.world.getWorldHeight() / 2 * Utils.easeOut(animationProgress, ANIM_TIME_GAME_OVER, 2));
		float xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtGameOverHeight = screen.getFontLayout().height / 2;
		float yPos = ((2 * screen.world.getWorldHeight()) / 3) + txtGameOverHeight + screen.getUiPos() + gameOverAnim;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Score
		screen.game.getFont().setColor(0, 1, 0, 1);
		screen.game.getFont().getData().setScale(1.2F);
		screen.getFontLayout().setText(screen.game.getFont(), "Your score: " + screen.world.getPlayer().getScore());
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtScoreHeight = screen.getFontLayout().height / 2;
		yPos -= txtGameOverHeight + screen.game.getFont().getLineHeight() + txtScoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Highscore
		if (highscore < screen.world.getPlayer().getScore()) {
			screen.game.getFont().setColor(0, 1, 0, 1);
			screen.game.getFont().getData().setScale(1.2F);
			screen.getFontLayout().setText(screen.game.getFont(), "New Highscore!");
		} else {
			screen.game.getFont().setColor(0, 0, 1, 1);
			screen.game.getFont().getData().setScale(1.0F);
			screen.getFontLayout().setText(screen.game.getFont(), "Highscore: " + highscore);
		}
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtHighscoreHeight = screen.getFontLayout().height / 2;
		yPos -= screen.game.getFont().getLineHeight() + txtHighscoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		animationProgress += delta;

		super.render(delta);
	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(new InputMultiplexer(stage.getInputProcessor(), this));
		screen.game.gameMusic.fadeOutToStop(0.6F);
	}

	@Override
	public void resume() {}

	@Override
	public void pause() {}

	private void restartMusic() {
		screen.game.gameMusic.setCurrentMusic(screen.getGameMusic());
		screen.game.gameMusic.play();
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
		if (animationProgress > TIME_TO_WAIT) {
			switch (keycode) {
				case Input.Keys.SPACE:
					restartGame();
					return true;
				case Input.Keys.ESCAPE:
					screen.showMainMenu();
					return true;
			}
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

	private void restartGame() {
		screen.resetToEmptyOverlay();
		screen.restart();
		restartMusic();
	}
}
