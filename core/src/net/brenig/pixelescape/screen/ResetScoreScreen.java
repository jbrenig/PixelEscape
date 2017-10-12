package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.PixelDialog;
import net.brenig.pixelescape.render.ui.general.StageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Screen used to reset scores of different gamemodes
 */
public class ResetScoreScreen extends PixelScreen {

	private final StageManager uiStage;
	private final Table headLayout;
	private final Table contentLayout;
	private final ScrollPane pane;

	private final Map<CheckBox, GameMode> gamemodeCheckboxes;
	private final CheckBox resetAllCheckBox;


	public ResetScoreScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(game.getRenderManager());

		game.getRenderManager().resetFontSize();

		//configure main layout
		Table uiLayout = new Table();
		uiLayout.setFillParent(true);
		uiLayout.setPosition(0, 0);
		uiLayout.center();
		uiLayout.padTop(30).padBottom(20);

		contentLayout = new Table();
		contentLayout.center();
		contentLayout.padBottom(30).padTop(30);

		//Header
		Label header = new Label("Reset Score:", game.getSkin());
		header.setFontScale(1.2F);
		uiLayout.add(header);
		uiLayout.row();

		//Head controls
		uiStage.getRootTable().top().right().pad(4);
		headLayout = Utils.createDefaultUIHeadControls();
		uiStage.add(headLayout);

		//content (scrollpane)
		gamemodeCheckboxes = new HashMap<>(game.getGameConfig().getAvailableGameModes().size());

		final ChangeListener chbxListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (CheckBox chbx : gamemodeCheckboxes.keySet()) {
					if (!chbx.isChecked()) {
						resetAllCheckBox.setChecked(false);
						return;
					}
				}
				resetAllCheckBox.setChecked(true);
			}
		};


		resetAllCheckBox = new CheckBox("Reset ALL", game.getSkin());
		resetAllCheckBox.setChecked(false);
		resetAllCheckBox.getImageCell().padBottom(8).padRight(10).size(32);
		resetAllCheckBox.getLabel().setFontScale(0.7F);
		resetAllCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (CheckBox chbx : gamemodeCheckboxes.keySet()) {
					chbx.setChecked(resetAllCheckBox.isChecked());
				}
			}
		});
		//suppress events caused by setChecked()
		resetAllCheckBox.setProgrammaticChangeEvents(false);
		contentLayout.add(resetAllCheckBox).padBottom(20).left().row();

		for (GameMode mode : game.getGameConfig().getAvailableGameModes()) {
			CheckBox chbx = new CheckBox(mode.getGameModeName(), game.getSkin());
			chbx.setChecked(false);
			chbx.getImageCell().padBottom(8).padRight(10).size(32);
			chbx.getLabel().setFontScale(0.7F);
			chbx.addListener(chbxListener);
			//suppress events caused by setChecked()
			chbx.setProgrammaticChangeEvents(false);
			gamemodeCheckboxes.put(chbx, mode);
			contentLayout.add(chbx).left();
			Label lbl = new Label("" + game.getUserData().getHighScore(mode), game.getSkin());
			lbl.setFontScale(0.7F);
			contentLayout.add(lbl);
			contentLayout.row();
		}

		//configure scollpane
		pane = new ScrollPane(contentLayout, game.getSkin());
		uiLayout.add(pane).expand().fillX().padTop(8).padLeft(20).padRight(20).center().row();
		//set scroll focus
		uiStage.getUiStage().setScrollFocus(pane);

		Table buttonLayout = new Table();

		//Back Button
		{
			TextButton btnBack = new TextButton("Go Back", game.getSkin());
			btnBack.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new SettingsScreen(game));
				}
			});

			buttonLayout.add(btnBack).padTop(8).padLeft(20);
		}
		buttonLayout.add(new HorizontalSpacer());
		//OK Button
		{
			TextButton btnFinish = new TextButton("Finish", game.getSkin());
			btnFinish.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					final PixelDialog d = new PixelDialog("Reset Scores", game.getSkin());
					d.label("Are you sure to reset scores?");
					d.buttonYes(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							apply();
							game.setScreen(new SettingsScreen(game));
							d.hide();
						}
					});
					d.buttonNo(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							d.hide();
						}
					});
					d.init();
					d.show(uiStage.getUiStage());
				}
			});

			buttonLayout.add(btnFinish).padTop(8).padRight(20);
		}
		uiLayout.add(buttonLayout);

		//Add main ui
		uiStage.addActorToStage(uiLayout);
	}

	private void apply() {
		for (Map.Entry<CheckBox, GameMode> entry : gamemodeCheckboxes.entrySet()) {
			if (entry.getKey().isChecked()) {
				game.getUserData().setHighScore(entry.getValue(), 0);
				game.getUserData().setTutorialSeen(entry.getValue(), false);
			}
		}
	}


	@SuppressWarnings("Duplicates")
	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage.getInputProcessor());
		game.getRenderManager().resetFontSize();
		uiStage.updateViewportToScreen();
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
		contentLayout.invalidateHierarchy();
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
