/*
 * This file is part of Elios.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * Elios is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Elios is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Elios. If not, see <http://www.gnu.org/licenses/>.
 */

package net.eliosoft.elios.gui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ListDataListener;

import net.eliosoft.elios.gui.events.ArtNetStartedEvent;
import net.eliosoft.elios.gui.events.ArtNetStoppedEvent;
import net.eliosoft.elios.gui.events.CommandLineValueChangedEvent;
import net.eliosoft.elios.gui.events.HttpStartedEvent;
import net.eliosoft.elios.gui.events.HttpStoppedEvent;
import net.eliosoft.elios.gui.listeners.RemoteModelListener;
import net.eliosoft.elios.gui.models.LocaleComboBoxModel;
import net.eliosoft.elios.gui.models.RemoteModel;

/**
 * The view of application preferences.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class PrefsView implements ViewInterface {

	private RemoteModel remoteModel;

	private final JPanel prefsPanel;

	private JButton startArtNetButton;
	private JButton stopArtNetButton;

	private JSpinner inPortSpinner;
	private JSpinner outPortSpinner;
	private JSpinner universeSpinner;
	private JSpinner subnetSpinner;

	private JComboBox broadcastAddressCombo;

	private JSpinner httpPortSpinner;

	private JCheckBox enableHttpServerCheckBox;
	private JCheckBox enableAdditiveModeCheckBox;

	private JComboBox langComboBox;

	private LocaleComboBoxModel localeModel;

	/**
	 * The Constructor of the view.
	 * 
	 * @param remoteModel
	 *            the model associated to the view
	 * @param localeModel
	 */
	public PrefsView(RemoteModel remoteModel, LocaleComboBoxModel localeModel) {
		this.remoteModel = remoteModel;
		this.localeModel = localeModel;

		prefsPanel = new JPanel(new BorderLayout());

		// general panel
		prefsPanel.add(createGeneralPane(), BorderLayout.NORTH);

		// artnet panel
		prefsPanel.add(createArtNetServerPane(), BorderLayout.CENTER);

		// http server panel
		prefsPanel.add(createHttpServerPane(), BorderLayout.SOUTH);

		// initialize the listener
		initListener();
	}

	/**
	 * Initializes all the listeners.
	 */
	private void initListener() {
		this.remoteModel
				.addRemoteModelChangedListener(new RemoteModelListener() {
					@Override
					public void commandLineValueChanged(
							CommandLineValueChangedEvent event) {
					}

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

	private JPanel createArtNetServerPane() {
		JPanel serverPrefPanel = new JPanel();
		serverPrefPanel.setName(Messages.getString("prefsview.artnetserver")); //$NON-NLS-1$
		serverPrefPanel.setLayout(new GridBagLayout());
		serverPrefPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("prefsview.artnetserver"))); //$NON-NLS-1$
		GridBagConstraints constraints = new GridBagConstraints();

		// additive
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1;
		this.enableAdditiveModeCheckBox = new JCheckBox(
				Messages.getString("prefsview.additivemode"), this.remoteModel.isAdditiveModeEnabled()); //$NON-NLS-1$
		serverPrefPanel.add(this.enableAdditiveModeCheckBox, constraints);

		// subnet/universe
		constraints.gridx = 0;
		constraints.gridy = 1;
		JLabel universeLabel = new JLabel(
				Messages.getString("prefsview.subnet")); //$NON-NLS-1$
		serverPrefPanel.add(universeLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		this.universeSpinner = new JSpinner(
				this.remoteModel.getUniverseSpinnerModel());
		serverPrefPanel.add(this.universeSpinner, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		JLabel subnetLabel = new JLabel(
				Messages.getString("prefsview.universe")); //$NON-NLS-1$
		serverPrefPanel.add(subnetLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 2;
		this.subnetSpinner = new JSpinner(
				this.remoteModel.getSubnetSpinnerModel());
		serverPrefPanel.add(this.subnetSpinner, constraints);

		// broadcast
		constraints.gridx = 0;
		constraints.gridy = 3;
		JLabel broadcastAddressLabel = new JLabel(
				Messages.getString("prefsview.broadcastaddress")); //$NON-NLS-1$
		serverPrefPanel.add(broadcastAddressLabel, constraints);
		broadcastAddressLabel.setLabelFor(this.broadcastAddressCombo);

		constraints.gridx = 1;
		constraints.gridy = 3;
		this.broadcastAddressCombo = new JComboBox(
				this.remoteModel.getBroadcastAddressComboModel());
		serverPrefPanel.add(this.broadcastAddressCombo, constraints);

		// in port
		constraints.gridx = 0;
		constraints.gridy = 4;
		JLabel inPortLabel = new JLabel(Messages.getString("prefsview.port.in")); //$NON-NLS-1$
		serverPrefPanel.add(inPortLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 4;
		this.inPortSpinner = new JSpinner(
				this.remoteModel.getInPortSpinnerModel());
		inPortLabel.setLabelFor(this.inPortSpinner);
		serverPrefPanel.add(this.inPortSpinner, constraints);

		// out port
		constraints.gridx = 0;
		constraints.gridy = 5;
		JLabel outPortLabel = new JLabel(
				Messages.getString("prefsview.port.out")); //$NON-NLS-1$
		serverPrefPanel.add(outPortLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 5;
		this.outPortSpinner = new JSpinner(
				this.remoteModel.getOutPortSpinnerModel());
		outPortLabel.setLabelFor(this.outPortSpinner);
		serverPrefPanel.add(this.outPortSpinner, constraints);

		// start / stop
		constraints.gridx = 0;
		constraints.gridy = 6;
		this.startArtNetButton = new JButton(
				Messages.getString("prefsview.start")); //$NON-NLS-1$
		serverPrefPanel.add(this.startArtNetButton, constraints);

		constraints.gridx = 1;
		constraints.gridy = 6;
		this.stopArtNetButton = new JButton(
				Messages.getString("prefsview.stop")); //$NON-NLS-1$
		this.stopArtNetButton.setEnabled(false);
		serverPrefPanel.add(this.stopArtNetButton, constraints);

		return serverPrefPanel;
	}

	/**
	 * Creates a panel for the general preferences.
	 * 
	 * @return a panel containing input elements to set general preferences
	 */
	private JPanel createGeneralPane() {
		final JPanel generalPrefsPanel = new JPanel();
		generalPrefsPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("prefsview.general"))); //$NON-NLS-1$
		generalPrefsPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1;

		this.langComboBox = new JComboBox(localeModel);

		langComboBox.setRenderer(new DefaultListCellRenderer() {
			/**
			 * serial UID.
			 */
			private static final long serialVersionUID = -8372082223569889802L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				setText(Messages
						.getString("ui.lang." + ((Locale) value).getLanguage())); //$NON-NLS-1$
				return this;
			}
		});

		// lang chooser
		constraints.gridx = 0;
		constraints.gridy = 0;
		JLabel langComboLabel = new JLabel(Messages.getString("prefsview.lang")); //$NON-NLS-1$
		generalPrefsPanel.add(langComboLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		langComboLabel.setLabelFor(this.langComboBox);
		generalPrefsPanel.add(this.langComboBox, constraints);

		return generalPrefsPanel;
	}

	/**
	 * Creates a panel for the http server preferences.
	 * 
	 * @return a panel containing input elements to set http server preferences
	 */
	private JPanel createHttpServerPane() {
		final JPanel httpServerPrefsPanel = new JPanel();
		httpServerPrefsPanel.setBorder(BorderFactory
				.createTitledBorder("HTTP Server"));
		httpServerPrefsPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1;

		// http server started
		constraints.gridx = 0;
		constraints.gridy = 0;
		this.enableHttpServerCheckBox = new JCheckBox(
				Messages.getString("prefsview.httpserver"), this.remoteModel.isHttpServerEnabled()); //$NON-NLS-1$
		httpServerPrefsPanel.add(this.enableHttpServerCheckBox, constraints);

		// http port
		constraints.gridx = 0;
		constraints.gridy = 1;
		JLabel httpPortLabel = new JLabel(
				Messages.getString("prefsview.port.http")); //$NON-NLS-1$
		httpServerPrefsPanel.add(httpPortLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		this.httpPortSpinner = new JSpinner(
				this.remoteModel.getHttpPortSpinnerModel());
		httpServerPrefsPanel.add(this.httpPortSpinner, constraints);

		return httpServerPrefsPanel;
	}

	/**
	 * Returns the preferences panel component.
	 * 
	 * @return the panel Component
	 */
	public JComponent getViewComponent() {
		return this.prefsPanel;
	}

	/**
	 * Adds an Action Listener to the Start ArtNet Button.
	 * 
	 * @param actionListener
	 *            the listener to add to the button
	 */
	public void addStartArtNetButtonListener(ActionListener actionListener) {
		this.startArtNetButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Start ArtNet Button.
	 * 
	 * @param actionListener
	 *            the listener to remove to the button
	 */
	public void removeStartArtNetButtonListener(ActionListener actionListener) {
		this.startArtNetButton.removeActionListener(actionListener);
	}

	/**
	 * Adds an Action Listener to the Stop ArtNet Button.
	 * 
	 * @param actionListener
	 *            the listener to add to the button
	 */
	public void addStopArtNetButtonListener(ActionListener actionListener) {
		this.stopArtNetButton.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Stop ArtNet Button.
	 * 
	 * @param actionListener
	 *            the listener to remove to the button
	 */
	public void removeStopArtNetButtonListener(ActionListener actionListener) {
		this.stopArtNetButton.removeActionListener(actionListener);
	}

	/**
	 * Add an Action Listener to the Enable Http server checkbox.
	 * 
	 * @param actionListener
	 *            the listener to add to the checkbox
	 */
	public void addEnableHttpServerCheckBoxListener(
			ActionListener actionListener) {
		this.enableHttpServerCheckBox.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Enable Http server checkbox.
	 * 
	 * @param actionListener
	 *            the listener to remove to the checkbox
	 */
	public void removeEnableHttpServerCheckBoxListener(
			ActionListener actionListener) {
		this.enableHttpServerCheckBox.removeActionListener(actionListener);
	}

	/**
	 * Add an Action Listener to the Enable Additive Mode checkbox.
	 * 
	 * @param actionListener
	 *            the listener to add to the checkbox
	 */
	public void addEnableAdditiveModeCheckBoxListener(
			ActionListener actionListener) {
		this.enableAdditiveModeCheckBox.addActionListener(actionListener);
	}

	/**
	 * Removes an Action Listener to the Enable Additive Mode checkbox.
	 * 
	 * @param actionListener
	 *            the listener to remove to the checkbox
	 */
	public void removeEnableAdditiveModeCheckBoxListener(
			ActionListener actionListener) {
		this.enableAdditiveModeCheckBox.removeActionListener(actionListener);
	}

	/**
	 * Add a {@link ListDataListener} to the lang combobox.
	 * 
	 * @param l
	 *            the listener to add to the lang combobox
	 */
	public void addLangComboListener(ActionListener l) {
		this.langComboBox.addActionListener(l);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
		return Messages.getString("prefsview.title"); //$NON-NLS-1$
	}
}
