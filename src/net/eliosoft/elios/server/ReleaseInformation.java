package net.eliosoft.elios.server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;

/**
 * Update information value object.
 * 
 * @author E362200
 */
public class ReleaseInformation {

    /** the release code. **/
    private ReleaseCode releaseCode;

    /** the release time. **/
    private long releaseTime;

    /** the URL of the download page. **/
    private URL downloadUrl;

    /** the URL of the release page. **/
    private URL releaseNoteUrl;

    /**
     * Constructs a {@link ReleaseInformation} with the given parameters.
     * 
     * @param releaseCode
     *            the release code
     * @param downloadUrl
     *            the URL of the download page
     * @param releaseNoteUrl
     *            the URL of the release note
     * @param releaseDate
     *            the date of the release
     */
    public ReleaseInformation(final ReleaseCode releaseCode,
            final URL downloadUrl, final URL releaseNoteUrl,
            final Date releaseDate) {
        this.releaseCode = releaseCode;
        this.downloadUrl = downloadUrl;
        this.releaseTime = releaseDate.getTime();
        this.releaseNoteUrl = releaseNoteUrl;
    }

    /**
     * Package constructor for builder pattern.
     */
    ReleaseInformation() {
        // nothing
    }

    /**
     * Returns the release code.
     * 
     * @return the release code
     */
    public ReleaseCode getReleaseCode() {
        return releaseCode;
    }

    /**
     * Sets the release code.
     * 
     * @param releaseCode
     *            the release code
     */
    void setReleaseCode(final ReleaseCode releaseCode) {
        this.releaseCode = releaseCode;
    }

    /**
     * Sets the {@link URL} of the download page.
     * 
     * @param downloadUrl
     *            a string representation of the URL
     * @throws MalformedURLException
     *             if the given string is not a valid {@link URL}.
     */
    void setDownloadUrl(final String downloadUrl) throws MalformedURLException {
        this.downloadUrl = URI.create(downloadUrl).toURL();
    }

    /**
     * Sets the {@link URL} of the release note page.
     * 
     * @param releaseNoteUrl
     *            a string representation of the URL of the release note
     * @throws MalformedURLException
     */
    void setReleaseNoteUrl(final String releaseNoteUrl)
            throws MalformedURLException {
        this.releaseNoteUrl = URI.create(releaseNoteUrl).toURL();
    }

    void setReleaseTime(final long releaseTime) {
        this.releaseTime = releaseTime;
    }

    /**
     * Returns the release time.
     * 
     * @return the release time
     */
    public long getReleaseTime() {
        return releaseTime;
    }

    /**
     * Returns an {@link URL} that points to the download page.
     * 
     * @return the {@link URL} of the download page
     */
    public URL getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Returns the {@link URL} that points to the release note.
     * 
     * @return the {@link URL} of the release note
     */
    public URL getReleaseNoteUrl() {
        return releaseNoteUrl;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((downloadUrl == null) ? 0 : downloadUrl.hashCode());
        result = prime * result
                + ((releaseCode == null) ? 0 : releaseCode.hashCode());
        result = prime * result
                + ((releaseNoteUrl == null) ? 0 : releaseNoteUrl.hashCode());
        result = prime * result + (int) (releaseTime ^ (releaseTime >>> 32));
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReleaseInformation other = (ReleaseInformation) obj;
        if (downloadUrl == null) {
            if (other.downloadUrl != null) {
                return false;
            }
        } else if (!downloadUrl.equals(other.downloadUrl)) {
            return false;
        }
        if (releaseCode == null) {
            if (other.releaseCode != null) {
                return false;
            }
        } else if (!releaseCode.equals(other.releaseCode)) {
            return false;
        }
        if (releaseNoteUrl == null) {
            if (other.releaseNoteUrl != null) {
                return false;
            }
        } else if (!releaseNoteUrl.equals(other.releaseNoteUrl)) {
            return false;
        }
        if (releaseTime != other.releaseTime) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[').append("releaseNoteUrl:").append(releaseNoteUrl)
                .append(", releaseNoteDate:").append(releaseTime)
                .append(", downloadUrl:").append(downloadUrl).append(']');
        return sb.toString();
    }

    /**
     * Returns a {@link ReleaseInformation} from the json string.
     * 
     * @param json
     *            a json string
     * @return a {@link ReleaseInformation} from the json string
     */
    public static ReleaseInformation fromJSON(final String json) {
        return new Gson().fromJson(json, ReleaseInformation.class);
    }
}
