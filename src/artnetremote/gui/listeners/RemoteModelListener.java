package artnetremote.gui.listeners;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;

public interface RemoteModelListener {	
	void commandLineValueChanged(CommandLineValueChangedEvent event);
	void artNetStarted(ArtNetStartedEvent event);
	void artNetStopped(ArtNetStoppedEvent event);
}
