package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.render.overlay.CountDownOverlay;
import net.brenig.pixelescape.render.overlay.EmptyOverlay;
import net.brenig.pixelescape.render.overlay.GameOverOverlay;
import net.brenig.pixelescape.render.overlay.GamePausedOverlay;
import net.brenig.pixelescape.render.overlay.Overlay;
import net.brenig.pixelescape.screen.ui.HorizontalSpacer;
import net.brenig.pixelescape.screen.ui.ScoreWidget;
import net.brenig.pixelescape.screen.ui.StageManagerGame;


/**
 * Main Game Screen<br></br>
 * Displays the game. Provides overlays for GamePaused, GameOver etc.
 */
public class GameScreen extends PixelScreen {

	/**
	 * The game world
	 */
	public final World world;

	/**
	 * Game world renderer
	 */
	public final WorldRenderer worldRenderer;


	private int uiPos = 0;
	private final GlyphLayout fontLayout = new GlyphLayout();

	/**
	 * window paused (by os)
	 */
	private boolean isScreenPaused = false;

	private boolean firstUpdate = true;

	//Game UI
	private final EmptyOverlay emptyOverlay;
	private final StageManagerGame stage;
	private final InputManager inputManager;
	private final InputMultiplexer inputMultiplexer;


	private Overlay overlay;

	public GameScreen(final PixelEscape game) {
		super(game);
		Gdx.app.log("PixelEscape | GameScreen", "initializing GameScreen...");
		//init world and renderer
		this.world = new World(this, game.gameSizeX);
		this.worldRenderer = new WorldRenderer(game, world);
		//create default overlay
		this.emptyOverlay = new EmptyOverlay(this);

		//init ui
		stage = new StageManagerGame(this);

		Table table = stage.createHeadUiLayoutTable();

		game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		TextButton mainMenu = new TextButton("Pause", this.game.getSkin());
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setOverlay(new GamePausedOverlay(GameScreen.this));
			}
		});
		table.add(mainMenu);
		table.add(new HorizontalSpacer());
		table.add(new ScoreWidget(world.getPlayer(), fontLayout, game));

		//init input
		inputManager = new InputManager();
		inputMultiplexer = new InputMultiplexer(stage.getInputProcessor(), inputManager);
		Gdx.input.setInputProcessor(inputMultiplexer);

		//set default overlay
		overlay = emptyOverlay;
	}

	@Override
	public void show() {
		firstUpdate = true;
		game.gameMusic.playOrFadeInto(getGameMusic());
	}

	public Music getGameMusic() {
		return game.getGameAssets().getRandomGameMusic(PixelEscape.rand);
	}

	@Override
	public void render(float delta) {
		//Cap max frame time to ensure proper simulation
		//Game will be slowed down if the frames don't get processed fast enough
		delta = Math.min(Reference.MAX_FRAME_TIME, delta);

		if(game.gameConfig.canHideCursor()) inputManager.updateMouseVisibility(delta, game.gameSettings.fullscreen && overlay.canHideCursor());

		if (firstUpdate) {
			init();
		}

		if (!isGamePaused()) {
			//update world
			world.update(delta);
		}

		//Pause on escape
		if(inputManager.isEscapeDown()) {
			if(overlay.shouldPauseOnEscape()) {
				showGamePausedOverlay();
			}
		}

		//render world
		worldRenderer.render(delta);

		//draw ui

		//black background
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		if(GameDebugSettings.get("DEBUG_SCREEN_BOUNDS")) {
			game.shapeRenderer.setColor(1, 0, 0, 0);
		} else {
			game.shapeRenderer.setColor(0, 0, 0, 1);
		}
		game.shapeRenderer.rect(0, 0, world.getWorldWidth(), uiPos);
		game.shapeRenderer.rect(0, world.getWorldHeight() + uiPos, world.getWorldWidth(), uiPos + Reference.GAME_UI_Y_SIZE);

		game.shapeRenderer.end();

		//Overlay first callback
		overlay.renderFirst(delta);

		//UI
		if(!overlay.shouldHideGameUI()) {
			this.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			stage.draw();
			stage.act(delta);
		}

		//Overlay
		overlay.render(delta);

		renderDebugInformation();
	}

	/**
	 * Setup world on first update
	 */
	private void init() {
		firstUpdate = false;
		setOverlay(new CountDownOverlay(this));
		world.generateWorld(true);
	}

	private void renderDebugInformation() {
		if(GameDebugSettings.get("DEBUG_MODE_COORDS")) {
			float x = game.getScaledMouseX();
			float y = game.getScaledMouseY();
			float worldY = world.convertMouseYToWorldCoordinate(y);
			String screenTxt = "Screen: X: " + (int) x + ", Y: " + (int) world.convertMouseYToScreenCoordinate(y) + "(" + (int) y + "), Player speed: " + (int) world.getPlayer().getVelocity();
			String worldTxt = "World: X: " + (int) world.convertScreenToWorldCoordinate(x) + ", Y: " + (int) worldY + ", Block: " + world.convertScreenCoordToWorldBlockIndex(x) + " (" + world.convertWorldBlockToLocalBlockIndex(world.convertScreenCoordToWorldBlockIndex(x)) + ")";
			TerrainPair terrain = world.getBlockForScreenPosition(x);
			boolean isTerrain = world.getWorldHeight() - terrain.getBot() * Reference.BLOCK_WIDTH < worldY
					|| terrain.getTop() * Reference.BLOCK_WIDTH >= worldY;
			String blockInfoTxt = "Info: IsTerrain: " + isTerrain + ", BlocksGenerated: " + world.getBlocksGenerated();

			//Begin draw
			game.batch.begin();
			game.getFont().setColor(Color.LIGHT_GRAY);
			game.getFont().getData().setScale(0.5F);
			//Draw
			fontLayout.setText(game.getFont(), screenTxt);
			float pos = 5 + fontLayout.height;
			game.getFont().draw(game.batch, fontLayout, 5, pos);

			pos += fontLayout.height + 5;
			fontLayout.setText(game.getFont(), worldTxt);
			game.getFont().draw(game.batch, fontLayout, 5, pos);

			pos += fontLayout.height + 5;
			fontLayout.setText(game.getFont(), blockInfoTxt);
			game.getFont().draw(game.batch, fontLayout, 5, pos);
			//End draw
			game.getFont().getData().setScale(1F);
			game.batch.end();
		} else if(GameDebugSettings.get("DEBUG_MUSIC")) {
			game.batch.begin();
			game.getFont().setColor(Color.LIGHT_GRAY);
			game.getFont().getData().setScale(0.5F);
			fontLayout.setText(game.getFont(), "Music state: " + game.gameMusic.getState());
			float pos = 5 + fontLayout.height;
			game.getFont().draw(game.batch, fontLayout, 5, pos);
			game.getFont().getData().setScale(1F);
			game.batch.end();
		}
	}

	@Override
	public void resize(int width, int height) {
		//update viewports and world size
		final int targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		uiPos = (int) Math.ceil((game.gameSizeY - targetHeight) / 2);
		world.resize(game.gameSizeX);
		worldRenderer.setPosition(0, uiPos);
		//Update UI
		stage.updateStageToGameBounds(width, height);
		//update Overlay
		overlay.onResize(width, height);
	}

	@Override
	public void pause() {
		if(GameDebugSettings.get("AUTO_PAUSE")) {
			isScreenPaused = true;
			overlay.pause();
		}
	}

	@Override
	public void resume() {
		if(GameDebugSettings.get("AUTO_PAUSE")) {
			isScreenPaused = false;
			overlay.resume();
		}
	}

	public boolean isScreenPaused() {
		return isScreenPaused;
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		overlay.dispose();
		inputManager.resetMouseVisibility();
	}

	/**
	 * Removes all Overlays and resets to an EmptyOverlay
	 */
	public void resetToEmptyOverlay() {
		if (overlay != emptyOverlay) {
			setOverlay(emptyOverlay);
		}
	}

	@Override
	public void updateMusic(boolean play) {
		overlay.updateMusic(play);
	}

	/**
	 * Sets a new overlay as active<br></br>
	 * disposes the old overlay and resets InputProcessors
	 * @param o {@link Overlay} to show
	 */
	public void setOverlay(Overlay o) {
		overlay.dispose();
		resetInputManager();
		overlay = o;
		overlay.show();
		if(!overlay.canHideCursor()) {
			inputManager.resetMouseVisibility();
		}
	}

	/**
	 * Callback when player died
	 * Shows GameOver Overlay and registers highscore
	 */
	public void onGameOver() {
		setOverlay(new GameOverOverlay(this));
		if (game.gameSettings.isSoundEnabled()) {
			game.getGameOverSound().play(game.gameSettings.getSoundVolume());
		}
		game.userData.updateHighscore(world.getPlayer().getScore());
	}

	/**
	 * restarts the game
	 */
	public void restart() {
		firstUpdate = true;
		resetToEmptyOverlay();
		world.restart();
	}

	public InputManager getInput() {
		return inputManager;
	}

	public boolean isGamePaused() {
		return overlay.doesPauseGame() || isScreenPaused();
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

	public void resetInputManager() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputManager.refreshButtonState();
		stage.getInputProcessor().mouseMoved(Gdx.input.getX(), Gdx.input.getY());
	}

	@SuppressWarnings("SameReturnValue")
	public float getUiSize() {
		return Reference.GAME_UI_Y_SIZE;
	}

	@SuppressWarnings("SameReturnValue")
	public float getUiPadding() {
		return Reference.GAME_UI_Y_PADDING;
	}

	/**
	 * position of the ui elements / black bars height
	 */
	public int getUiPos() {
		return uiPos;
	}

	/**
	 * Game-Font
	 */
	public GlyphLayout getFontLayout() {
		return fontLayout;
	}
}
