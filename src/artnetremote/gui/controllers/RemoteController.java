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

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import artnetremote.gui.models.RemoteModel;
import artnetremote.gui.views.RemoteView;

/**
 * The controller of the remote view.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class RemoteController {

	private final RemoteModel remoteModel;
	private final RemoteView remoteView;

	/**
	 * The default constructor for the remote controller.
	 * @param remoteModel model that contains the state of the remote.
	 * @param remoteView view that display the remote.
	 */
	public RemoteController(RemoteModel remoteModel, RemoteView remoteView) {
		this.remoteModel = remoteModel;
		this.remoteView = remoteView;

		this.initButtonsListeners();
		this.initKeyStrokes();
	}

	private void initKeyStrokes() {
		InputMap inputMap = this.remoteView.getRemotePanelInputMap();
		ActionMap actionMap = this.remoteView.getRemotePanelActionMap();
		for (Character c : this.remoteView.getValuesList()) {
			this.initValueKeyStroke(inputMap, actionMap, c);
		}

		inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "backspace");
		actionMap.put("backspace", new AbstractAction() {

			private static final long serialVersionUID = -4335381990869042671L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (remoteView.isDelButtonEnabled()) {
					remoteModel.delLastCommandLineChar();
				}
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
		actionMap.put("enter", new AbstractAction() {

			private static final long serialVersionUID = -1152279417262923317L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (remoteView.isEnterButtonEnabled()) {
					remoteModel.processCommandLine();
				}
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		actionMap.put("escape", new AbstractAction() {

			private static final long serialVersionUID = -8772359436304401320L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (remoteView.isResetButtonEnabled()) {
					remoteModel.resetCommandLine();
				}
			}
		});
	}

	private void initValueKeyStroke(InputMap inputMap, ActionMap actionMap, final Character c){
		inputMap.put(KeyStroke.getKeyStroke(c), c);
		actionMap.put(c, new AbstractAction() {

			private static final long serialVersionUID = 6382574213528201626L;

			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.addToCommandLine(c);
			}
		});
	}

	private void initButtonsListeners() {
		this.remoteView.addValueButtonsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String buttonText = ((JButton) e.getSource()).getText();
				remoteModel.addToCommandLine(buttonText.charAt(0));
			}
		});
		this.remoteView.addDelButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.delLastCommandLineChar();
			}
		});
		this.remoteView.addEnterButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.processCommandLine();
			}
		});
		this.remoteView.addResetButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remoteModel.resetCommandLine();
			}
		});
	}
}
