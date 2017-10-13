package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.PixelDialog;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.render.ui.ingame.ScoreWidget;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Displays when game gets paused
 */
public class GamePausedOverlay extends OverlayWithUi implements InputProcessor {

	private static final float ANIM_TIME_GAME_OVER = 0.6F;
	private static final float ANIM_TIME_PAUSED = 0.4F;
	private static final float TIME_TO_WAIT = 1.2F;

	private final boolean isGameOver;

	private final int highscore;

	private float animationProgress = 0;

	public GamePausedOverlay(final GameScreen screen, final boolean isGameOver) {
		super(screen);
		highscore = screen.game.getUserData().getHighScore(screen.getGameMode());
		this.isGameOver = isGameOver;

		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);

		Table table = stage.createHeadUiLayoutTable();

		if (!isGameOver) {
			ImageTextButton btnResume = new ImageTextButton("Resume", screen.game.getSkin(), "resume");
			btnResume.getImageCell().padRight(6).padBottom(4);
			btnResume.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					resumeGame();
				}
			});
			table.add(btnResume);
		}

		table.add(new HorizontalSpacer());
		table.add(Utils.addFullScreenButtonToTable(Utils.addSoundAndMusicControllerToLayout(screen.game, Utils.createUIHeadLayout(screen.game))));

		if (!isGameOver) {
			table.add(new ScoreWidget(screen));
		}

		Table content = stage.createContentUiLayoutTable();
		content.add(new VerticalSpacer());
		content.row().expandX().center();

		TextButton btnMainMenu = new TextButton("Main Menu", screen.game.getSkin());
		btnMainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gotoMainMenu();

			}
		});
		content.add(btnMainMenu).right().bottom().padBottom(40).padRight(10);

		TextButton btnRestartGame = new TextButton("Retry", screen.game.getSkin());
		btnRestartGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				restartGame();
			}
		});
		btnRestartGame.setVisible(false);
		content.add(btnRestartGame).left().bottom().padBottom(40).padLeft(10).width(btnMainMenu.getWidth());

		stage.getRootTable().layout();

		btnMainMenu.setVisible(false);
		btnRestartGame.setVisible(false);
		float menuX = btnMainMenu.getX();
		float restartX = btnRestartGame.getX();
		btnMainMenu.addAction(Actions.sequence(
				Actions.delay(isGameOver ? TIME_TO_WAIT : 0),
				Actions.moveTo(menuX + 1000, btnMainMenu.getY()),
				Actions.visible(true),
				Actions.moveTo(menuX, btnMainMenu.getY(), 0.8F, Interpolation.swing)));
		btnRestartGame.addAction(Actions.sequence(
				Actions.delay(isGameOver ? TIME_TO_WAIT + 0.2F : 0.2F),
				Actions.moveTo(restartX + 500, btnRestartGame.getY()),
				Actions.visible(true),
				Actions.moveTo(restartX, btnRestartGame.getY(), 0.8F, Interpolation.swing)));

	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(new InputMultiplexer(stage.getInputProcessor(), this));
		if (isGameOver) {
			screen.game.getGameMusic().fadeOutToStop(0.6F);
		} else {
			screen.game.getGameMusic().fadeOutToPause();
		}
	}


	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if (Reference.SCREEN_TINT_STRENGTH > 0 && animationProgress > 0) {
			renderScreenTint(Utils.easeOut(animationProgress, isGameOver ? ANIM_TIME_GAME_OVER : ANIM_TIME_PAUSED, 2) * Reference.SCREEN_TINT_STRENGTH);
		}
	}

	@Override
	public void render(float delta) {
		//Game Paused
		screen.game.getRenderManager().begin();
		screen.game.getFont().getData().setScale(2, 4);
		if (isGameOver) {
			screen.game.getFont().setColor(1, 0, 0, 1);
			screen.getFontLayout().setText(screen.game.getFont(), "Game Over!");
		} else {
			screen.game.getFont().setColor(0, 0, 1, 1);
			screen.getFontLayout().setText(screen.game.getFont(), "Game Paused!");
		}

		//Slide in
		float gameOverAnim = isGameOver ? Math.max(0, screen.getWorld().getWorldHeight() / 2 - screen.getWorld().getWorldHeight() / 2 * Utils.easeOut(animationProgress, ANIM_TIME_GAME_OVER, 2)) : 0;
		float xPos = screen.getWorld().getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtGameOverHeight = screen.getFontLayout().height / 2;
		float yPos = ((2 * screen.getWorld().getWorldHeight()) / 3) + screen.getUiPos() + txtGameOverHeight + gameOverAnim;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Score
		screen.game.getFont().setColor(0, 1, 0, 1);
		screen.game.getFont().getData().setScale(1.2F);
		screen.getFontLayout().setText(screen.game.getFont(), "Your score: " + screen.getWorld().getPlayer().getScore());
		xPos = screen.getWorld().getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtScoreHeight = screen.getFontLayout().height / 2;
		yPos -= txtGameOverHeight + screen.game.getFont().getLineHeight() + txtScoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		//Highscore
		if (isGameOver && highscore < screen.getWorld().getPlayer().getScore()) {
			screen.game.getFont().setColor(0, 1, 0, 1);
			screen.game.getFont().getData().setScale(1.2F);
			screen.getFontLayout().setText(screen.game.getFont(), "New Highscore!");
		} else {
			screen.game.getFont().setColor(0, 0, 1, 1);
			screen.game.getFont().getData().setScale(1.0F);
			screen.getFontLayout().setText(screen.game.getFont(), "Highscore: " + highscore);
		}
		xPos = screen.getWorld().getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float txtHighscoreHeight = screen.getFontLayout().height / 2;
		yPos -= screen.game.getFont().getLineHeight() + txtHighscoreHeight;
		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);

		animationProgress += delta;

		super.render(delta);
	}

	private void restartMusic() {
		if (isGameOver) {
			screen.game.getGameMusic().setCurrentMusic(screen.getGameMusic());
		}
		screen.game.getGameMusic().play(true);
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
		if (!isGameOver || animationProgress > TIME_TO_WAIT) {
			switch (keycode) {
				case Input.Keys.SPACE:
					resumeGame();
					return true;
				case Input.Keys.ESCAPE:
					gotoMainMenu();
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

	private void resumeGame() {
		screen.setOverlay(new CountDownOverlay(screen));
		restartMusic();
	}

	/**
	 * display dialog asking to go to main menu
	 */
	private void gotoMainMenu() {
		if (isGameOver) {
			screen.showMainMenu();
		} else {
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

	private void restartGame() {
		if (isGameOver) {
			restartGameDo();
		} else {
			final PixelDialog dialog = new PixelDialog("Sure?", screen.game.getSkin());
			dialog.setMovable(false);
			dialog.setModal(true);
			dialog.setPrefWidth(stage.getStageViewport().getWorldWidth() * 0.7F);
			dialog.setWidth(stage.getStageViewport().getWorldWidth() * 0.7F);
			dialog.label("Restart game?");
			dialog.buttonYes(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					dialog.hide();
					restartGameDo();
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

	private void restartGameDo() {
		screen.resetToEmptyOverlay();
		screen.restart();
		restartMusic();
	}

	@Override
	protected boolean switchToPausedOverlayOnFocusChange() {
		return false;
	}
}
