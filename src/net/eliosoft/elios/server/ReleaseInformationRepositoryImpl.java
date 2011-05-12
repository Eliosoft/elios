package net.eliosoft.elios.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

public class ReleaseInformationRepositoryImpl implements
		ReleaseInformationRepository {

	public static final String URL_PATTERN = "{0}/{1}.euf";

	public static final String RELEASEINFO_ROOT_URL = "releaseinfo.root.url";

	public static final String RELEASEINFO_CURENT_RELEASE_CODE = "releaseinfo.current.release";

	private final URI rootUrl;

	private final Preferences prefs;

	public ReleaseInformationRepositoryImpl(Preferences prefs) {
		String rootUrlStr = prefs.get(RELEASEINFO_ROOT_URL, null);
		if(rootUrlStr == null)
			throw new IllegalStateException("Unable to retrieve the URL of the release information url");
		
		rootUrl = URI.create(rootUrlStr);
		this.prefs = prefs;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.eliosoft.elios.server.ReleaseInformationRepository#getInstalled()
	 */
	@Override
	public ReleaseInformation getInstalled() {
		return getByReleaseCode(getInstalledReleaseCode());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.eliosoft.elios.server.ReleaseInformationRepository#getLatest()
	 */
	@Override
	public ReleaseInformation getLatest() {
		try {
			return checkout(buildLatestURI());
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.eliosoft.elios.server.ReleaseInformationRepository#getByReleaseCode(net.eliosoft.elios.server.ReleaseCode)
	 */
	@Override
	public ReleaseInformation getByReleaseCode(ReleaseCode releaseCode) {
		try {
			return checkout(buildURI(releaseCode));
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private ReleaseInformation checkout(URI uri) throws MalformedURLException,
			IOException {
		InputStream stream = null;
		try {
			stream = uri.toURL().openStream();

			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[4096];

			for (;;) {
				int i = stream.read(buf);
				if (i == -1)
					break;
				sb.append(new String(buf, 0, i, Charset.forName("UTF-8")));
			}

			return ReleaseInformation.fromJSON(sb.toString());
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	private URI buildURI(ReleaseCode code) {
		return URI.create(MessageFormat.format(URL_PATTERN, rootUrl,
				code.getCode()));
	}

	private URI buildLatestURI() {
		return URI.create(MessageFormat.format(URL_PATTERN, rootUrl, "latest"));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.eliosoft.elios.server.ReleaseInformationRepository#getInstalledReleaseCode()
	 */
	@Override
	public ReleaseCode getInstalledReleaseCode() {
		return ReleaseCode.create(prefs.get(
				RELEASEINFO_CURENT_RELEASE_CODE, null));
	}
}
