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

package artnetremote.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.PrefsView;

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
	 * @param remoteModel the model associated to the controller
	 * @param prefsView  the view associated to the controller
	 */
	public PrefsController(RemoteModel remoteModel, PrefsView prefsView) {
		this.remoteModel = remoteModel;
		this.prefsView = prefsView;

		this.initButtonsListeners();
	}

	private void initButtonsListeners() {
		this.prefsView.addStartArtNetButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.startArtNet();
			}
		});

		this.prefsView.addStopArtNetButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.stopHttp();
				remoteModel.stopArtNet();
			}
		});
		
		this.prefsView.addStartHttpButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.startHttp();
			}
		});

		this.prefsView.addStopHttpButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.stopHttp();
			}
		});
	}
}
