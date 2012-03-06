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
 * Event fired when a cue has been added.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class CueRemovedEvent {
    private String cueName;

    /**
     * Constructor method to instantiate a new event.
     * 
     * @param cueName
     *            the name of the removed cue in the cuesList
     */
    public CueRemovedEvent(final String cueName) {
	this.cueName = cueName;
    }

    /**
     * Returns the name of the removed cue.
     * 
     * @return the cue name
     */
    public String getCueName() {
	return cueName;
    }

}
