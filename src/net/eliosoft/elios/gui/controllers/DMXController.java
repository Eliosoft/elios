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

import net.eliosoft.elios.gui.models.DMXTableModel;
import net.eliosoft.elios.gui.views.DMXView;

/**
 * The controller of the cues view.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class DMXController {

    private final DMXTableModel dmxTableModel;
    private final DMXView dmxView;

    /**
     * The constructor of the DMXController class.
     * 
     * @param dmxTableModel
     *            the dmx table model associated with this Controller
     * @param dmxView
     *            the view associated with this Controller
     */
    public DMXController(final DMXTableModel dmxTableModel,
	    final DMXView dmxView) {
	this.dmxTableModel = dmxTableModel;
	this.dmxView = dmxView;

	this.initListeners();
    }

    private void initListeners() {
	this.dmxView.addInOutRadioActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(final ActionEvent actionEvent) {
		dmxTableModel.setInputEnabled(actionEvent.getActionCommand()
			.equals("input"));
	    }
	});
    }
}
