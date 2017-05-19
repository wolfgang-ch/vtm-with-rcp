package vtm.rcp.app;

import java.awt.Canvas;

import org.lwjgl.opengl.Display;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.LwjglGL20;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;

public class GdxMapApp extends GdxMap {

	public static final Logger	log	= LoggerFactory.getLogger(GdxMapApp.class);

	LwjglApplication			_lwjglApp;

	protected static LwjglApplicationConfiguration getConfig(String title) {

		LwjglApplicationConfiguration.disableAudio = true;
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.title = title != null ? title : "vtm-gdx";
		cfg.width = 1200; // 800;
		cfg.height = 1000; // 600;
		cfg.stencil = 8;
		cfg.samples = 2;
		cfg.foregroundFPS = 30;
		cfg.backgroundFPS = 10;

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

// !!! both methods will also close the RCP app, therefore it is disabled !!!		

//		_lwjglApp.stop();
//
//		Display.destroy();
	}

	@Override
	public void createLayers() {

		TileSource tileSource = new OSciMap4TileSource();

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
