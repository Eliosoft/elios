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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.events.HttpStartedEvent;
import artnetremote.gui.events.HttpStoppedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

/**
 * The view of application preferences.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class PrefsView implements ViewInterface {

	private RemoteModel remoteModel;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel prefsPanel = new JPanel();

	private final JButton startArtNetButton;
	private final JButton stopArtNetButton;
	
	private final JSpinner inPortSpinner;
	private final JSpinner outPortSpinner;
	private final JSpinner universeSpinner;
	private final JSpinner subnetSpinner;
	
	private JComboBox broadcastAddressCombo;

	private final JSpinner httpPortSpinner;

	private final JCheckBox enableHttpServerCheckBox;


	/**
	 * The Constructor of the view.
	 * @param remoteModel the model associated to the view
	 */
	public PrefsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;

		JPanel artNetServer = new JPanel();
		artNetServer.setName(Messages.getString("prefsview.artnetserver")); //$NON-NLS-1$
		TitledBorder artNetServerBorder = BorderFactory.createTitledBorder(artNetServer.getName());
		artNetServerBorder.setTitleJustification(TitledBorder.CENTER);
		artNetServer.setBorder(artNetServerBorder);
		artNetServer.setLayout(layout);

		this.prefsPanel.add(artNetServer);

		constraints.gridwidth = 2;		
		constraints.gridy = 0;
		this.universeSpinner = new JSpinner(this.remoteModel.getUniverseSpinnerModel());
		this.subnetSpinner = new JSpinner(this.remoteModel.getSubnetSpinnerModel());
		JLabel subnetUniverseLabel = new JLabel(Messages.getString("prefsview.subnetuniverse")); //$NON-NLS-1$
		artNetServer.add(subnetUniverseLabel, constraints);
		constraints.gridwidth = 1;
		constraints.gridy = 1;
		artNetServer.add(this.subnetSpinner, constraints);
		artNetServer.add(this.universeSpinner, constraints);

		
		constraints.gridwidth = 2;
		constraints.gridy = 2;
		this.broadcastAddressCombo = new JComboBox(this.remoteModel.getBroadcastAddressComboModel());
		JLabel broadcastAddressLabel = new JLabel(Messages.getString("prefsview.broadcastaddress")); //$NON-NLS-1$
		artNetServer.add(broadcastAddressLabel, constraints);
		broadcastAddressLabel.setLabelFor(this.broadcastAddressCombo);
		constraints.gridy = 3;
		artNetServer.add(this.broadcastAddressCombo,constraints);
		
		constraints.gridwidth = 1;		
		constraints.gridy = 4;
		this.inPortSpinner = new JSpinner(this.remoteModel.getInPortSpinnerModel());
		JLabel inPortLabel = new JLabel(Messages.getString("prefsview.port.in")); //$NON-NLS-1$
		artNetServer.add(inPortLabel, constraints);
		inPortLabel.setLabelFor(this.inPortSpinner);
		artNetServer.add(this.inPortSpinner, constraints);

		constraints.gridy = 5;
		this.outPortSpinner = new JSpinner(this.remoteModel.getOutPortSpinnerModel());
		JLabel outPortLabel = new JLabel(Messages.getString("prefsview.port.out")); //$NON-NLS-1$
		artNetServer.add(outPortLabel, constraints);
		outPortLabel.setLabelFor(this.outPortSpinner);
		artNetServer.add(this.outPortSpinner, constraints);
		
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		this.enableHttpServerCheckBox = new JCheckBox(Messages.getString("prefsview.httpserver"), this.remoteModel.isHttpServerEnabled());
		artNetServer.add(this.enableHttpServerCheckBox, constraints);
		
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		this.httpPortSpinner = new JSpinner(this.remoteModel.getHttpPortSpinnerModel());
		JLabel httpPortLabel = new JLabel(Messages.getString("prefsview.port.http")); //$NON-NLS-1$
		artNetServer.add(httpPortLabel, constraints);
		outPortLabel.setLabelFor(this.httpPortSpinner);
		artNetServer.add(this.httpPortSpinner, constraints);


		constraints.gridy = 8;
		constraints.gridwidth = 1;		
		this.startArtNetButton = new JButton(Messages.getString("prefsview.start")); //$NON-NLS-1$
		artNetServer.add(this.startArtNetButton, constraints);
		this.stopArtNetButton = new JButton(Messages.getString("prefsview.stop")); //$NON-NLS-1$
		this.stopArtNetButton.setEnabled(false);
		artNetServer.add(this.stopArtNetButton, constraints);
		
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) { }

			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {
				startArtNetButton.setEnabled(true);
				stopArtNetButton.setEnabled(false);
				inPortSpinner.setEnabled(true);
				outPortSpinner.setEnabled(true);

				enableHttpServerCheckBox.setEnabled(true);
				httpPortSpinner.setEnabled(true);				
			}

			@Override
			public void artNetStarted(ArtNetStartedEvent event) {
				startArtNetButton.setEnabled(false);
				stopArtNetButton.setEnabled(true);
				inPortSpinner.setEnabled(false);
				outPortSpinner.setEnabled(false);

				enableHttpServerCheckBox.setEnabled(false);
				httpPortSpinner.setEnabled(false);
			}

			@Override
			public void httpStopped(HttpStoppedEvent event) {
			}

			@Override
			public void httpStarted(HttpStartedEvent event) {
			}
		});
	}

	/**
	 * Returns the preferences panel component.
	 * @return the panel Component
	 */
	public JComponent getViewComponent() {
		return this.prefsPanel;
	}

	/**
	 * Adds an Action Listener to the Start ArtNet Button.
	 * @param actionListener the listener to add to the button
	 */
	public void addStartArtNetButtonListener(ActionListener actionListener) {
		this.startArtNetButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Start ArtNet Button.
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStartArtNetButtonListener(ActionListener actionListener) {
		this.startArtNetButton.removeActionListener(actionListener);
	}

	/**
	 * Adds an Action Listener to the Stop ArtNet Button.
	 * @param actionListener the listener to add to the button
	 */
	public void addStopArtNetButtonListener(ActionListener actionListener) {
		this.stopArtNetButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Stop ArtNet Button.
	 * @param actionListener the listener to remove to the button
	 */
	public void removeStopArtNetButtonListener(ActionListener actionListener) {
		this.stopArtNetButton.removeActionListener(actionListener);
	}
	
	/**
	 * Add an Action Listener to the Enable Http server checkbox.
	 * @param actionListener the listener to add to the checkbox
	 */
	public void addEnableHttpServerCheckBoxListener(ActionListener actionListener){
		this.enableHttpServerCheckBox.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Enable Http server checkbox.
	 * @param actionListener the listener to remove to the checkbox
	 */
	public void removeEnableHttpServerCheckBoxListener(ActionListener actionListener){
		this.enableHttpServerCheckBox.removeActionListener(actionListener);
	}
}
