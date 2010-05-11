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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import artnetremote.gui.controllers.LogsController;
import artnetremote.gui.controllers.PrefsController;
import artnetremote.gui.controllers.RemoteController;
import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.models.RemoteModel.BroadCastAddress;
import artnetremote.gui.views.AboutView;
import artnetremote.gui.views.LogsLineView;
import artnetremote.gui.views.LogsView;
import artnetremote.gui.views.PrefsView;
import artnetremote.gui.views.RemoteView;
import artnetremote.server.ArtNetServerManager;
import artnetremote.server.HttpServerManager;

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

		final Preferences prefs = Preferences.userNodeForPackage(ArtNetRemote.class);
        final RemoteModel remoteModel = createRemoteModel(prefs);
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
		AboutView aboutView = new AboutView();
		
		for(Logger l : LoggersManager.getInstance().getLoggersList()){
			remoteModel.getLogsListModel().addLogger(l);
		}

		JFrame frame = new JFrame("ArtNet Remote");
		final JTabbedPane tabbedPane = new JTabbedPane();
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(ArtNetRemote.class.getResource("/artnetremote/server/handler/files/favicon.ico")).getImage());

		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(logsLineView.getViewComponent(), BorderLayout.SOUTH);
		tabbedPane.addTab("remote", remoteView.getViewComponent());
		tabbedPane.addTab("prefs", prefsView.getViewComponent());
		tabbedPane.addTab("logs", logsView.getViewComponent());
		tabbedPane.addTab("about", aboutView.getViewComponent());

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
		tabbedPane.getSelectedComponent().requestFocusInWindow();
			}
		});
		tabbedPane.setSelectedIndex(0);

		frame.addWindowListener(new WindowAdapter() {
            /**
             *
		     */
            @Override
            public void windowClosing(WindowEvent e) {
                persistRemoteModel(remoteModel, prefs);
            }
		});

		frame.pack();
		frame.setVisible(true);

		tabbedPane.getSelectedComponent().requestFocusInWindow();
	}

	/**
	 * Create a <code>RemoteModel</code> and retrieve the configuration
	 * from the given <code>Preferences</code>.
	 * If any configuration is found for a specific parameter, the default
	 * value is used according to <code>RemoteModel</code>.
	 *
	 * @param prefs <code>Preferences</code> used to retrieve the configuration
	 * @return a configured <code>RemoteModel</code>
	 */
	public static RemoteModel createRemoteModel(Preferences prefs) {
	    RemoteModel model = new RemoteModel();
	    model.setSubnet(prefs.getInt("server.subnet", 0));
        model.setUniverse(prefs.getInt("server.universe", 0));
        model.setBroadCastAddress(BroadCastAddress.valueOf(
                BroadCastAddress.class,
                prefs.get("server.broadcast.address",
                BroadCastAddress.PRIMARY.name())));

	    model.setInPort(prefs.getInt("server.inport",
	            ArtNetServerManager.DEFAULT_ARTNET_PORT));
        model.setOutputPort(prefs.getInt("server.outport",
                ArtNetServerManager.DEFAULT_ARTNET_PORT));

        model.setHttpServerEnabled(
                prefs.getBoolean("server.httpserver.enable", false));
        model.setHttpPort(
                prefs.getInt("server.httpserver.port",
                HttpServerManager.DEFAULT_HTTP_PORT));

        return model;
	}

	/**
	 * Persits a remote model to the given <code>Preferences</code>.
	 *
	 * @param model the <code>RemoteModel</code> to store
	 * @param prefs the <code>Preferences</code> used to persist
	 */
	public static void persistRemoteModel(RemoteModel model, Preferences prefs) {
	    prefs.putInt("server.subnet", model.getSubnet());
	    prefs.putInt("server.universe", model.getUniverse());
	    prefs.put("server.broadcast.address",
	            model.getBroadCastAddress().name());

	    prefs.putInt("server.inport", model.getInPort());
        prefs.putInt("server.outport", model.getOutPort());

        prefs.putBoolean("server.httpserver.enable",
                model.isHttpServerEnabled());
        prefs.putInt("server.httpserver.port",
                model.getHttpPort());
	}
}
