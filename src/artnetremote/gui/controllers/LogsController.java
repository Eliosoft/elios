package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.LogsView;

public class LogsController {

	private final RemoteModel remoteModel;
	private final LogsView logsView;
	
	public LogsController(RemoteModel remoteModel, LogsView logsView) {
		this.remoteModel = remoteModel;
		this.logsView = logsView;
		
		this.initButtonsListeners();
	}

	/**
	 * 
	 */
	private void initButtonsListeners() {
		this.logsView.addClearLogsButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.getLogsListModel().clearLogsList();
			}
		});
	}	
}
