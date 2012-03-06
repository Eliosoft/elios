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

package net.eliosoft.elios.gui.events;

/**
 * Event fired when CommandLine value has changed.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class CommandLineValueChangedEvent {

    private String command;

    /**
     * Constructor method to instantiate a new event.
     * 
     * @param command
     *            the new command line value
     */
    public CommandLineValueChangedEvent(final String command) {
	this.command = command;
    }

    /**
     * Returns the Command Line value of the event.
     * 
     * @return the new CommandLine value
     */
    public String getCommand() {
	return command;
    }
}
