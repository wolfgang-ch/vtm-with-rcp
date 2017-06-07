package vtm.rcp.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.oscim.core.Tile;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.source.UrlTileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Cache;
import okhttp3.HttpUrl;

public class OkHttpTileCache implements ITileCache {

	public static final Logger	log			= LoggerFactory.getLogger(OkHttpTileCache.class);

	private static final String	CACHE_DIR	= Util.getCacheDir();
	private static final Path	CACHE_PATH	= Paths.get(CACHE_DIR);

	private UrlTileSource		_tileSource;

	public OkHttpTileCache(final UrlTileSource tileSource) {
		_tileSource = tileSource;
	}

	private String getCacheKey(final Tile tile) {

		try {

			final String tileUrl = _tileSource.getTileUrl(tile);

			final HttpUrl httpUrl = HttpUrl.get(new URL(tileUrl));
			final String cacheKey = Cache.key(httpUrl);

			return cacheKey;

		} catch (final MalformedURLException e) {
			log.error("", e);
		}

		return null;
	}

	@Override
	public TileReader getTile(final Tile tile) {

		final String cacheKey = getCacheKey(tile);
		final String cacheFileName = cacheKey + ".1";
		final Path cacheFilePath = CACHE_PATH.resolve(cacheFileName);

		if (Files.exists(cacheFilePath)) {

			return new TileReader() {

				@Override
				public InputStream getInputStream() {

					try {

						log.debug("Using http tile cache file " + cacheFilePath);

						return Files.newInputStream(cacheFilePath);

					} catch (final IOException e) {
						log.error("", e);
					}

					return null;
				}

				@Override
				public Tile getTile() {
					return tile;
				}
			};
		}

		return null;
	}

	@Override
	public void setCacheSize(final long size) {}

	@Override
	public TileWriter writeTile(final Tile tile) {

		return new TileWriter() {

			@Override
			public void complete(final boolean success) {}

			@Override
			public OutputStream getOutputStream() {

				final String cacheKey = getCacheKey(tile);
				final String cacheFileName = cacheKey + ".dummy";
				final Path cacheFilePath = CACHE_PATH.resolve(cacheFileName);

				try {

					return Files.newOutputStream(cacheFilePath);

				} catch (final IOException e) {
					log.error("", e);
				}

				return null;
			}

			@Override
			public Tile getTile() {
				return tile;
			}
		};
	}

}
