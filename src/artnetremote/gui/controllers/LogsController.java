/*
 * This file is part of ArtNet-Remote.
 * 
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 * 
 * ArtNet-Remote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArtNet-Remote is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArtNet-Remote. If not, see <http://www.gnu.org/licenses/>.
 */

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
