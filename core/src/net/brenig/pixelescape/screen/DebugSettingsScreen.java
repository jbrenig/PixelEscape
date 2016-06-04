package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.StageManager;

/**
 * Screen to adjust DEBUG features
 */
public class DebugSettingsScreen extends PixelScreen {

	private final StageManager uiStage;
	private final Table uiLayout;
	private final Table headLayout;
	private final ScrollPane pane;


	public DebugSettingsScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(game.getRenderManager());

		game.getRenderManager().resetFontSize();

		//configure main layout
		uiLayout = new Table();
		uiLayout.center();
		uiLayout.padBottom(30).padTop(30);

		uiLayout.add(createDebugSettingCheckBox("Show FPS", "SHOW_FPS")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Pause when window looses focus", "AUTO_PAUSE")).left().row();
		uiLayout.add(createDebugSettingCheckBox("show debug information", "DEBUG_MODE_COORDS")).left().row();
		uiLayout.add(createDebugSettingCheckBox("validate world gen", "DEBUG_WORLD_GEN_VALIDATE")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Debug screen bounds", "DEBUG_SCREEN_BOUNDS")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Debug UI", "DEBUG_UI")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Enable debug Logging", "DEBUG_LOGGING")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Enable Godmode", "DEBUG_GOD_MODE")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Show music Debug information", "DEBUG_MUSIC")).left().row();
		uiLayout.add(createDebugSettingCheckBox("Enable Screen-Shake", "SCREEN_SHAKE")).left().row();


		//Add ui elements to stage

		//Head controls
		uiStage.getRootTable().top().right().pad(4);
		headLayout = Utils.createDefaultUIHeadControls();

		Label header = new Label("DEBUG Settings", game.getSkin());
		header.setFontScale(1.2F);

		uiStage.add(new HorizontalSpacer()).width(headLayout.getPrefWidth());
		uiStage.add(header).pad(8).fillX();
		uiStage.add(headLayout);
		uiStage.row();


		//Main Layout

		//configure scollpane
		pane = new ScrollPane(uiLayout, game.getSkin());
//		pane.setFillParent(true);
//		uiStage.addActorToStage(pane);
		uiStage.add(pane).expand().fillX().padTop(8).padLeft(20).padRight(20).center().colspan(3).row();
		uiStage.getUiStage().setScrollFocus(pane);

		//Back Button
		{
			TextButton btnBack = new TextButton("Go Back", game.getSkin());
			btnBack.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.gameDebugSettings.saveToDisk();
					game.setScreen(new SettingsScreen(game));
				}
			});

			uiStage.add(btnBack).colspan(3).padTop(8);
		}
	}

	private CheckBox createDebugSettingCheckBox(String text, final String property) {
		CheckBox chbx = new CheckBox(text, game.getSkin());
		chbx.setChecked(game.gameDebugSettings.getBoolean(property));
		chbx.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.gameDebugSettings.setBoolean(property, ((CheckBox) actor).isChecked());
			}
		});
		chbx.getImageCell().padBottom(8).padRight(10).size(32);
		chbx.getLabel().setFontScale(0.7F);
		return chbx;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage.getInputProcessor());
		game.getRenderManager().resetFontSize();
		uiStage.updateViewportToScreen();
//		uiLayout.invalidateHierarchy();
		pane.invalidateHierarchy();
		headLayout.invalidateHierarchy();
	}

	@Override
	public void render(float delta) {
		uiStage.act(delta);
		uiStage.draw(game.getRenderManager());
	}

	@Override
	public void resize(int width, int height) {
		uiStage.updateViewport(width, height, true);
		uiLayout.invalidateHierarchy();
		pane.invalidateHierarchy();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		uiStage.dispose();
	}
}
