package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.LogsView;
import artnetremote.gui.views.PrefsView;

public class PrefsController {

	private final RemoteModel remoteModel;
	private final PrefsView prefsView;
	
	public PrefsController(RemoteModel remoteModel, PrefsView prefsView) {
		this.remoteModel = remoteModel;
		this.prefsView= prefsView;
		
		this.initButtonsListeners();
	}

	/**
	 * 
	 */
	private void initButtonsListeners() {
		this.prefsView.addStartButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.startArtNet();
			}
		});
		
		this.prefsView.addStopButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.stopArtNet();
			}
		});
	}	
}
