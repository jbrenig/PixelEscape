package net.brenig.pixelescape.lib;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import net.brenig.pixelescape.game.data.GameDebugSettings;

public final class LogHelper {

	public enum LogLevel {
		DEBUG("DEBUG", Application.LOG_DEBUG, Gdx.app::debug, Gdx.app::debug),
		INFO("INFO", Application.LOG_INFO, Gdx.app::log, Gdx.app::log),
		WARNING("WARN", Application.LOG_ERROR, Gdx.app::error, Gdx.app::error),
		ERROR("ERROR", Application.LOG_ERROR, Gdx.app::error, Gdx.app::error);

		String tag;
		int level;
		NormalLogger normalLogger;
		ExceptionLogger exceptionLogger;

		LogLevel(String tag, int level, NormalLogger normalLogger, ExceptionLogger exceptionLogger) {
			this.tag = tag;
			this.level = level;
			this.normalLogger = normalLogger;
			this.exceptionLogger = exceptionLogger;
		}
	}

	public interface NormalLogger {
		void log(String tag, String msg);
	}
	public interface ExceptionLogger {
		void log(String tag, String msg, Throwable t);
	}

	private static final String LOG_TAG_BRACKET_OPEN = "[";
	private static final String LOG_TAG_BRACKET_CLOSE = "]";
	private static final String LOG_TAG_BRACKET_SEPARATE = " | ";
	private static final String LOG_LEVEL_BRACKET_OPEN = "[";
	private static final String LOG_LEVEL_BRACKET_CLOSE = "]";

	private static final String LOG_TAG_NAME = "PixelEscape";

	public static void log(String msg) {
		log(LogLevel.INFO, null, msg, null);
	}

	public static void log(String tag, String msg) {
		log(LogLevel.INFO, tag, msg, null);
	}

	public static void log(LogLevel level, String tag, String msg) {
		log(level, tag, msg, null);
	}

	public static void log(LogLevel level, String tag, String msg, Throwable t) {
		StringBuilder builder = new StringBuilder();
		//level
		builder.append(LOG_LEVEL_BRACKET_OPEN).append(level.tag).append(LOG_LEVEL_BRACKET_CLOSE);
		//pixelescape tag
		builder.append(LOG_TAG_BRACKET_OPEN + LOG_TAG_NAME);
		//tag
		if (tag != null && !(tag.length() <= 0)) {
			builder.append(LOG_TAG_BRACKET_SEPARATE).append(tag);
		}
		builder.append(LOG_TAG_BRACKET_CLOSE);

		if (t != null) {
			level.exceptionLogger.log(builder.toString(), msg, t);
		} else {
			level.normalLogger.log(builder.toString(), msg);
		}
	}

	public static void debug(String msg) {
		debug(null, msg, null);
	}

	public static void debug(String tag, String msg) {
		debug(tag, msg, null);
	}

	public static void debug(String tag, String msg, Throwable t) {
		if (GameDebugSettings.get("DEBUG_LOGGING")) {
			log(LogLevel.DEBUG, tag, msg, t);
		}
	}

	public static void error(String msg) {
		log(LogLevel.ERROR, null, msg, null);
	}

	public static void error(String tag, String msg) {
		log(LogLevel.ERROR, tag, msg, null);
	}

	public static void error(String tag, String msg, Throwable t) {
		log(LogLevel.ERROR, tag, msg, t);
	}

	public static void warn(String msg) {
		log(LogLevel.WARNING, null, msg, null);
	}

	public static void warn(String tag, String msg) {
		log(LogLevel.WARNING, tag, msg, null);
	}

	public static void warn(String tag, String msg, Throwable t) {
		log(LogLevel.WARNING, tag, msg, t);
	}

	public static void info(String msg) {
		log(LogLevel.INFO, null, msg, null);
	}

	public static void info(String tag, String msg) {
		log(LogLevel.INFO, tag, msg, null);
	}

	public static void info(String tag, String msg, Throwable t) {
		log(LogLevel.INFO, tag, msg, t);
	}

	public static void setGDXLogLevel(int level) {
		Gdx.app.setLogLevel(level);
	}

	private LogHelper() {}
}
