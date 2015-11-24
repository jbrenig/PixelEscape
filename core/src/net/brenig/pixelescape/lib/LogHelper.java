package net.brenig.pixelescape.lib;

import com.badlogic.gdx.Gdx;

import net.brenig.pixelescape.game.data.GameDebugSettings;

/**
 * Created by Jonas Brenig on 11.10.2015.
 */
public class LogHelper {

	private static final String LOG_TAG_BRACKET_OPEN = "[";
	private static final String LOG_TAG_BRACKET_CLOSE = "]";
	private static final String LOG_TAG_BRACKET_SEPARATE = " | ";
	private static final String LOG_LEVEL_BRACKET_OPEN = "[";
	private static final String LOG_LEVEL_BRACKET_CLOSE = "]";

	private static final String LOG_TAG_NAME = "PixelEscape";

	public static final String LOG_LEVEL_ERROR = "Error";
	public static final String LOG_LEVEL_WARNING = "Warning";
	private static final String LOG_LEVEL_DEBUG = "Debug";

	public static void log(String msg) {
		log(null, null, msg, null);
	}

	public static void log(String tag, String msg) {
		log(null, tag, msg, null);
	}

	public static void log(String level, String tag, String msg) {
		log(level, tag, msg, null);
	}

	public static void log(String level, String tag, String msg, Throwable t) {
		StringBuilder builder = new StringBuilder();
		if(level != null && !(level.length() <= 0)) {
			builder.append(LOG_LEVEL_BRACKET_OPEN).append(level).append(LOG_LEVEL_BRACKET_CLOSE);
		}
		builder.append(LOG_TAG_BRACKET_OPEN + LOG_TAG_NAME);
		if(tag != null && !(tag.length() <= 0)) {
			builder.append(LOG_TAG_BRACKET_SEPARATE).append(tag);
		}
		builder.append(LOG_TAG_BRACKET_CLOSE);
		if(t != null) {
			Gdx.app.log(builder.toString(), msg);
		} else {
			Gdx.app.log(builder.toString(), msg);
		}
	}

	public static void debug(String msg) {
		debug(null, msg, null);
	}

	public static void debug(String tag, String msg) {
		debug(tag, msg, null);
	}

	public static void debug(String tag, String msg, Throwable t) {
		if(GameDebugSettings.get("DEBUG_LOGGING")) {
			log(LOG_LEVEL_DEBUG, tag, msg, t);
		}
	}

	public static void error(String msg) {
		log(LOG_LEVEL_ERROR, null, msg, null);
	}

	public static void newLine() {
		Gdx.app.log("", "");
	}

	public static void warn(String msg) {
		log(LOG_LEVEL_WARNING, null, msg, null);
	}

	public static void warn(String tag, String msg) {
		log(LOG_LEVEL_WARNING, tag, msg, null);
	}

	public static void warn(String tag, String msg, Throwable t) {
		log(LOG_LEVEL_WARNING, tag, msg, t);
	}
}
