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
import java.util.logging.Logger;

import javax.swing.JCheckBox;

import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.views.PrefsView;
import net.eliosoft.elios.main.LoggersManager;



/**
 * The Controller of the prefs view.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class PrefsController {

	private final RemoteModel remoteModel;
	private final PrefsView prefsView;
	
	private final transient Logger logger = LoggersManager.getInstance().getLogger(PrefsController.class
			.getName());

	/**
	 * The default constructor for the prefs controller.
	 * @param remoteModel the model associated to the controller
	 * @param prefsView  the view associated to the controller
	 */
	public PrefsController(RemoteModel remoteModel, PrefsView prefsView) {
		this.remoteModel = remoteModel;
		this.prefsView = prefsView;

		this.initListeners();
	}

	private void initListeners() {
		this.prefsView.addStartArtNetButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					remoteModel.startArtNet();
					if(remoteModel.isHttpServerEnabled()){
						remoteModel.startHttp();
					}
				} catch (Exception exception) {
					logger.severe(exception.getMessage());
					exception.printStackTrace();
					
					remoteModel.stopHttp();
					remoteModel.stopArtNet();
				}
			}
		});

		this.prefsView.addStopArtNetButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(remoteModel.isHttpServerEnabled()){
					remoteModel.stopHttp();
				}
				remoteModel.stopArtNet();
			}
		});
		
		this.prefsView.addEnableHttpServerCheckBoxListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.setHttpServerEnabled(((JCheckBox)e.getSource()).isSelected());
			}
		});
		
		this.prefsView.addEnableAdditiveModeCheckBoxListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.setAdditiveModeEnabled(((JCheckBox)e.getSource()).isSelected());
			}
		});
	}
}
