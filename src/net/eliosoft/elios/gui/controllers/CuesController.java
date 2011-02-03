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

import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.views.CuesView;
import net.eliosoft.elios.gui.views.CuesViewHelper;
import net.eliosoft.elios.server.Cue;



/**
 * The controller of the cues view.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class CuesController {

	private final RemoteModel remoteModel;
	private final CuesView cuesView;

	/**
	 * The constructor of the LogsController class.
	 * @param remoteModel the model associated with this Controller
	 * @param cuesView the view associated with this Controller
	 */
	public CuesController(RemoteModel remoteModel, CuesView cuesView) {
		this.remoteModel = remoteModel;
		this.cuesView = cuesView;

		this.initButtonsListeners();
	}

	private void initButtonsListeners() {
		this.cuesView.addStoreButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remoteModel.storeCue(CuesViewHelper.askForCueName(cuesView.getViewComponent()));
			}
		});

		this.cuesView.addLoadButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Cue cue = cuesView.getSelectedCue();
				if(cue != null){
					remoteModel.loadCue(cue);
				}
			}
		});
		
		this.cuesView.addRemoveButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Cue cue = cuesView.getSelectedCue();
				if(cue != null &&cuesView.confirmCueRemove(cue.getName())){
					remoteModel.removeCue(cue);
				}
			}
		});
	}
}
