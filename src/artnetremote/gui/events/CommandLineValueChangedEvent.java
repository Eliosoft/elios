package artnetremote.gui.events;

public class CommandLineValueChangedEvent {

	private String command;
	
	public CommandLineValueChangedEvent(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
}
