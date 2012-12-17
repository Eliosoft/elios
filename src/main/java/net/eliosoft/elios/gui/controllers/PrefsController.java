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

package net.eliosoft.elios.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import artnet4j.ArtNetException;

import net.eliosoft.elios.gui.models.LocaleComboBoxModel;
import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.views.Messages;
import net.eliosoft.elios.gui.views.PrefsView;

/**
 * The Controller of the prefs view.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class PrefsController {

    private final RemoteModel remoteModel;
    private final PrefsView prefsView;

    /**
     * The default constructor for the prefs controller.
     *
     * @param remoteModel
     *            the model associated to the controller
     * @param localeModel
     * @param prefsView
     *            the view associated to the controller
     */
    public PrefsController(final RemoteModel remoteModel,
            final LocaleComboBoxModel localeModel, final PrefsView prefsView) {
        this.remoteModel = remoteModel;
        this.prefsView = prefsView;
        this.initListeners();
    }

    private void initListeners() {
        this.prefsView.addCancelButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                remoteModel.restoreArtNetServerManagerConfig();
            }
        });

        this.prefsView.addSaveButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    remoteModel.applyArtNetServerManagerConfig();
                } catch (ArtNetException ane) {
                    JOptionPane.showMessageDialog(
                            null,
                            MessageFormat.format(
                                    Messages.getString("error.server.cannotstart.message"),
                                    ane.getMessage()),
                            Messages.getString("error.server.cannotstart.title"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.prefsView
                .addEnableHttpServerCheckBoxListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        remoteModel.setHttpServerEnabled(((JCheckBox) e
                                .getSource()).isSelected());
                    }
                });

        this.prefsView.addLangComboListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                Locale l = (Locale) cb.getSelectedItem();
                JOptionPane.showMessageDialog(null, MessageFormat.format(
                        Messages.getString("prefsview.lang.restart"),
                        Messages.getString("ui.lang." + l.getLanguage())));
            }
        });
    }
}
