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
 * The view of application preferences.
 *
 * @author Jeremie GASTON-RAOUL
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
	 * The Constructor of the view.
	 * @param remoteModel the model associated to the view
	 */
	public PrefsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;

		JPanel artnetServer = new JPanel();
		artnetServer.setName(Messages.getString("prefsview.artnetserver")); //$NON-NLS-1$
		TitledBorder border = BorderFactory.createTitledBorder(artnetServer.getName());
		border.setTitleJustification(TitledBorder.CENTER);
		artnetServer.setBorder(border);
		artnetServer.setLayout(layout);

		this.prefsPanel.add(artnetServer);

		constraints.gridy = 0;
		this.inPortSpinner = new JSpinner(this.remoteModel.getInPortSpinnerModel());
		JLabel inPortLabel = new JLabel(Messages.getString("prefsview.port.in")); //$NON-NLS-1$
		artnetServer.add(inPortLabel, constraints);
		inPortLabel.setLabelFor(this.inPortSpinner);
		artnetServer.add(this.inPortSpinner, constraints);

		constraints.gridy = 1;
		this.outPortSpinner = new JSpinner(this.remoteModel.getOutPortSpinnerModel());
		JLabel outPortLabel = new JLabel(Messages.getString("prefsview.port.out")); //$NON-NLS-1$
		artnetServer.add(outPortLabel, constraints);
		outPortLabel.setLabelFor(this.outPortSpinner);
		artnetServer.add(this.outPortSpinner, constraints);

		constraints.gridy = 2;
		this.startButton = new JButton(Messages.getString("prefsview.start")); //$NON-NLS-1$
		artnetServer.add(this.startButton, constraints);

		this.stopButton = new JButton(Messages.getString("prefsview.stop")); //$NON-NLS-1$
		this.stopButton.setEnabled(false);
		artnetServer.add(this.stopButton, constraints);

		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) { }

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
	 * Returns the preferences panel component.
	 * @return the panel Component
	 */
	public JPanel getPrefsPanel() {
		return this.prefsPanel;
	}

	/**
	 * Adds an Action Listener to the Start Button.
	 * @param actionListener the listener to add to the button
	 */
	public void addStartButtonListener(ActionListener actionListener) {
		this.startButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Start Button.
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStartButtonListener(ActionListener actionListener) {
		this.startButton.removeActionListener(actionListener);
	}

	/**
	 * Adds an Action Listener to the Stop Button.
	 * @param actionListener the listener to add to the button
	 */
	public void addStopButtonListener(ActionListener actionListener) {
		this.stopButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Stop Button.
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStopButtonListener(ActionListener actionListener) {
		this.stopButton.removeActionListener(actionListener);
	}
}
