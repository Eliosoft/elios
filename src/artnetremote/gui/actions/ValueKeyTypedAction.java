package artnetremote.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import artnetremote.gui.models.RemoteModel;

public class ValueKeyTypedAction extends AbstractAction {

	private RemoteModel remoteModel;
	private Character character;
	
	public ValueKeyTypedAction(RemoteModel remoteModel, Character character) {
		this.remoteModel = remoteModel;
		this.character = character;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.remoteModel.addToCommandLine(this.character);
	}

}
