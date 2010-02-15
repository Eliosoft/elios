package artnetremote.gui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

public class PrefsView {

	private RemoteModel remoteModel;
	
	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel prefsPanel = new JPanel();

	private final JButton startButton;
	private final JButton stopButton;

	public PrefsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.prefsPanel.setLayout(this.layout);
		
		this.startButton = new JButton("Start");
		this.prefsPanel.add(this.startButton, this.constraints);
		
		this.stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false);
		this.prefsPanel.add(this.stopButton, this.constraints);
		
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
