package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.game.player.effects.StatusEffect;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.LogHelperKt;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.render.overlay.*;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.render.ui.ingame.AbilityWidget;
import net.brenig.pixelescape.render.ui.ingame.ScoreWidget;
import net.brenig.pixelescape.render.ui.ingame.StageManagerGame;

import javax.annotation.Nullable;
import java.util.Collection;


/**
 * Main Game Screen<br></br>
 * Displays the game. Provides overlays for GamePaused, GameOver etc.
 */
public class GameScreen extends PixelScreen {

	/**
	 * current gamemode
	 */
	private final GameMode gameMode;

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

	private boolean initialized = true;

	private volatile boolean valid = false;

	//Game UI
	private final EmptyOverlay emptyOverlay;
	private final StageManagerGame stage;
	private final InputManager inputManager;
	private final InputMultiplexer inputMultiplexer;

	private Overlay overlay;

	public GameScreen(final PixelEscape game, GameMode gameMode) {
		super(game);
		LogHelperKt.log("GameScreen", "Initializing game. GameMode: " + gameMode.getGameModeName());

		this.gameMode = gameMode;
		//init world and renderer
		this.world = new World(this, game.getGameSizeX());
		this.worldRenderer = new WorldRenderer(game, world);
		//create default overlay
		this.emptyOverlay = new EmptyOverlay(this);

		//init ui
		stage = new StageManagerGame(this);

		Table table = stage.createHeadUiLayoutTable();

		game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		ImageTextButton buttonPause = new ImageTextButton("Pause", this.game.getSkin(), StyleNames.BUTTON_PAUSE);
		buttonPause.getImageCell().padRight(6).padBottom(4);
		buttonPause.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setOverlay(new GamePausedOverlay(GameScreen.this, false));
			}
		});
		table.add(buttonPause);
		table.add(new HorizontalSpacer());
		table.add(new ScoreWidget(world.getPlayer(), fontLayout, game));
		if (gameMode.abilitiesEnabled()) {
			stage.row();
			stage.add(new VerticalSpacer());
			stage.row();
			stage.add(new AbilityWidget(game.getSkin(), world.getPlayer(), this)).bottom().right().size(128F).pad(0, 0, 32, uiPos + 32);
		}

		//init input
		inputManager = new InputManager();
		inputMultiplexer = new InputMultiplexer(stage.getInputProcessor(), inputManager);

		//set default overlay
		overlay = emptyOverlay;

		LogHelperKt.log("GameScreen", "Game initialized.");

	}

	@Override
	public void show() {
		initialized = false;
		valid = true;
		game.getGameMusic().playOrFadeInto(getGameMusic());
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Nullable
	public Music getGameMusic() {
		return game.getGameAssets().getRandomGameMusic(PixelEscape.Companion.getRand());
	}

	@Override
	public void render(float delta) {
		//Cap max frame time to ensure proper simulation
		//Game will be slowed down if the frames don't get processed fast enough
		delta = Math.min(Reference.MAX_FRAME_TIME, delta);

		if (game.getGameConfig().getCanHideCursor())
			inputManager.updateMouseVisibility(delta, game.getGameSettings().getFullscreen() && overlay.canHideCursor());

		if (!initialized) {
			init();
		}

		if (!isGamePaused()) {
			//update world
			world.update(delta);
		}

		//Pause on escape
		if (inputManager.isEscapeDown()) {
			if (overlay.shouldPauseOnEscape()) {
				showGamePausedOverlay();
			}
		}

		//black background
		game.getRenderManager().disableBlending();
		renderUIBackground();
		game.getRenderManager().enableBlending();

		//render world
		worldRenderer.render(delta);

		//draw ui

		//Overlay first callback
		overlay.renderFirst(delta);

		//UI
		if (!overlay.shouldHideGameUI()) {

			//Draw lives
			renderLives();
			renderEffectTime();

			this.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			stage.draw(game.getRenderManager());
			stage.act(delta);
		}

		//Overlay
		overlay.render(delta);

		renderDebugInformation();
	}

	private void renderUIBackground() {
		game.getRenderManager().begin();

		if (GameDebugSettings.Companion.get("DEBUG_SCREEN_BOUNDS")) {
			game.getRenderManager().setColor(1, 0, 0, 0);
		} else {
			game.getRenderManager().setColor(0, 0, 0, 1);
		}
		game.getRenderManager().rect(0, 0, world.getWorldWidth(), uiPos);
		game.getRenderManager().rect(0, world.getWorldHeight() + uiPos, world.getWorldWidth(), uiPos + Reference.GAME_UI_Y_SIZE);
	}

	private void renderEffectTime() {
		Collection<StatusEffect> effects = world.player.getStatusEffects();
		if (!effects.isEmpty()) {
			game.getRenderManager().begin();
			int index = 0;
			final int yPos = world.getWorldHeight() + uiPos - 6;
			final int xPos = 10;
			final int xSize = 16;
			final int ySize = 32;
			for (StatusEffect effect : effects) {
				final float timeRemaining = effect.getScaledTime();
				if (timeRemaining > 0) {
					final float timeRemainingScaled = timeRemaining * ySize;
					effect.updateRenderColor(game.getRenderManager());
					game.getRenderManager().rect(xPos + index * xSize, yPos - timeRemainingScaled, xSize, timeRemainingScaled);
					index++;
				}
			}
		}
	}

	private void renderLives() {
		if (gameMode.getExtraLives() > 0) {
			game.getRenderManager().begin();
			for (int index = 1; index <= world.getPlayer().getExtraLives() + 1; index++) {
				game.getRenderManager().getBatch().draw(game.getGameAssets().getHeart(), game.getGameSizeX() - 36 * index, uiPos + world.getWorldHeight() - 28);
			}
		}
	}

	/**
	 * Setup world on first update
	 */
	private void init() {
		initialized = true;
		if (game.getUserData().tutorialSeen(gameMode)) {
			setOverlay(new CountDownOverlay(this));
		} else {
			setOverlay(new TutorialOverlay(this));
		}
		world.generateWorld(true);
		world.spawnEntities();
	}

	private void renderDebugInformation() {
		if (GameDebugSettings.Companion.get("DEBUG_MODE_COORDS")) {
			float x = game.getScaledMouseX();
			float y = game.getScaledMouseY();
			float worldY = world.convertMouseYToWorldCoordinate(y);
			String screenTxt = "Screen: X: " + (int) x + ", Y: " + (int) world.convertMouseYToScreenCoordinate(y) + "(" + (int) y + " / raw: " + Gdx.input.getY() + "), Player speed: " + (int) world.getPlayer().getXVelocity();
			String worldTxt = "World: X: " + (int) world.convertScreenToWorldCoordinate(x) + ", Y: " + (int) worldY + ", Block: " + world.convertScreenCoordToWorldBlockIndex(x) + " (" + world.convertWorldBlockToLocalBlockIndex(world.convertScreenCoordToWorldBlockIndex(x)) + ")";
			TerrainPair terrain = world.getTerrainPairForIndex(world.convertScreenCoordToLocalBlockIndex(x));
			boolean isTerrain = terrain.getBot() * Reference.BLOCK_WIDTH >= worldY
					|| world.getWorldHeight() - (terrain.getTop() * Reference.BLOCK_WIDTH) <= worldY;
			String blockInfoTxt = "Info: IsTerrain: " + isTerrain + ", BlocksGenerated: " + world.getTerrainBufferWorldIndex();

			//Begin draw
			game.getRenderManager().begin();
			game.getFont().setColor(Color.LIGHT_GRAY);
			game.getFont().getData().setScale(0.5F);
			//Draw
			fontLayout.setText(game.getFont(), screenTxt);
			float pos = 5 + fontLayout.height;
			game.getFont().draw(game.getBatch(), fontLayout, 5, pos);

			pos += fontLayout.height + 5;
			fontLayout.setText(game.getFont(), worldTxt);
			game.getFont().draw(game.getBatch(), fontLayout, 5, pos);

			pos += fontLayout.height + 5;
			fontLayout.setText(game.getFont(), blockInfoTxt);
			game.getFont().draw(game.getBatch(), fontLayout, 5, pos);
			//End draw
			game.getFont().getData().setScale(1F);
		} else if (GameDebugSettings.Companion.get("DEBUG_MUSIC")) {
			game.getRenderManager().begin();
			game.getFont().setColor(Color.LIGHT_GRAY);
			game.getFont().getData().setScale(0.5F);
			fontLayout.setText(game.getFont(), "Music state: " + game.getGameMusic().getState());
			float pos = 5 + fontLayout.height;
			game.getFont().draw(game.getBatch(), fontLayout, 5, pos);
			game.getFont().getData().setScale(1F);
		}
	}

	@Override
	public void resize(int width, int height) {
		//update viewports and world size
		final int targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		uiPos = (int) Math.ceil((game.getGameSizeY() - targetHeight) / 2);

		world.resize(game.getGameSizeX());
		worldRenderer.setWorldRendererYOffset(uiPos);
		worldRenderer.onResize();
		//Update UI
		stage.updateStageToGameBounds(width, height);
		//update Overlay
		overlay.onResize(width, height);
	}

	@Override
	public void pause() {
		if (GameDebugSettings.Companion.get("AUTO_PAUSE")) {
			isScreenPaused = true;
			overlay.pause();
		}
	}

	@Override
	public void resume() {
		if (GameDebugSettings.Companion.get("AUTO_PAUSE")) {
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
		valid = false;
		inputManager.resetMouseVisibility();

		overlay.dispose();
	}

	public void setOverlayInputProcessor(@Nullable InputProcessor processor) {
		if (valid) {
			Gdx.input.setInputProcessor(processor);
		}
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
	 *
	 * @param o {@link Overlay} to show
	 */
	public void setOverlay(Overlay o) {
		overlay.dispose();
		resetInputManager();
		overlay = o;
		overlay.show();
		if (!overlay.canHideCursor()) {
			inputManager.resetMouseVisibility();
		}
	}

	/**
	 * Callback when player died
	 * Shows GameOver Overlay and registers highscore
	 */
	public void onGameOver() {
		setOverlay(new GamePausedOverlay(this, true));
		game.getUserData().updateHighscore(gameMode, world.getPlayer().getScore());
	}

	/**
	 * restarts the game
	 */
	public void restart() {
		initialized = false;
		resetToEmptyOverlay();
		world.restart();
	}

	public InputManager getInput() {
		return inputManager;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isGamePaused() {
		return overlay.doesPauseGame() || isScreenPaused();
	}

	public void showGamePausedOverlay() {
		this.setOverlay(new GamePausedOverlay(this, false));
	}

	public void showMainMenu() {
		game.showMainMenu();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resetInputManager() {
		if (valid) {
			Gdx.input.setInputProcessor(inputMultiplexer);
			inputManager.refreshButtonState();
			stage.getInputProcessor().mouseMoved(Gdx.input.getX(), Gdx.input.getY());
		}
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

	public GameMode getGameMode() {
		return gameMode;
	}
}
