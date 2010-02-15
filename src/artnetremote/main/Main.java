package artnetremote.main;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import artnetremote.gui.controllers.LogsController;
import artnetremote.gui.controllers.PrefsController;
import artnetremote.gui.controllers.RemoteController;
import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.LogsView;
import artnetremote.gui.views.PrefsView;
import artnetremote.gui.views.RemoteView;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteModel remoteModel = new RemoteModel();
		RemoteView remoteView = new RemoteView(remoteModel);
		RemoteController remoteController = new RemoteController(remoteModel,remoteView);
		
		PrefsView prefsView = new PrefsView(remoteModel);
		PrefsController prefsController = new PrefsController(remoteModel, prefsView);
		
		LogsView logsView = new LogsView(remoteModel);
		LogsController logsController = new LogsController(remoteModel, logsView);
		
		JFrame frame = new JFrame("ArtNet Remote");
		JTabbedPane tabbedPane = new JTabbedPane();
		frame.setContentPane(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabbedPane.addTab("remote",remoteView.getRemotePanel());
		tabbedPane.addTab("prefs",prefsView.getPrefsPanel());
		tabbedPane.addTab("logs",logsView.getLogsPanel());
		
		frame.pack();
		frame.setVisible(true);
	}



}
