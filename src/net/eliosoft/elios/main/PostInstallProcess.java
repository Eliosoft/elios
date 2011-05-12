package net.eliosoft.elios.main;

import java.net.URI;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.eliosoft.elios.server.ReleaseCode;
import net.eliosoft.elios.server.ReleaseInformationRepositoryImpl;

/**
 * Executable class that performs post installation process.
 * 
 * @author acollign
 * @since May 12, 2011
 */
public class PostInstallProcess {

    /**
     * The logger
     */
    private static final Logger LOGGER = Logger
	    .getLogger(PostInstallProcess.class.getCanonicalName());

    /**
     * Treats the two arguments as a release code and a URL. The release code is
     * the current one and the URL is the root URL of the eliosoft update site.
     * 
     * @param args
     *            a string array with a release code and a URL
     */
    public static void main(String[] args) {

	if (args.length != 2) {
	    LOGGER.severe("A release code and an url must be given in argument");
	}

	ReleaseCode code = null;

	try {
	    code = ReleaseCode.create(args[0]);
	} catch (IllegalArgumentException iae) {
	    LOGGER.severe("The release code is not well formed, see "
		    + iae.getMessage());
	}
	URI rootUrl = null;
	try {
	    rootUrl = URI.create(args[1]);
	} catch (IllegalArgumentException iae) {
	    LOGGER.severe("The url is not well formed, see " + iae.getMessage());
	}

	LOGGER.info("Registering update information");
	Preferences prefs = Preferences.userNodeForPackage(Elios.class);
	prefs.put(
		ReleaseInformationRepositoryImpl.RELEASEINFO_CURENT_RELEASE_CODE,
		code.getCode());
	prefs.put(ReleaseInformationRepositoryImpl.RELEASEINFO_ROOT_URL,
		rootUrl.toString());
	try {
	    prefs.flush();
	    LOGGER.info("End of post processing");
	} catch (BackingStoreException e) {
	    LOGGER.severe(e.getMessage());
	}
    }
}
