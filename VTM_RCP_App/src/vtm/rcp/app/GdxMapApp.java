package vtm.rcp.app;

import java.awt.Canvas;
import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
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

	public static final Logger	log	= LoggerFactory.getLogger(GdxMapApp.class);

	LwjglApplication			_lwjglApp;

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

	void closeMap() {

		_lwjglApp.stop();
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

// ORIGINAL

//		TileSource tileSource = new OSciMap4TileSource();

///////////////////////////////////////////////////////////////////////////////////////////////		

		final Cache cache = new Cache(new File(getCacheDir()), Integer.MAX_VALUE);

		final OkHttpEngine.OkHttpFactory httpFactory = new OkHttpEngine.OkHttpFactory(cache);

		final OSciMap4TileSource tileSource = OSciMap4TileSource//
				.builder()
				.httpFactory(httpFactory)
				.build();

		initDefaultLayers(tileSource, false, true, true);

		mMap.setMapPosition(0, 0, 1 << 2);

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

		_lwjglApp = new LwjglApplication(new GdxMapApp(), getConfig(null), canvas);
	}
}
