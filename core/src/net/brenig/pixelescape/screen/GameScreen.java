package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.render.overlay.CountDownOverlay;
import net.brenig.pixelescape.render.overlay.EmptyOverlay;
import net.brenig.pixelescape.render.overlay.GameOverOverlay;
import net.brenig.pixelescape.render.overlay.GamePausedOverlay;
import net.brenig.pixelescape.render.overlay.Overlay;


/**
 * Created by Jonas Brenig on 02.08.2015.
 */
public class GameScreen implements Screen {

	public final PixelEscape game;
	public final World world;
	public final WorldRenderer worldRenderer;
	/**
	 * position of the ui elements / black bars height
	 */
	public int uiPos = 0;
	/**
	 * Game-Font
	 */
	public GlyphLayout fontLayout = new GlyphLayout();

	private float lastScoreScreenWidth = 0;

	/**
	 * simulation paused
	 */
	private boolean isGamePaused = false;
	/**
	 * window paused (by os)
	 */
	private boolean isScreenPaused = false;
	private boolean renderingPaused = false;

	private boolean firstUpdate = true;

	//Game UI
	private final EmptyOverlay emptyOverlay;
	private final Stage stage;
	private final Table table;
	private final TextButton mainMenu;
	private final InputManager gameInput;
	private final InputMultiplexer inputManager;

	private Overlay overlay;

	public GameScreen(final PixelEscape game) {
		Gdx.app.log("PixelEscape | GameScreen", "initializing GameScreen...");
		//Game reference
		this.game = game;
		//init world and renderer
		this.world = new World(this);
		this.worldRenderer = new WorldRenderer(game, world);
		//create default overlay
		this.emptyOverlay = new EmptyOverlay(this);

		//init ui
		stage = new Stage(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));
		stage.setDebugAll(Reference.DEBUG_UI);

		table = new Table();
		table.setPosition(0, this.uiPos);
		table.setSize(this.world.getWorldWidth(), this.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		table.left().top();

		stage.addActor(table);

		game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		mainMenu = new TextButton("Pause", this.game.skin);
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setOverlay(new GamePausedOverlay(GameScreen.this));
			}
		});
		table.add(mainMenu).padTop(20).padLeft(10);

		//init input
		gameInput = new InputManager();
		inputManager = new InputMultiplexer(stage, gameInput);
		Gdx.input.setInputProcessor(inputManager);

		//set default overlay
		overlay = emptyOverlay;
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		//Cap max frame time to ensure proper simulation
		//Game will be slowed down if the frames don't get processed fast enough
		delta = Math.min(Reference.MAX_FRAME_TIME, delta);
		if (firstUpdate) {
			//Setup world on first update
			firstUpdate = false;
			setOverlay(new CountDownOverlay(this));
			world.generateWorld(true);
		}
		if (!isGamePaused()) {
			//update world
			world.update(delta);
		}
		if (renderingPaused) {
			//skip rendering if necessary
			return;
		}
		//render world
		worldRenderer.render(delta);

		//draw ui

		//black background
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(0, 0, 0, 1);
		game.shapeRenderer.rect(0, 0, world.getWorldWidth(), uiPos);
		game.shapeRenderer.rect(0, world.getWorldHeight() + uiPos, world.getWorldWidth(), uiPos + Reference.GAME_UI_Y_SIZE);
		game.shapeRenderer.end();

		//Draw Score Screen
		if(!overlay.shouldHideScore()) {
			drawScoreScreen(delta);
		}

		//UI
		if(!overlay.shouldHideGameUI()) {
			this.game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			stage.draw();
			stage.act(delta);
		}

		//Overlay
		overlay.render(delta);
	}

	private void drawScoreScreen(float delta) {
		String score = "Score: " + world.player.getXPos();
		game.font.getData().setScale(0.8F, 0.9F);
		game.font.setColor(0, 0, 0, 1);
		fontLayout.setText(game.font, score);
		if (fontLayout.width > lastScoreScreenWidth || lastScoreScreenWidth - fontLayout.width > Reference.GAME_UI_SCORE_SCREEN_SIZE_BUFFER) {
			lastScoreScreenWidth = fontLayout.width;
		}
		//Score Screen white background
		game.batch.begin();
		game.buttonNinePatch.draw(game.batch, world.getWorldWidth() - 20 - 16 - lastScoreScreenWidth, uiPos + world.getWorldHeight() + 20, 16 + lastScoreScreenWidth, 16 + fontLayout.height);
		game.batch.end();

		//Score
		game.batch.begin();
		game.font.setColor(0, 0, 0, 1);
		game.font.draw(game.batch, fontLayout, world.getWorldWidth() - 20 - 8 - lastScoreScreenWidth, uiPos + world.getWorldHeight() + 20 + 8 + fontLayout.height);
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		//update viewports and world size
		final int targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		uiPos = (int) Math.ceil((game.gameSizeY - targetHeight) / 2);
		world.resize(game.gameSizeX);
		worldRenderer.setPosition(0, uiPos);
		//Update UI
		Utils.updateUIElementsToScreen(this, stage, table, width, height);
		//update Overlay
		overlay.onResize(width, height);
	}

	@Override
	public void pause() {
		isScreenPaused = true;
		overlay.pause();
	}

	@Override
	public void resume() {
		isScreenPaused = false;
		overlay.resume();
	}

	public boolean isScreenPaused() {
		return isScreenPaused;
	}

	@Override
	public void hide() {
		reset();
		dispose();
	}

	@Override
	public void dispose() {
		overlay.dispose();
	}

	/**
	 * Removes all Overlays and resets to an EmptyOverlay
	 */
	public void resetToEmptyOverlay() {
		if (overlay != emptyOverlay) {
			setOverlay(emptyOverlay);
		}
	}

	public void resetInputManager() {
		Gdx.input.setInputProcessor(inputManager);
		gameInput.refreshButtonState();
		inputManager.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
	}

	public void setOverlay(Overlay o) {
		overlay.dispose();
		resetInputManager();
		overlay = o;
		overlay.show();
	}

	public void reset() {
		firstUpdate = true;
		resetToEmptyOverlay();
	}

	public void onGameOver() {
		setOverlay(new GameOverOverlay(this));
		if (game.gameSettings.soundEnabled) {
			game.gameOverSound.play();
		}
	}

	public void restart() {
		reset();
		world.restart();
	}

	public InputManager getInput() {
		return gameInput;
	}

	public boolean isGamePaused() {
		return isGamePaused || overlay.doesPauseGame() || isScreenPaused();
	}

	public void showGamePausedOverlay() {
		this.setOverlay(new GamePausedOverlay(this));
	}

	public void showMainMenu() {
		game.showMainMenu();
	}

	public boolean isFirstUpdate() {
		return firstUpdate;
	}
}
