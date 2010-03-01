package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.LogsView;

/**
 * @author jeremie
 * The controller of the log view
 */
public class LogsController {

	private final RemoteModel remoteModel;
	private final LogsView logsView;
	
	/**
	 * The constructor of the LogsController class
	 * @param remoteModel the model associated with this Controller
	 * @param logsView the view associated with this Controller
	 */
	public LogsController(RemoteModel remoteModel, LogsView logsView) {
		this.remoteModel = remoteModel;
		this.logsView = logsView;
		
		this.initButtonsListeners();
	}

	private void initButtonsListeners() {
		this.logsView.addClearLogsButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.getLogsListModel().clearLogsList();
			}
		});
	}	
}
