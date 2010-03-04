/*
 * This file is part of ArtNet-Remote.
 * 
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 * 
 * ArtNet-Remote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArtNet-Remote is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArtNet-Remote. If not, see <http://www.gnu.org/licenses/>.
 */

package artnetremote.gui.events;

/**
 * @author jeremie
 * Event fired when CommandLine value has changed
 */
public class CommandLineValueChangedEvent {

	private String command;
	
	/**
	 * Constructor method to instantiate a new event
	 * @param command the new command line value
	 */
	public CommandLineValueChangedEvent(String command) {
		this.command = command;
	}
	
	/**
	 * Get the Command Line value of the event
	 * @return the new CommandLine value
	 */
	public String getCommand() {
		return command;
	}
}
