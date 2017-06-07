package vtm.rcp.app;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;

public class Util {

	public static String getCacheDir() {

		final String workingDirectory = Platform.getInstanceLocation().getURL().getPath();

		final IPath tileCachePath = new Path(workingDirectory).append("vtm-tile-cache");

		if (tileCachePath.toFile().exists() == false) {
			tileCachePath.toFile().mkdirs();
		}

		return tileCachePath.toOSString();
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a float value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static double getStateDouble(final IDialogSettings state, final String key, final double defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getDouble(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a float value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static float getStateFloat(final IDialogSettings state, final String key, final float defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getFloat(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns an integer value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static int getStateInt(final IDialogSettings state, final String key, final int defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getInt(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
}
