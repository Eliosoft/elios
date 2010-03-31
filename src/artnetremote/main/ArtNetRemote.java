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

package artnetremote.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import artnetremote.gui.controllers.LogsController;
import artnetremote.gui.controllers.PrefsController;
import artnetremote.gui.controllers.RemoteController;
import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.LogsLineView;
import artnetremote.gui.views.LogsView;
import artnetremote.gui.views.PrefsView;
import artnetremote.gui.views.RemoteView;

/**
 * Main Class of ArtNet Remote Software contains the main method
 * used to launch the application.
 *
 * @author Jeremie GASTON-RAOUL
 */
public final class ArtNetRemote {

	/**
	 * Do nothing more than ensure that no object
	 * can be construct.
	 */
	private ArtNetRemote() {
		// nothing
	}

	/**
	 * Launches ArtNet-Remote.
	 * @param args command-line argument. Currently unused !
	 */
	public static void main(String[] args) {
		RemoteModel remoteModel = new RemoteModel();
		final RemoteView remoteView = new RemoteView(remoteModel);
		//used to make relation between view and model
		new RemoteController(remoteModel, remoteView);

		PrefsView prefsView = new PrefsView(remoteModel);
		//used to make relation between view and model
		new PrefsController(remoteModel, prefsView);

		LogsView logsView = new LogsView(remoteModel);
		//used to make relation between view and model
		new LogsController(remoteModel, logsView);

		LogsLineView logsLineView = new LogsLineView(remoteModel);
		
		for(Logger l : LoggersManager.getInstance().getLoggersList()){
			remoteModel.getLogsListModel().addLogger(l);
		}
		
		JFrame frame = new JFrame("ArtNet Remote");
		final JTabbedPane tabbedPane = new JTabbedPane();
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(logsLineView.getLogField(), BorderLayout.SOUTH);
		tabbedPane.addTab("remote", remoteView.getRemotePanel());
		tabbedPane.addTab("prefs", prefsView.getPrefsPanel());
		tabbedPane.addTab("logs", logsView.getLogsPanel());

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
		tabbedPane.getSelectedComponent().requestFocusInWindow();
			}
		});
		tabbedPane.setSelectedIndex(0);

		frame.pack();
		frame.setVisible(true);

		tabbedPane.getSelectedComponent().requestFocusInWindow();
	}
}
