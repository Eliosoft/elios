package artnetremote.gui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

/**
 * @author jeremie
 * The view of application preferences
 */
public class PrefsView {

	private RemoteModel remoteModel;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel prefsPanel = new JPanel();

	private final JButton startButton;
	private final JButton stopButton;
	
	private final JSpinner inPortSpinner;
	private final JSpinner outPortSpinner;

	/**
	 * the Constructor of the view
	 * @param remoteModel the model associated to the view
	 */
	public PrefsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		
		JPanel artnetServer = new JPanel();
		artnetServer.setName("ArtNet Server");
		TitledBorder border = BorderFactory.createTitledBorder(artnetServer.getName());
		border.setTitleJustification(TitledBorder.CENTER);
		artnetServer.setBorder(border);
		artnetServer.setLayout(layout);
		
		this.prefsPanel.add(artnetServer);
		
		constraints.gridy = 0;
		this.inPortSpinner = new JSpinner(this.remoteModel.getInPortSpinnerModel());
		JLabel inPortLabel = new JLabel("In Port");
		artnetServer.add(inPortLabel,constraints);
		inPortLabel.setLabelFor(this.inPortSpinner);
		artnetServer.add(this.inPortSpinner,constraints);

		constraints.gridy = 1;
		this.outPortSpinner = new JSpinner(this.remoteModel.getOutPortSpinnerModel());
		JLabel outPortLabel = new JLabel("Out Port");
		artnetServer.add(outPortLabel,constraints);
		outPortLabel.setLabelFor(this.outPortSpinner);
		artnetServer.add(this.outPortSpinner,constraints);
		
		constraints.gridy = 2;
		this.startButton = new JButton("Start");
		artnetServer.add(this.startButton,constraints);
		
		this.stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false);
		artnetServer.add(this.stopButton,constraints);
		
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {}
			
			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				inPortSpinner.setEnabled(true);
				outPortSpinner.setEnabled(true);
			}
			
			@Override
			public void artNetStarted(ArtNetStartedEvent event) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				inPortSpinner.setEnabled(false);
				outPortSpinner.setEnabled(false);
			}
		});
	}

	/**
	 * get the preferences panel component
	 * @return the panel Component
	 */
	public JPanel getPrefsPanel() {
		return this.prefsPanel;
	}

	/**
	 * add an Action Listener to the Start Button
	 * @param actionListener the listener to add to the button
	 */
	public void addStartButtonListener(ActionListener actionListener) {
		this.startButton.addActionListener(actionListener);
	}
	
	/**
	 * remove an Action Listener to the Start Button
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStartButtonListener(ActionListener actionListener) {
		this.startButton.removeActionListener(actionListener);
	}
	
	/**
	 * add an Action Listener to the Stop Button
	 * @param actionListener the listener to add to the button
	 */
	public void addStopButtonListener(ActionListener actionListener) {
		this.stopButton.addActionListener(actionListener);
	}

	/**
	 * remove an Action Listener to the Stop Button
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStopButtonListener(ActionListener actionListener) {
		this.stopButton.removeActionListener(actionListener);
	}
}
