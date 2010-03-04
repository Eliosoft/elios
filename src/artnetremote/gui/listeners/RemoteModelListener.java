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

package artnetremote.gui.listeners;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;

/**
 * @author jeremie
 * This interface describes the methods that must be implemented by classes which want to be a listener of the remote model
 */
public interface RemoteModelListener {	
	/**
	 * this method is called when the value of the command line has changed
	 * @param event the event corresponding to the change of the command line
	 */
	void commandLineValueChanged(CommandLineValueChangedEvent event);
	
	
	/**
	 * this method is called when the ArtNet Server is started
	 * @param event the event corresponding to the start of the server
	 */
	void artNetStarted(ArtNetStartedEvent event);
	
	/**
	 * this method is called when the ArtNet Server is stopped
	 * @param event the event corresponding to the stop of the server
	 */
	void artNetStopped(ArtNetStoppedEvent event);
}
