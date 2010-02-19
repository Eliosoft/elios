package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import artnetremote.gui.actions.ValueKeyTypedAction;
import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.RemoteView;

public class RemoteController {

	private final RemoteModel remoteModel;
	private final RemoteView remoteView;
	
	public RemoteController(RemoteModel remoteModel, RemoteView remoteView) {
		this.remoteModel = remoteModel;
		this.remoteView = remoteView;
		
		this.initButtonsListeners();
		this.initKeyStrokes();
	}

	private void initKeyStrokes() {
		InputMap inputMap = this.remoteView.getRemotePanelInputMap();
		ActionMap actionMap = this.remoteView.getRemotePanelActionMap();
		for(Character c : this.remoteView.getValuesList()){
			this.initKeyStroke(inputMap, actionMap, c);
		}
	}

	private void initKeyStroke(InputMap inputMap,ActionMap actionMap, Character c){
		inputMap.put(KeyStroke.getKeyStroke(c), c);
		actionMap.put(c, new ValueKeyTypedAction(this.remoteModel, c));
	}
	
	private void initButtonsListeners() {
		this.remoteView.addValueButtonsListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				String buttonText = ((JButton) e.getSource()).getText();
				remoteModel.addToCommandLine(buttonText.charAt(0));
			}
		});
		this.remoteView.addDelButtonListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.delLastCommandLineChar();
			}
		});
		this.remoteView.addEnterButtonListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.processCommandLine();
			}
		});
		this.remoteView.addResetButtonListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.resetCommandLine();
			}
		});
	}	
}
