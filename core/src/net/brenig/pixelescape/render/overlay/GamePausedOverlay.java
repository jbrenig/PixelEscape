package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.GameScreen;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.PixelDialog;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.render.ui.ingame.ScoreWidget;
import net.brenig.pixelescape.render.ui.ingame.StageManagerGame;

/**
 * Displays when game gets paused
 */
public class GamePausedOverlay extends Overlay implements InputProcessor {

	private static final float ANIM_TIME_PAUSED = 0.4F;
	private final StageManagerGame stage;

	private final int highscore;

	private float animationProgress = 0;

	public GamePausedOverlay(final GameScreen screen) {
		super(screen);
		stage = new StageManagerGame(screen);
		highscore = screen.game.userData.getHighScore(screen.getGameMode());

		Table table = stage.createHeadUiLayoutTable();

		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
//		TextButton mainMenu = new TextButton("Main Menu", screen.game.getSkin());
//		mainMenu.addListener(new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				screen.showMainMenu();
//			}
//		});
//		table.add(mainMenu);

		TextButton btnResume = new TextButton("Resume", screen.game.getSkin());
		btnResume.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resumeGame();
			}
		});
		table.add(btnResume);

		table.add(new HorizontalSpacer());
		table.add(Utils.addFullScreenButtonToTable(Utils.addSoundAndMusicControllerToLayout(screen.game, Utils.createUIHeadLayout(screen.game))));
		table.add(new ScoreWidget(screen));

		Table content = stage.createContentUiLayoutTable();
		content.add(new VerticalSpacer());
		content.row().expandX();
		TextButton btnMainMenu = new TextButton("Main Menu", screen.game.getSkin());
		btnMainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gotoMainMenu();

			}
		});
		content.add(btnMainMenu).center().bottom().pad(30);

		stage.getRootTable().layout();

		float oldX = btnMainMenu.getX();
		float oldY = btnMainMenu.getY();
		btnMainMenu.setVisible(false);
		btnMainMenu.addAction(Actions.sequence(Actions.moveTo(oldX, btnMainMenu.getY() - 60), Actions.visible(true), Actions.moveTo(oldX, oldY, 0.1F)));

	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(new InputMultiplexer(stage.getInputProcessor(), this));
		screen.game.gameMusic.fadeOutToPause();
	}

	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if(Reference.SCREEN_TINT_STRENGTH > 0 && animationProgress > 0) {
			renderScreenTint(Utils.easeOut(animationProgress, ANIM_TIME_PAUSED, 2) * Reference.SCREEN_TINT_STRENGTH);
		}
	}

	@Override
	public void render(float delta) {
		//Game Paused
		screen.game.getRenderManager().begin();
		screen.game.getFont().setColor(0, 0, 1, 1);
		screen.game.getFont().getData().setScale(2, 4);
		screen.getFontLayout().setText(screen.game.getFont(), "Game Paused!");
		float xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtGameOverHeight = screen.getFontLayout().height / 2;
		float yPos = ((2 * screen.world.getWorldHeight()) / 3) + screen.getUiPos() + txtGameOverHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Score
		screen.game.getFont().setColor(0, 1, 0, 1);
		screen.game.getFont().getData().setScale(1.2F);
		screen.getFontLayout().setText(screen.game.getFont(), "Your score: " + screen.world.getPlayer().getScore());
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtScoreHeight = screen.getFontLayout().height / 2;
		yPos -= txtGameOverHeight + screen.game.getFont().getLineHeight()+ txtScoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Highscore
		screen.game.getFont().setColor(0, 0, 1, 1);
		screen.game.getFont().getData().setScale(1.0F);
		screen.getFontLayout().setText(screen.game.getFont(), "Highscore: " + highscore);
		xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtHighscoreHeight = screen.getFontLayout().height / 2;
		yPos -= screen.game.getFont().getLineHeight() + txtScoreHeight + txtHighscoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.draw(screen.game.getRenderManager());
		stage.act(delta);

		animationProgress += delta;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void onResize(int width, int height) {
		stage.updateStageToGameBounds(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	private void restartMusic() {
		screen.game.gameMusic.play(true);
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
		switch (keycode) {
			case Input.Keys.SPACE:
				resumeGame();
				return true;
			case Input.Keys.ESCAPE:
				gotoMainMenu();
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

	private void resumeGame() {
		screen.setOverlay(new CountDownOverlay(screen));
		restartMusic();
	}

	/**
	 * display dialog asking to go to main menu
	 */
	private void gotoMainMenu() {
		final PixelDialog dialog = new PixelDialog("Sure?", screen.game.getSkin());
		dialog.setMovable(false);
		dialog.setModal(true);
		dialog.setPrefWidth(stage.getStageViewport().getWorldWidth() * 0.7F);
		dialog.setWidth(stage.getStageViewport().getWorldWidth() * 0.7F);
		dialog.label("Quit to main menu?");
		dialog.buttonYes(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dialog.hide();
				screen.showMainMenu();
			}
		});
		dialog.buttonNo(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dialog.hide();
			}
		});
		dialog.init();
		dialog.show(stage.getUiStage());
	}
}
