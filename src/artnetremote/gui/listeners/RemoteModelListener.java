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
