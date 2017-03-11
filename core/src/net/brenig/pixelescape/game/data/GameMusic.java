package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.audio.Music;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;

import javax.annotation.Nullable;

/**
 * Music wrapper with additional features such as fading and queueing
 */
public class GameMusic {

	private enum MusicState {
		PLAYING, PAUSED, STOPPED, FADE_IN, FADE_OUT_STOP, FADE_OUT_PAUSE, FADE_OUT_INTO
	}

	private Music currentMusic;

	/**
	 * The target Music that should get faded in
	 */
	private Music fadeInMusic;


	private final PixelEscape game;
	private float fadingProgress = 0;
	private float fadingTime = 0;
	private static final float defaultFadingTime = 1;

	private float currentVolume = 1;

	private MusicState state = MusicState.STOPPED;

	public GameMusic(PixelEscape game) {
		this.game = game;
	}

	public MusicState getState() {
		return state;
	}

	public void setCurrentMusic(@Nullable Music m) {
		stop();
		if(Reference.ENABLE_MUSIC) {
			currentMusic = m;
			if (currentMusic != null) {
				currentMusic.setVolume(currentVolume);
			}
		}
	}

	/**
	 * this function has to be called for fading
	 *
	 * @param delta time passed
	 */
	public void update(float delta) {
		if (Reference.ENABLE_MUSIC && isFading()) {
			fadingProgress += delta;
			if (state == MusicState.FADE_IN) {
				currentVolume = Utils.easeInAndOut(fadingProgress, fadingTime) * game.getGameSettings().getMusicVolume();
				if (fadingProgress > fadingTime) {
					state = MusicState.PLAYING;
				}
			} else {
				currentVolume = game.getGameSettings().getMusicVolume() - Utils.easeInAndOut(fadingProgress, fadingTime) * game.getGameSettings().getMusicVolume();
				if (fadingProgress > fadingTime) {
					fadingProgress = 0;
					if (state == MusicState.FADE_OUT_PAUSE) {
						pause();
					} else if (state == MusicState.FADE_OUT_INTO) {
						setFadeInMusicCurrent();
						fadeIn();
					} else {
						stop();
					}
				}
			}
			currentMusic.setVolume(currentVolume);
		}
	}

	public void updateMusicVolume() {
		if (!isFading()) {
			currentVolume = game.getGameSettings().getMusicVolume();
			currentMusic.setVolume(game.getGameSettings().getMusicVolume());
		}
	}

	public void fadeIn() {
		play(true);
	}

	/**
	 * @return true if fading is in progress
	 */
	public boolean isFading() {
		return state == MusicState.FADE_IN || state == MusicState.FADE_OUT_STOP || state == MusicState.FADE_OUT_PAUSE || state == MusicState.FADE_OUT_INTO;
	}

	/**
	 * stops current music if possible
	 */
	public void stop() {
		if (currentMusic != null && state != MusicState.STOPPED) {
			currentMusic.stop();
			state = MusicState.STOPPED;
			setFadeInMusicCurrent();
		}
	}

	/**
	 * pauses current music if possible
	 */
	public void pause() {
		if (currentMusic != null && currentMusic.isPlaying()) {
			currentMusic.pause();
			state = MusicState.PAUSED;
			setFadeInMusicCurrent();
		}
	}

	/**
	 * sets music to fade into as current music (and stops any music playing)
	 */
	public void setFadeInMusicCurrent() {
		if (fadeInMusic != null) {
			setCurrentMusic(fadeInMusic);
			fadeInMusic = null;
		}
	}

	public void play() {
		play(false, 0);
	}

	public void play(boolean fadeIn) {
		play(fadeIn, defaultFadingTime);
	}

	public void play(boolean fadeIn, float fadeInTime) {
		if(Reference.ENABLE_MUSIC) {
			if (currentMusic != null && state != MusicState.PLAYING && game.getGameSettings().isMusicEnabled()) {
				if (fadeIn) {
					if (!isFading()) {
						fadingProgress = 0;
						currentMusic.setVolume(0);
					}
					fadingTime = fadeInTime;
					state = MusicState.FADE_IN;
				} else {
					currentMusic.setVolume(game.getGameSettings().getMusicVolume());
					fadingProgress = 0;
					fadingTime = 0;
					state = MusicState.PLAYING;
				}
				currentMusic.play();
			}
		}
	}

	public void playOrFadeInto(@Nullable Music music) {
		if(Reference.ENABLE_MUSIC) {
			if (music == null) {
				stop();
			} else if (currentMusic != null && isPlaying()) {
				if (currentMusic == music) {
					fadeIn();
					return;
				}
				fadeIntoMusic(music);
			} else {
				setCurrentMusic(music);
				play();
			}
		}
	}

	public boolean isPlaying() {
		return state != MusicState.STOPPED && state != MusicState.PAUSED;
	}

	public void fadeOutToStop() {
		fadeOutToStop(defaultFadingTime);
	}

	public void fadeOutToStop(float time) {
		if (!isFading() || state == MusicState.FADE_IN) {
			fadingProgress = 0;
			fadingTime = time;
		}
		state = MusicState.FADE_OUT_STOP;
	}

	public void fadeOutToPause() {
		fadeOutToPause(defaultFadingTime);
	}

	public void fadeOutToPause(float time) {
		fadingProgress = 0;
		fadingTime = time;
		state = MusicState.FADE_OUT_PAUSE;
	}

	/**
	 * Fades current music out, and fades given music in
	 *
	 * @param music music to play next
	 */
	public void fadeIntoMusic(Music music) {
		fadeInMusic = music;
		state = MusicState.FADE_OUT_INTO;
	}

}
