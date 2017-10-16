package net.brenig.pixelescape.lib

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import net.brenig.pixelescape.game.data.GameDebugSettings

private const val LOG_TAG_BRACKET_OPEN = "["
private const val LOG_TAG_BRACKET_CLOSE = "]"
private const val LOG_TAG_BRACKET_SEPARATE = " | "
private const val LOG_LEVEL_BRACKET_OPEN = "["
private const val LOG_LEVEL_BRACKET_CLOSE = "]"

private const val LOG_TAG_NAME = "PixelEscape"

enum class LogLevel(internal var tag: String, internal var level: Int) {
    DEBUG("DEBUG", Application.LOG_DEBUG),
    INFO("INFO", Application.LOG_INFO),
    WARNING("WARN", Application.LOG_ERROR),
    ERROR("ERROR", Application.LOG_ERROR)
}

fun log(msg: String) {
    log(LogLevel.INFO, null, msg, null)
}

fun log(tag: String, msg: String) {
    log(LogLevel.INFO, tag, msg, null)
}

fun log(level: LogLevel, tag: String?, msg: String, t: Throwable? = null) {
    val builder = StringBuilder()
    //level
    builder.append(LOG_LEVEL_BRACKET_OPEN).append(level.tag).append(LOG_LEVEL_BRACKET_CLOSE)
    //pixelescape tag
    builder.append(LOG_TAG_BRACKET_OPEN + LOG_TAG_NAME)
    //tag
    if (!tag.isNullOrEmpty()) {
        builder.append(LOG_TAG_BRACKET_SEPARATE).append(tag)
    }
    builder.append(LOG_TAG_BRACKET_CLOSE)

    if (t != null) {
        when (level) {
            LogLevel.DEBUG -> Gdx.app.debug(builder.toString(), msg, t)
            LogLevel.INFO -> Gdx.app.log(builder.toString(), msg, t)
            LogLevel.WARNING, LogLevel.ERROR -> Gdx.app.error(builder.toString(), msg, t)
        }
    } else {
        when (level) {
            LogLevel.DEBUG -> Gdx.app.debug(builder.toString(), msg)
            LogLevel.INFO -> Gdx.app.log(builder.toString(), msg)
            LogLevel.WARNING, LogLevel.ERROR -> Gdx.app.error(builder.toString(), msg)
        }
    }
}

fun debug(msg: String) {
    debug(null, msg, null)
}

@JvmOverloads
fun debug(tag: String?, msg: String, t: Throwable? = null) {
    if (GameDebugSettings["DEBUG_LOGGING"]) {
        log(LogLevel.DEBUG, tag, msg, t)
    }
}

fun error(msg: String) {
    log(LogLevel.ERROR, null, msg, null)
}

fun error(tag: String?, msg: String, t: Throwable? = null) {
    log(LogLevel.ERROR, tag, msg, t)
}

fun warn(msg: String) {
    log(LogLevel.WARNING, null, msg, null)
}

fun warn(tag: String?, msg: String, t: Throwable? = null) {
    log(LogLevel.WARNING, tag, msg, t)
}

fun info(msg: String) {
    log(LogLevel.INFO, null, msg, null)
}

fun info(tag: String?, msg: String, t: Throwable? = null) {
    log(LogLevel.INFO, tag, msg, t)
}

fun setGDXLogLevel(level: Int) {
    Gdx.app.logLevel = level
}

fun setGDXLogLevel(level: LogLevel) {
    Gdx.app.logLevel = level.level
}