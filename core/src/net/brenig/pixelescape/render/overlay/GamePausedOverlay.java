package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Created by Jonas Brenig on 07.09.2015.
 */
public class GamePausedOverlay extends Overlay implements InputProcessor {
	private Stage stage;
	private Table table;
	private TextButton mainMenu;

	public GamePausedOverlay(final GameScreen screen) {
		super(screen);
		stage = new Stage(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, screen.game.cam));
		stage.setDebugAll(Reference.DEBUG_UI);

		table = new Table();
		table.setPosition(0, screen.uiPos);
		table.setSize(screen.world.getWorldWidth(), screen.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		table.left().top();

		stage.addActor(table);

		screen.game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		mainMenu = new TextButton("Main Menu", screen.game.skin);
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				screen.showMainMenu();
			}
		});
		table.add(mainMenu).padTop(20).padLeft(10);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
	}

	@Override
	public void render(float delta) {
		//Game Over
		screen.game.batch.begin();
		screen.game.font.setColor(0, 0, 1, 1);
		screen.game.font.getData().setScale(2, 4);
		screen.fontLayout.setText(screen.game.font, "Game Paused!");
		float xPos = screen.world.getWorldWidth() / 2 - screen.fontLayout.width / 2;
		float txtGameOverHeight = screen.fontLayout.height / 2;
		float yPos = ((2 * screen.world.getWorldHeight()) / 3) + txtGameOverHeight + screen.uiPos;
		screen.game.font.draw(screen.game.batch, screen.fontLayout, xPos, yPos);
		screen.game.batch.end();

		//Score
		screen.game.batch.begin();
		screen.game.font.setColor(0, 1, 0, 1);
		screen.game.font.getData().setScale(1.2F);
		screen.fontLayout.setText(screen.game.font, "Your score: " + screen.world.player.getXPos());
		xPos = screen.world.getWorldWidth() / 2 - screen.fontLayout.width / 2;
		float txtScoreHeight = screen.fontLayout.height / 2;
		yPos -= txtGameOverHeight + screen.game.font.getLineHeight() + txtScoreHeight;
		screen.game.font.draw(screen.game.batch, screen.fontLayout, xPos, yPos);
		screen.game.batch.end();

		//Info
		screen.game.batch.begin();
		screen.game.font.setColor(0, 1, 0, 1);
		screen.game.font.getData().setScale(0.8F);
		screen.fontLayout.setText(screen.game.font, "Tap to continue!");
		xPos = screen.world.getWorldWidth() / 2 - screen.fontLayout.width / 2;
		yPos -= txtGameOverHeight + screen.game.font.getLineHeight() + txtScoreHeight + screen.fontLayout.height / 2;
		screen.game.font.draw(screen.game.batch, screen.fontLayout, xPos, yPos);
		screen.game.batch.end();


		screen.game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.draw();
		stage.act(delta);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void onResize(int width, int height) {
		Utils.updateUIElementsToScreen(screen, stage, table, width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
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
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.SPACE) {
			screen.setOverlay(new CountDownOverlay(screen));
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
		screen.setOverlay(new CountDownOverlay(screen));
		return true;
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
