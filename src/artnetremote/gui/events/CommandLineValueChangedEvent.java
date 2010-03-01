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
