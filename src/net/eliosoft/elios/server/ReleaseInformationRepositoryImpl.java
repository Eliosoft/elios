package net.eliosoft.elios.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

/**
 * A {@link ReleaseInformationRepository} implementation.
 *
 * @author acollign
 */
public class ReleaseInformationRepositoryImpl implements
        ReleaseInformationRepository {

    private static final int FETCH_TIMEOUT = 5000;

    /**
     * The pattern of the URL of the Elios update file.
     */
    public static final String URL_PATTERN = "{0}/{1}.euf";

    /**
     * The release info root URL preference key.
     */
    public static final String RELEASEINFO_ROOT_URL = "releaseinfo.root.url";

    /**
     * The current release preference key.
     */
    public static final String RELEASEINFO_CURENT_RELEASE_CODE = "releaseinfo.current.release";

    private final URI rootUrl;

    private final Preferences prefs;

    /**
     * Constructs a {@link ReleaseInformationRepositoryImpl} according to the
     * configuration stored in the {@link Preferences}.
     *
     * @param prefs
     *            a {@link Preferences} instance
     */
    public ReleaseInformationRepositoryImpl(final Preferences prefs) {
        String rootUrlStr = prefs.get(RELEASEINFO_ROOT_URL, null);
        if (rootUrlStr == null) {
            throw new IllegalStateException(
                    "Unable to retrieve the URL of the release information url");
        }
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
    public ReleaseInformation getByReleaseCode(final ReleaseCode releaseCode) {
        try {
            return checkout(buildURI(releaseCode));
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private ReleaseInformation checkout(final URI uri)
            throws MalformedURLException, IOException {
        InputStream stream = null;
        try {
            stream = streamOfUri(uri);

            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[4096];

            for (;;) {
                int i = stream.read(buf);
                if (i == -1) {
                    break;
                }
                sb.append(new String(buf, 0, i, Charset.forName("UTF-8")));
            }

            return ReleaseInformation.fromJSON(sb.toString());
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private InputStream streamOfUri(final URI uri) throws IOException,
            MalformedURLException {
        InputStream stream;
        final URLConnection cn = uri.toURL().openConnection();
        cn.setConnectTimeout(FETCH_TIMEOUT);
        stream = cn.getInputStream();
        return stream;
    }

    private URI buildURI(final ReleaseCode code) {
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
        return ReleaseCode.create(prefs.get(RELEASEINFO_CURENT_RELEASE_CODE,
                null));
    }
}
