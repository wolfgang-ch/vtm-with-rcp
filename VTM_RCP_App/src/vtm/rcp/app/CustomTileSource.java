package vtm.rcp.app;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.mvt.TileDecoder;

public class CustomTileSource extends UrlTileSource {

	private final static String	DEFAULT_URL		= "http://192.168.99.99:8080/all";
	private final static String	DEFAULT_PATH	= "/{Z}/{X}/{Y}.mvt";

	public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {

		public Builder() {
			super(DEFAULT_URL, DEFAULT_PATH, 1, 17);
		}

		@Override
		public CustomTileSource build() {
			
			return new CustomTileSource(this);
		}
	}

	private CustomTileSource(final Builder<?> builder) {
		super(builder);
	}

	@SuppressWarnings("rawtypes")
	public static Builder<?> builder() {
		
		return new Builder();
	}

	@Override
	public ITileDataSource getDataSource() {

		return new UrlTileDataSource(this, new TileDecoder(), getHttpEngine());
	}
}
