package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.RemoteView;

/**
 * @author jeremie
 * The controller of the remote view
 */
public class RemoteController {

	private final RemoteModel remoteModel;
	private final RemoteView remoteView;
	
	/**
	 * The default constructor for the remote controller
	 * @param remoteModel
	 * @param remoteView
	 */
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
			this.initValueKeyStroke(inputMap, actionMap, c);
		}
		
		inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "backspace");
		actionMap.put("backspace", new AbstractAction() {

			private static final long serialVersionUID = -4335381990869042671L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(remoteView.isDelButtonEnabled())
					remoteModel.delLastCommandLineChar();
			}
		});
		
		inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
		actionMap.put("enter", new AbstractAction() {

			private static final long serialVersionUID = -1152279417262923317L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(remoteView.isEnterButtonEnabled())
					remoteModel.processCommandLine();
			}
		});
		
		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		actionMap.put("escape", new AbstractAction() {
			
			private static final long serialVersionUID = -8772359436304401320L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(remoteView.isResetButtonEnabled())
					remoteModel.resetCommandLine();
			}
		});
	}

	private void initValueKeyStroke(InputMap inputMap,ActionMap actionMap, final Character c){
		inputMap.put(KeyStroke.getKeyStroke(c), c);
		actionMap.put(c, new AbstractAction() {

			private static final long serialVersionUID = 6382574213528201626L;

			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.addToCommandLine(c);
			}
		});
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
