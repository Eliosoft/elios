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

package net.eliosoft.elios.server.events;

/**
 * Event fired when additive mode status has changed.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class AdditiveModeValueChangedEvent {
    private boolean additiveModeEnabled;

    /**
     * Constructor method to instantiate a new event.
     * 
     * @param additiveModeEnabled
     *            the new additive mode status value
     */
    public AdditiveModeValueChangedEvent(final boolean additiveModeEnabled) {
        this.additiveModeEnabled = additiveModeEnabled;
    }

    /**
     * Returns the additive mode status value of the event.
     * 
     * @return the new additive mode status value
     */
    public boolean isAdditiveModeEnabled() {
        return additiveModeEnabled;
    }
}
