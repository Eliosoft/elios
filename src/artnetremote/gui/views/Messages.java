package artnetremote.gui.views;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class provides i18n functionalities.
 * 
 * @author Alexandre COLLIGNON
 */
public final class Messages {
	/** bundle fqn **/
	private static final String BUNDLE_NAME = "artnetremote.gui.views.messages"; //$NON-NLS-1$

	/** bundle instance **/
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
		// nothing
	}

	/**
	 * Returns the localized text of the given key if the resource is found,
	 * returns !key! (key is the argument) otherwise.
	 * 
	 * @param key string that identified a localized text
	 * @return the localized text or !key! is the resource is not found
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
