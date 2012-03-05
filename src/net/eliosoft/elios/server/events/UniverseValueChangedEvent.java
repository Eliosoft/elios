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
 * Event fired when universe value has changed.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class UniverseValueChangedEvent {
    private int universe;

    /**
     * Constructor method to instantiate a new event.
     * 
     * @param universe
     *            the new universe value
     */
    public UniverseValueChangedEvent(int universe) {
	this.universe = universe;
    }

    /**
     * Returns the universe value of the event.
     * 
     * @return the new universe value
     */
    public int getUniverse() {
	return universe;
    }
}
