/*
 * This file is part of Elios.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * Elios is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Elios is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Elios. If not, see <http://www.gnu.org/licenses/>.
 */

package net.eliosoft.elios.gui.views;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import net.eliosoft.elios.main.LoggersManager;

/**
 * This class provides i18n functionalities.
 *
 * @author Alexandre COLLIGNON
 */
public final class Messages {
    /** bundle full qualified name. **/
    private static final String BUNDLE_NAME = "net.eliosoft.elios.gui.views.messages"; //$NON-NLS-1$

    private static final Logger LOGGER = LoggersManager.getInstance()
            .getLogger(Messages.class.getCanonicalName());

    /** bundle instance. **/
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * Ensure that no object can be create.
     */
    private Messages() {
        // nothing
    }

    /**
     * Returns the localized text of the given key if the resource is found,
     * returns !key! (key is the argument) otherwise.
     *
     * @param key
     *            string that identified a localized text
     * @return the localized text or !key! is the resource is not found
     */
    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return fallbackTranslation(key);
        }
    }

    /**
     * Returns the localized text of the given key if the resource is found or
     * throws a MissingResourceException.
     *
     * @param key
     *            string that identified a localized text
     * @return the localized text
     * @throws MissingResourceException
     *             if the resource is not found
     */
    private static String internalGetString(final String key)
            throws MissingResourceException {
        return RESOURCE_BUNDLE.getString(key);
    }

    /**
     * Returns a formatted localized string identified by the given key. The
     * string is populated according to {@link MessageFormat}.
     *
     * @param key
     *            string that identified a localized text
     * @param objects
     *            variable arguments of Object that will used to populated the
     *            string
     * @return the populated localized text, returns !key! (key is the argument)
     *         otherwise.
     */
    public static String getString(final String key, final Object... objects) {
        try {
            return new MessageFormat(internalGetString(key)).format(objects);
        } catch (MissingResourceException e) {
            return fallbackTranslation(key);
        }
    }

    /**
     * Returns the given string between "!".
     *
     * @param key
     *            a string
     * @return the given string between "!"
     */
    private static String fallbackTranslation(final String key) {
        LOGGER.warning("missing i18n key [" + key + "]");
        return '!' + key + '!';
    }
}
