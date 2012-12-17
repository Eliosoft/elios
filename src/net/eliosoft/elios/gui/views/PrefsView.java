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
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.eliosoft.elios.gui.models.LocaleComboBoxModel;
import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.models.UpdateModel;
import net.eliosoft.elios.main.Elios;

/**
 * The view of application preferences.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class PrefsView implements ViewInterface {

    private RemoteModel remoteModel;

    private final JPanel prefsPanel;

    private JButton cancelButton;
    private JButton saveButton;

    private JSpinner inPortSpinner;
    private JSpinner outPortSpinner;

    private JComboBox broadcastAddressCombo;

    private JSpinner httpPortSpinner;

    private JCheckBox enableHttpServerCheckBox;

    private JComboBox langComboBox;

    private LocaleComboBoxModel localeModel;

    private UpdateModel updateModel;

    /**
     * The Constructor of the view.
     *
     * @param remoteModel
     *            the model associated to the view
     * @param localeModel
     *            the associated {@link LocaleComboBoxModel}
     * @param updateModel
     *            the associated {@link UpdateModel}
     */
    public PrefsView(final RemoteModel remoteModel,
            final LocaleComboBoxModel localeModel, final UpdateModel updateModel) {
        this.remoteModel = remoteModel;
        this.localeModel = localeModel;
        this.updateModel = updateModel;

        prefsPanel = new JPanel();
        prefsPanel.setLayout(new BorderLayout());

        JPanel globalPane = new JPanel(new GridLayout(4, 0));

        // general panel
        globalPane.add(createGeneralPane());

        // artnet panel
        globalPane.add(createArtNetServerPane());

        // http server panel
        globalPane.add(createHttpServerPane());

        // add a glue to have a so beautiful fullscreen layout
        globalPane.add(Box.createVerticalGlue());

        prefsPanel.add(globalPane, BorderLayout.NORTH);

        // button panel
        prefsPanel.add(createButtonPane(), BorderLayout.SOUTH);
    }

    private Component createButtonPane() {

        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(Box.createGlue());

        // cancel / save
        this.cancelButton = new JButton(Messages.getString("prefsview.cancel")); //$NON-NLS-1$
        this.cancelButton.setIcon(new ImageIcon(Elios.class
                .getResource("/net/eliosoft/elios/gui/views/edit-undo.png")));
        buttonPanel.add(this.cancelButton);

        buttonPanel.add(Box.createHorizontalStrut(5));

        this.saveButton = new JButton(Messages.getString("prefsview.save")); //$NON-NLS-1$
        buttonPanel.add(this.saveButton);
        this.saveButton
                .setIcon(new ImageIcon(
                        Elios.class
                                .getResource("/net/eliosoft/elios/gui/views/document-save.png")));

        return buttonPanel;
    }

    private JPanel createArtNetServerPane() {
        JPanel serverPrefPanel = new JPanel();
        serverPrefPanel.setName(Messages.getString("prefsview.artnetserver")); //$NON-NLS-1$
        serverPrefPanel.setLayout(new GridBagLayout());
        serverPrefPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("prefsview.artnetserver"))); //$NON-NLS-1$
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;

        // broadcast
        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel broadcastAddressLabel = new JLabel(
                Messages.getString("prefsview.broadcastaddress")); //$NON-NLS-1$
        serverPrefPanel.add(broadcastAddressLabel, constraints);
        broadcastAddressLabel.setLabelFor(this.broadcastAddressCombo);

        constraints.gridx = 1;
        constraints.gridy = 0;
        this.broadcastAddressCombo = new JComboBox(
                this.remoteModel.getBroadcastAddressComboModel());
        serverPrefPanel.add(this.broadcastAddressCombo, constraints);

        // in port
        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel inPortLabel = new JLabel(Messages.getString("prefsview.port.in")); //$NON-NLS-1$
        serverPrefPanel.add(inPortLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        this.inPortSpinner = new JSpinner(
                this.remoteModel.getInPortSpinnerModel());
        inPortLabel.setLabelFor(this.inPortSpinner);
        serverPrefPanel.add(this.inPortSpinner, constraints);

        // out port
        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel outPortLabel = new JLabel(
                Messages.getString("prefsview.port.out")); //$NON-NLS-1$
        serverPrefPanel.add(outPortLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        this.outPortSpinner = new JSpinner(
                this.remoteModel.getOutPortSpinnerModel());
        outPortLabel.setLabelFor(this.outPortSpinner);
        serverPrefPanel.add(this.outPortSpinner, constraints);

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
            public Component getListCellRendererComponent(final JList list,
                    final Object value, final int index,
                    final boolean isSelected, final boolean cellHasFocus) {

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

        // update chooser
        UpdateFrequencyChooserView view = new UpdateFrequencyChooserView(
                updateModel);
        JComponent viewComponent = view.getViewComponent();

        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel updateViewLabel = new JLabel(view.getLocalizedTitle());
        generalPrefsPanel.add(updateViewLabel, constraints);
        langComboLabel.setLabelFor(viewComponent);
        constraints.gridx = 1;
        constraints.gridy = 1;

        generalPrefsPanel.add(viewComponent, constraints);

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
    @Override
    public JComponent getViewComponent() {
        return this.prefsPanel;
    }

    /**
     * Adds an Action Listener to the Start ArtNet Button.
     *
     * @param actionListener
     *            the listener to add to the button
     */
    public void addCancelButtonListener(final ActionListener actionListener) {
        this.cancelButton.addActionListener(actionListener);
    }

    /**
     * Removes an Action Listener to the Start ArtNet Button.
     *
     * @param actionListener
     *            the listener to remove to the button
     */
    public void removeStartArtNetButtonListener(
            final ActionListener actionListener) {
        this.cancelButton.removeActionListener(actionListener);
    }

    /**
     * Adds an Action Listener to the Stop ArtNet Button.
     *
     * @param actionListener
     *            the listener to add to the button
     */
    public void addSaveButtonListener(final ActionListener actionListener) {
        this.saveButton.addActionListener(actionListener);
    }

    /**
     * Removes an Action Listener to the Stop ArtNet Button.
     *
     * @param actionListener
     *            the listener to remove to the button
     */
    public void removeStopArtNetButtonListener(
            final ActionListener actionListener) {
        this.saveButton.removeActionListener(actionListener);
    }

    /**
     * Add an Action Listener to the Enable Http server checkbox.
     *
     * @param actionListener
     *            the listener to add to the checkbox
     */
    public void addEnableHttpServerCheckBoxListener(
            final ActionListener actionListener) {
        this.enableHttpServerCheckBox.addActionListener(actionListener);
    }

    /**
     * Removes an Action Listener to the Enable Http server checkbox.
     *
     * @param actionListener
     *            the listener to remove to the checkbox
     */
    public void removeEnableHttpServerCheckBoxListener(
            final ActionListener actionListener) {
        this.enableHttpServerCheckBox.removeActionListener(actionListener);
    }

    /**
     * Add a {@link java.awt.event.ActionListener} to the lang combobox.
     *
     * @param l
     *            the listener to add to the lang combobox
     */
    public void addLangComboListener(final ActionListener l) {
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
