package vtm.rcp.app;

import java.awt.Canvas;
import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.LwjglGL20;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import okhttp3.Cache;

public class GdxMapApp extends GdxMap {

	private static final String	STATE_MAP_POS_X				= "STATE_MAP_POS_X";						//$NON-NLS-1$
	private static final String	STATE_MAP_POS_Y				= "STATE_MAP_POS_Y";						//$NON-NLS-1$
	private static final String	STATE_MAP_POS_ZOOM_LEVEL	= "STATE_MAP_POS_ZOOM_LEVEL";				//$NON-NLS-1$
	private static final String	STATE_MAP_POS_BEARING		= "STATE_MAP_POS_BEARING";					//$NON-NLS-1$
	private static final String	STATE_MAP_POS_SCALE			= "STATE_MAP_POS_SCALE";					//$NON-NLS-1$
	private static final String	STATE_MAP_POS_TILT			= "STATE_MAP_POS_TILT";						//$NON-NLS-1$

	public static final Logger	log							= LoggerFactory.getLogger(GdxMapApp.class);
	private IDialogSettings		_state;

	LwjglApplication			_lwjglApp;

	public GdxMapApp(IDialogSettings state) {

		_state = state;
	}

	protected static LwjglApplicationConfiguration getConfig(String title) {

		LwjglApplicationConfiguration.disableAudio = true;
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.title = title != null ? title : "vtm-gdx";
		cfg.width = 1200;
		cfg.height = 1000;
		cfg.stencil = 8;
		cfg.samples = 2;
		cfg.foregroundFPS = 30;
		cfg.backgroundFPS = 10;

		cfg.forceExit = false;

		return cfg;
	}

	public static void init() {

		// load native library
		new SharedLibraryLoader().load("vtm-jni");

		// init globals
		AwtGraphics.init();

		GdxAssets.init("assets/");

		GLAdapter.init(new LwjglGL20());
		GLAdapter.GDX_DESKTOP_QUIRKS = true;
	}

	@Override
	public void dispose() {

		saveState();

		super.dispose();
	}

	private void saveState() {

		final MapPosition mapPosition = mMap.getMapPosition();

		_state.put(STATE_MAP_POS_X, mapPosition.x);
		_state.put(STATE_MAP_POS_Y, mapPosition.y);
		_state.put(STATE_MAP_POS_BEARING, mapPosition.bearing);
		_state.put(STATE_MAP_POS_SCALE, mapPosition.scale);
		_state.put(STATE_MAP_POS_TILT, mapPosition.tilt);
		_state.put(STATE_MAP_POS_ZOOM_LEVEL, mapPosition.zoomLevel);
	}

	void closeMap() {

		_lwjglApp.stop();
	}

	private void restoreState() {

		final MapPosition mapPosition = new MapPosition();

		mapPosition.x = Util.getStateDouble(_state, STATE_MAP_POS_X, 0.5);
		mapPosition.y = Util.getStateDouble(_state, STATE_MAP_POS_Y, 0.5);

		mapPosition.bearing = Util.getStateFloat(_state, STATE_MAP_POS_BEARING, 0);
		mapPosition.tilt = Util.getStateFloat(_state, STATE_MAP_POS_TILT, 0);

		mapPosition.scale = Util.getStateDouble(_state, STATE_MAP_POS_SCALE, 1);
		mapPosition.zoomLevel = Util.getStateInt(_state, STATE_MAP_POS_ZOOM_LEVEL, 1);

		mMap.setMapPosition(mapPosition);
	}

	private String getCacheDir() {

		final String workingDirectory = Platform.getInstanceLocation().getURL().getPath();

		final IPath tileCachePath = new Path(workingDirectory).append("vtm-tile-cache");

		if (tileCachePath.toFile().exists() == false) {
			tileCachePath.toFile().mkdirs();
		}

		return tileCachePath.toOSString();
	}

	@Override
	public void createLayers() {

		final Cache cache = new Cache(new File(getCacheDir()), Integer.MAX_VALUE);

		final OkHttpEngine.OkHttpFactory httpFactory = new OkHttpEngine.OkHttpFactory(cache);

		final OSciMap4TileSource tileSource = OSciMap4TileSource//
				.builder()
				.httpFactory(httpFactory)
				.build();

		initDefaultLayers(tileSource, false, true, true);

		restoreState();
	}

	@Override
	public void resize(final int w, final int h) {

		if (h < 1) {

			// Fix exception
			//
			// Exception in thread "LWJGL Application"
			// java.lang.IllegalArgumentException: top == bottom
			// at org.oscim.renderer.GLMatrix.frustumM(GLMatrix.java:331)
			// at org.oscim.map.ViewController.setScreenSize(ViewController.java:50)
			// at org.oscim.gdx.GdxMap.resize(GdxMap.java:122)
			// at net.tourbook.map.vtm.VtmMap.resize(VtmMap.java:176)

			return;
		}

		super.resize(w, h);
	}

	public void run(Canvas canvas) {

		init();

		_lwjglApp = new LwjglApplication(new GdxMapApp(_state), getConfig(null), canvas);
	}
}
