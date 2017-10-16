package net.brenig.pixelescape.game.data

import com.badlogic.gdx.audio.Music
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.lib.utils.AnimationUtils

/**
 * Music wrapper with additional features such as fading and queueing
 */
class GameMusic(private val game: PixelEscape) {

    private var currentMusic: Music? = null

    /**
     * The target Music that should get faded in
     */
    private var fadeInMusic: Music? = null
    private var fadingProgress = 0f
    private var fadingTime = 0f

    private var currentVolume = 1f

    private var state = MusicState.STOPPED

    /**
     * @return true if fading is in progress
     */
    val isFading: Boolean
        get() = state == MusicState.FADE_IN || state == MusicState.FADE_OUT_STOP || state == MusicState.FADE_OUT_PAUSE || state == MusicState.FADE_OUT_INTO

    val isPlaying: Boolean
        get() = state != MusicState.STOPPED && state != MusicState.PAUSED

    fun getState() = state.toString()

    fun setCurrentMusic(m: Music?) {
        stop()
        @Suppress("ConstantConditionIf")
        if (Reference.ENABLE_MUSIC) {
            currentMusic = m
            if (currentMusic != null) {
                currentMusic!!.volume = currentVolume
            }
        }
    }

    /**
     * this function has to be called for fading
     *
     * @param delta time passed
     */
    fun update(delta: Float) {
        if (Reference.ENABLE_MUSIC && isFading) {
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
        if (!isFading) {
            currentVolume = game.gameSettings.musicVolume
            currentMusic!!.volume = game.gameSettings.musicVolume
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
            setCurrentMusic(fadeInMusic)
            fadeInMusic = null
        }
    }

    fun play() {
        play(false, 0f)
    }

    @JvmOverloads
    fun play(fadeIn: Boolean, fadeInTime: Float = defaultFadingTime) {
        @Suppress("ConstantConditionIf")
        if (Reference.ENABLE_MUSIC) {
            if (currentMusic != null && state != MusicState.PLAYING && game.gameSettings.isMusicEnabled) {
                if (fadeIn) {
                    if (!isFading) {
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

    fun playOrFadeInto(music: Music?) {
        @Suppress("ConstantConditionIf")
        if (Reference.ENABLE_MUSIC) {
            if (music == null) {
                stop()
            } else if (currentMusic != null && isPlaying) {
                if (currentMusic === music) {
                    fadeIn()
                    return
                }
                fadeIntoMusic(music)
            } else {
                setCurrentMusic(music)
                play()
            }
        }
    }

    @JvmOverloads
    fun fadeOutToStop(time: Float = defaultFadingTime) {
        if (!isFading || state == MusicState.FADE_IN) {
            fadingProgress = 0f
            fadingTime = time
        }
        state = MusicState.FADE_OUT_STOP
    }

    @JvmOverloads
    fun fadeOutToPause(time: Float = defaultFadingTime) {
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

    private enum class MusicState {
        PLAYING, PAUSED, STOPPED, FADE_IN, FADE_OUT_STOP, FADE_OUT_PAUSE, FADE_OUT_INTO
    }

    companion object {
        private const val defaultFadingTime = 1f
    }

}
