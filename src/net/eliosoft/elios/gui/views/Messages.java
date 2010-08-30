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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class provides i18n functionalities.
 *
 * @author Alexandre COLLIGNON
 */
public final class Messages {
	/** bundle full qualified name. **/
	private static final String BUNDLE_NAME = "net.eliosoft.elios.gui.views.messages"; //$NON-NLS-1$

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
