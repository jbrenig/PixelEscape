package net.brenig.pixelescape.lib;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.render.ui.general.TwoStateImageButton;

/**
 * general utilities
 */
public class Utils {

	/**
	 * creates an instance of Table to use for sound and music controls (unnecessary)
	 */
	public static Table createUIHeadLayout(PixelEscape game) {
		Table table = new Table();
		Drawable ninePatch = Utils.minimizeNinePatch((NinePatchDrawable) game.getSkin().getDrawable("up"));
		table.setBackground(ninePatch);
		//minimize padding
		table.pad(8, 8, 8, 8);
		table.defaults().size(getButtonSize());
		if (game.getGameConfig().useBiggerButtons()) {
			table.defaults().pad(2, 1, 2, 1);
			table.defaults().expand().fillY();
			table.setHeight(Reference.GAME_UI_Y_SIZE);
		}
		return table;
	}

	public static Table createDefaultUIHeadControls() {
		PixelEscape game = PixelEscape.getPixelEscape();
		return Utils.addFullScreenButtonToTable(game, Utils.addSoundAndMusicControllerToLayout(game, createUIHeadLayout(game)));
	}

	/**
	 * creates a new {@link Table} with two TwoStateButtons that are used to control sound and music
	 *
	 * @param game instance of the game
	 * @return the table they got added to
	 */
	public static Table addSoundAndMusicControllerToLayout(final PixelEscape game) {
		return addSoundAndMusicControllerToLayout(game, createUIHeadLayout(game));
	}

	/**
	 * adds two TwoStateButtons to a Table that are used to control sound and music
	 *
	 * @param game   instance of the game
	 * @param layout the table they should get added to
	 * @return the table they got added to
	 */
	public static Table addSoundAndMusicControllerToLayout(final PixelEscape game, Table layout) {
		final TwoStateImageButton btnSound = new TwoStateImageButton(game.getSkin(), "sound");
		btnSound.setState(!game.getGameSettings().isSoundEnabled());
		btnSound.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Invert current selection
				//btn checked --> no sound
				//btn not checked --> sound enabled
				game.getGameSettings().setSoundEnabled(btnSound.getState());
				btnSound.setState(!game.getGameSettings().isSoundEnabled());
			}
		});
		layout.add(btnSound);

		if(Reference.ENABLE_MUSIC) {
			final TwoStateImageButton btnMusic = new TwoStateImageButton(game.getSkin(), "music");
			btnMusic.setState(!game.getGameSettings().isMusicEnabled());
			btnMusic.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					//Invert current selection
					//btn checked --> no music
					//btn not checked --> music enabled
					game.getGameSettings().setMusicEnabled(btnMusic.getState());
					btnMusic.setState(!game.getGameSettings().isMusicEnabled());
					game.updateMusicPlaying();
				}
			});
			layout.add(btnMusic);
		}
		return layout;
	}

	/**
	 * adds one TwoStateButtons to a Table that is used to go to fullscreen<br></br>
	 * gets skipped if fullscreen is not supported
	 *
	 * @param layout the table they should get added to
	 * @return the table they got added to
	 */
	public static Table addFullScreenButtonToTable(Table layout) {
		return addFullScreenButtonToTable(PixelEscape.getPixelEscape(), layout);
	}

	/**
	 * adds one TwoStateButtons to a Table that is used to go to fullscreen<br></br>
	 * gets skipped if fullscreen is not supported
	 *
	 * @param game   instance of the game
	 * @param layout the table they should get added to
	 * @return the table they got added to
	 */
	public static Table addFullScreenButtonToTable(final PixelEscape game, Table layout) {
		if (game.getGameConfig().canGoFullScreen()) {
			final TwoStateImageButton btnFullScreen = new TwoStateImageButton(game.getSkin(), "fullscreen");
			btnFullScreen.setState(game.getGameSettings().fullscreen);
			btnFullScreen.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					//Invert current selection
					//btn checked --> fullscreen
					//btn not checked --> no fullscreen
					game.getGameSettings().fullscreen = !btnFullScreen.getState();
					btnFullScreen.setState(game.getGameSettings().fullscreen);
					game.updateFullscreen();
				}
			});
			layout.add(btnFullScreen);
		}
		return layout;
	}

	/**
	 * NinePatchDrawables use their total size as minimum size by default
	 * This helper function resizes them to their minimum, so they can be resized to be smaller than their total size
	 *
	 * @param patch The ninepatch to minimize
	 * @return the given, minimized Ninepatch
	 */
	public static Drawable minimizeNinePatch(NinePatchDrawable patch) {
		patch.setMinHeight(patch.getPatch().getBottomHeight() + patch.getPatch().getTopHeight());
		patch.setMinWidth(patch.getPatch().getLeftWidth() + patch.getPatch().getRightWidth());
		return patch;
	}


	public static float easeOut(float timePassed, float maxTime) {
		return easeOut(timePassed, maxTime, 3);
	}

	public static float easeOut(float timePassed, float maxTime, int intensity, float target) {
		return easeOut(timePassed, maxTime, intensity) * target;
	}

	public static float easeOut(float timePassed, float maxTime, int intensity) {
		if (timePassed > maxTime) {
			return 1;
		}
		return (float) (1 - Math.pow(1 - (timePassed / maxTime), intensity));
	}

	public static float easeInAndOut(float timePassed, float maxTime) {
		if (timePassed > maxTime) {
			return 1;
		}
		float timeProgress = timePassed / maxTime;
		if (timeProgress < 0.5) {
			return 2 * timeProgress * timeProgress;
		} else {
			return -1 + (4 - 2 * timeProgress) * timeProgress;
		}
	}

	public static float easeIn(float timePassed, float maxTime) {
		return easeIn(timePassed, maxTime, 2);
	}

	public static float easeIn(float timePassed, float maxTime, int intensity)
	{
		if (timePassed > maxTime)
		{
			return 1;
		}
		return (float) Math.pow(timePassed / maxTime, intensity);
	}

	public static float EaseIn(float timePassed, float maxTime, int intensity, float target) {
		return easeIn(timePassed, maxTime, intensity) * target;
	}

	public static float getButtonSize() {
		return PixelEscape.getPixelEscape().getGameConfig().useBiggerButtons() ? 48 : 32;
	}
}
