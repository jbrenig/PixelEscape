package net.brenig.pixelescape.game.data

import com.badlogic.gdx.audio.Music
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.lib.utils.AnimationUtils

/**
 * Music wrapper with additional features such as fading and queueing
 */
class GameMusic(private val game: PixelEscape) {

    var currentMusic: Music? = null
        set(value) {
            stop()
            if (game.gameConfig.musicAvailable) {
                field = value
                if (field != null) {
                    field!!.volume = currentVolume
                }
            }
        }

    /**
     * The target Music that should get faded in
     */
    private var fadeInMusic: Music? = null
    private var fadingProgress = 0f
    private var fadingTime = 0f

    private var currentVolume = 1f

    private var state = MusicState.STOPPED

    fun getStateString() = state.toString()

    /**
     * this function has to be called for fading
     *
     * @param delta time passed
     */
    fun update(delta: Float) {
        if (game.gameConfig.musicAvailable && state.isFading) {
            fadingProgress += delta
            if (state == MusicState.FADE_IN) {
                currentVolume = AnimationUtils.easeInAndOut(fadingProgress, fadingTime) * game.gameSettings.musicVolume
                if (fadingProgress > fadingTime) {
                    state = MusicState.PLAYING
                }
            } else {
                currentVolume = game.gameSettings.musicVolume - AnimationUtils.easeInAndOut(fadingProgress, fadingTime) * game.gameSettings.musicVolume
                if (fadingProgress > fadingTime) {
                    fadingProgress = 0f
                    when (state) {
                        MusicState.FADE_OUT_PAUSE -> pause()
                        MusicState.FADE_OUT_INTO -> {
                            setFadeInMusicCurrent()
                            fadeIn()
                        }
                        else -> stop()
                    }
                }
            }
            currentMusic!!.volume = currentVolume
        }
    }

    fun updateMusicVolume() {
        if (!state.isFading) {
            currentVolume = game.gameSettings.musicVolume
            currentMusic?.volume = game.gameSettings.musicVolume
        }
    }

    fun fadeIn() {
        play(true)
    }

    /**
     * stops current music if possible
     */
    fun stop() {
        if (currentMusic != null && state != MusicState.STOPPED) {
            currentMusic!!.stop()
            state = MusicState.STOPPED
            setFadeInMusicCurrent()
        }
    }

    /**
     * pauses current music if possible
     */
    fun pause() {
        if (currentMusic != null && currentMusic!!.isPlaying) {
            currentMusic!!.pause()
            state = MusicState.PAUSED
            setFadeInMusicCurrent()
        }
    }

    /**
     * sets music to fade into as current music (and stops any music playing)
     */
    fun setFadeInMusicCurrent() {
        if (fadeInMusic != null) {
            currentMusic = fadeInMusic
            fadeInMusic = null
        }
    }

    fun play(fadeIn: Boolean = false, fadeInTime: Float = DEFAULT_FADING_TIME) {
        if (game.gameConfig.musicAvailable) {
            if (currentMusic != null && state != MusicState.PLAYING && game.gameSettings.isMusicEnabled) {
                if (fadeIn) {
                    if (!state.isFading) {
                        fadingProgress = 0f
                        currentMusic!!.volume = 0f
                    }
                    fadingTime = fadeInTime
                    state = MusicState.FADE_IN
                } else {
                    currentMusic!!.volume = game.gameSettings.musicVolume
                    fadingProgress = 0f
                    fadingTime = 0f
                    state = MusicState.PLAYING
                }
                currentMusic!!.play()
            }
        }
    }

    fun playOrFadeInto(music: Music) {
        if (game.gameConfig.musicAvailable) {
            if (currentMusic != null && state.isPlaying) {
                if (currentMusic === music) {
                    fadeIn()
                    return
                }
                fadeIntoMusic(music)
            } else {
                currentMusic = music
                play()
            }
        }
    }

    fun fadeOutToStop(time: Float = DEFAULT_FADING_TIME) {
        if (!state.isFading || state == MusicState.FADE_IN) {
            fadingProgress = 0f
            fadingTime = time
        }
        state = MusicState.FADE_OUT_STOP
    }

    fun fadeOutToPause(time: Float = DEFAULT_FADING_TIME) {
        fadingProgress = 0f
        fadingTime = time
        state = MusicState.FADE_OUT_PAUSE
    }

    /**
     * Fades current music out, and fades given music in
     *
     * @param music music to play next
     */
    fun fadeIntoMusic(music: Music) {
        fadeInMusic = music
        state = MusicState.FADE_OUT_INTO
    }

    private enum class MusicState(val isFading: Boolean, val isPlaying: Boolean) {
        PLAYING(false, true),
        PAUSED(false, false),
        STOPPED(false, false),
        FADE_IN(true, true),
        FADE_OUT_STOP(true, true),
        FADE_OUT_PAUSE(true, true),
        FADE_OUT_INTO(true, true)
    }

    companion object {
        private const val DEFAULT_FADING_TIME = 1f
    }

}
