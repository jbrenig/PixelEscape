package net.brenig.pixelescape.lib;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.screen.GameScreen;
import net.brenig.pixelescape.screen.ui.StageManager;
import net.brenig.pixelescape.screen.ui.TwoStateImageButton;

/**
 * Created by Jonas Brenig on 21.08.2015.
 */
public class Utils {

	/**
	 * Updates a table to the world bounds
	 * @param table table to resize
	 * @param world current world instance
	 * @param uiPos current uiPos
	 */
	public static void updateTableToGameBounds(Table table, World world, int uiPos) {
		table.setPosition(0, uiPos);
		table.setSize(world.getWorldWidth(), world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		table.invalidateHierarchy();
	}

	/**
	 * Helper function to update gui Elements (eg. onResize)
	 */
	public static void updateUIElementsToScreen(GameScreen screen, Stage stage, Table table, int width, int height) {
		screen.game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.getViewport().update(width, height, true);
		updateTableToGameBounds(table, screen.world, screen.uiPos);
	}

	/**
	 * creates an instance of Table to use for sound and music controls (unnecessary)
	 */
	public static Table createUIHeadLayout(PixelEscape game) {
		Table table =  new Table();
		Drawable ninePatch = Utils.minimizeNinePatch((NinePatchDrawable) game.skin.getDrawable("up"));
		table.setBackground(ninePatch);
		//minimze padding
		table.pad(4, 4, 4, 4);
		return table;
	}

	/**
	 * creates a new {@link Table} with two TwoStateButtons that are used to control sound and music
	 * @param game instance of the game
	 * @return the table they got added to
	 */
	public static Table addSoundAndMusicControllerToLayout(final PixelEscape game) {
		return addSoundAndMusicControllerToLayout(game, createUIHeadLayout(game));
	}

	/**
	 * adds two TwoStateButtons to a Table that are used to control sound and music
	 * @param game instance of the game
	 * @param layout the table they should get added to
	 * @return the table they got added to
	 */
	public static Table addSoundAndMusicControllerToLayout(final PixelEscape game, Table layout) {
		final TwoStateImageButton btnSound = new TwoStateImageButton(game.skin, "sound");
		btnSound.setState(game.gameSettings.soundEnabled);
		btnSound.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Invert current selection
				//btn checked --> no sound
				//btn not checked --> sound enabled
				game.gameSettings.soundEnabled = !btnSound.getState();
				btnSound.setState(game.gameSettings.soundEnabled);
			}
		});
		layout.add(btnSound);

		final TwoStateImageButton btnMusic = new TwoStateImageButton(game.skin, "music");
		btnMusic.setState(game.gameSettings.musicEnabled);
		btnMusic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Invert current selection
				//btn checked --> no music
				//btn not checked --> music enabled
				game.gameSettings.musicEnabled = !btnMusic.getState();
				btnMusic.setState(game.gameSettings.musicEnabled);
				game.updateMusicPlaying();
			}
		});
		layout.add(btnMusic);
		return layout;
	}

	/**
	 * NinePatchDrawables use their total size as minimum size by default
	 * This helper function resizes them to their minimum, so they can be resized to be smaller than their total size
	 * @param patch The ninepatch to minimize
	 * @return the given, minimized Ninepatch
	 */
	public static Drawable minimizeNinePatch(NinePatchDrawable patch) {
		patch.setMinHeight(patch.getPatch().getBottomHeight() + patch.getPatch().getTopHeight());
		patch.setMinWidth(patch.getPatch().getLeftWidth() + patch.getPatch().getRightWidth());
		return patch;
	}
}
