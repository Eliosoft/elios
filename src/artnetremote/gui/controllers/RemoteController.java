package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.RemoteView;

public class RemoteController {

	private final RemoteModel remoteModel;
	private final RemoteView remoteView;
	
	public RemoteController(RemoteModel remoteModel, RemoteView remoteView) {
		this.remoteModel = remoteModel;
		this.remoteView = remoteView;
		
		this.initButtonsListeners();
	}

	/**
	 * 
	 */
	private void initButtonsListeners() {
		this.remoteView.addValueButtonsListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				String buttonText = ((JButton) e.getSource()).getText();
				remoteModel.addToCommandLine(buttonText);
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
