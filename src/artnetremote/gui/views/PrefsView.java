package artnetremote.gui.views;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

public class PrefsView {

	private RemoteModel remoteModel;

	private final JPanel prefsPanel = new JPanel();

	private final JButton startButton;
	private final JButton stopButton;

	public PrefsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		
		JPanel artnetServer = new JPanel();
		artnetServer.setName("ArtNet Server");
		TitledBorder border = BorderFactory.createTitledBorder(artnetServer.getName());
		border.setTitleJustification(TitledBorder.CENTER);
		artnetServer.setBorder(border);
		
		this.prefsPanel.add(artnetServer);
		this.startButton = new JButton("Start");
		artnetServer.add(this.startButton);
		
		this.stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false);
		artnetServer.add(this.stopButton);
		
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {}
			
			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
			
			@Override
			public void artNetStarted(ArtNetStartedEvent event) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		});
	}

	public JPanel getPrefsPanel() {
		return this.prefsPanel;
	}

	public void addStartButtonListener(ActionListener actionListener) {
		this.startButton.addActionListener(actionListener);
	}
	
	public void removeStartButtonListener(ActionListener actionListener) {
		this.startButton.removeActionListener(actionListener);
	}
	
	public void addStopButtonListener(ActionListener actionListener) {
		this.stopButton.addActionListener(actionListener);
	}
	
	public void removeStopButtonListener(ActionListener actionListener) {
		this.stopButton.removeActionListener(actionListener);
	}
}
