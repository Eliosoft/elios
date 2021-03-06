package net.eliosoft.elios.server;

/**
 * This repository allow to fetch {@link ReleaseInformation}.
 *
 * @author E362200
 */
public interface ReleaseInformationRepository {

    /**
     * Returns the ReleaseCode of the current installed version.
     *
     * @return the {@link ReleaseCode} of current installed version
     */
    ReleaseCode getInstalledReleaseCode();

    /**
     * Returns the {@link ReleaseInformation} of current installed version.
     *
     * @return the {@link ReleaseInformation} of current installed version
     */
    ReleaseInformation getInstalled();

    /**
     * Returns the {@link ReleaseInformation} of the latest release.
     *
     * @return the {@link ReleaseInformation} of the latest release
     */
    ReleaseInformation getLatest();

    /**
     * Returns the {@link ReleaseInformation} of the release identified by
     * {@link ReleaseCode}.
     *
     * @param releaseCode
     *            a {@link ReleaseCode}
     * @return a {@link ReleaseInformation} or null if any release was found
     */
    ReleaseInformation getByReleaseCode(ReleaseCode releaseCode);

}
