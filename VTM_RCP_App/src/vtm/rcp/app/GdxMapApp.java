package vtm.rcp.app;

import java.awt.Canvas;
import java.io.File;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.LwjglGL20;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Layers;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

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

	private enum TileSourceProvider {

		CustomTileProvider, //
		OpenScienceMap, //
		Mapzen, //
	}

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

		log.debug("Map position: " + mapPosition.toString());
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

	@Override
	public void createLayers() {

		final Cache cache = new Cache(new File(Util.getCacheDir()), Integer.MAX_VALUE);

		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.cache(cache)
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS);

		OkHttpEngine.OkHttpFactory httpFactory = new OkHttpEngine.OkHttpFactory(builder);

		UrlTileSource tileSource;
		ThemeFile mapTheme;

		switch (TileSourceProvider.CustomTileProvider) {

//		case CustomTileProvider:
//
//			mapTheme = VtmThemes.MAPZEN;
//
//			tileSource = CustomTileSource //
//					.builder()
//					.httpFactory(httpFactory)
//					.build();
//			break;

//		case Mapzen:
//
//			mapTheme = VtmThemes.MAPZEN;
//			
//			// Mapzen requires an API key that the tiles can be loaded
//			String apiKey = System.getProperty("MapzenApiKey", "mapzen-xxxxxxx");
//
//			tileSource = MapboxTileSource
//					.builder()
//					.apiKey(apiKey) // Put a proper API key
//					.httpFactory(httpFactory)
//					.build();
//			break;

		default:

			mapTheme = VtmThemes.DEFAULT;

			tileSource = OSciMap4TileSource//
					.builder()
					.httpFactory(httpFactory)
					.build();
			break;
		}

		VectorTileLayer mapLayer = mMap.setBaseMap(tileSource);

		mMap.setTheme(mapTheme);
		mMap.viewport().setMaxTilt(88);

		Layers layers = mMap.layers();

		layers.add(new BuildingLayer(mMap, mapLayer));
		layers.add(new LabelLayer(mMap, mapLayer));

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
