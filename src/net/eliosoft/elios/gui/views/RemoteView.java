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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.eliosoft.elios.gui.events.ArtNetStartedEvent;
import net.eliosoft.elios.gui.events.ArtNetStoppedEvent;
import net.eliosoft.elios.gui.events.CommandLineValueChangedEvent;
import net.eliosoft.elios.gui.events.HttpStartedEvent;
import net.eliosoft.elios.gui.events.HttpStoppedEvent;
import net.eliosoft.elios.gui.listeners.RemoteModelListener;
import net.eliosoft.elios.gui.models.RemoteModel;



/**
 * The view of the remote.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class RemoteView implements ViewInterface {
	
	private static final int BUTTON_WIDTH = 70;
	private static final int BUTTON_HEIGHT = 40;
	
	private final RemoteModel remoteModel;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel remotePanel = new JPanel();
	private final JTextField commandLineTextField = new JTextField();

	private final List<JButton> valueButtonsList = new ArrayList<JButton>();
	private final List<Character> valuesList = new ArrayList<Character>();
	private final JButton enterButton;
	private final JButton resetButton;
	private final JButton delButton;


	/**
	 * The default constructor of the remote view.
	 * @param remoteModel the model associated to the view
	 */
	public RemoteView(final RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {
				commandLineTextField.setText(event.getCommand());
				if (event.getCommand().isEmpty()) {
					delButton.setEnabled(false);
					resetButton.setEnabled(false);
				} else {
					delButton.setEnabled(true);
					resetButton.setEnabled(true);
				}
			}
			@Override
			public void artNetStarted(ArtNetStartedEvent event) {
				enterButton.setEnabled(true);
			}
			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {
				enterButton.setEnabled(false);
			}
			@Override
			public void httpStarted(HttpStartedEvent event) {}
			@Override
			public void httpStopped(HttpStoppedEvent event) {}
		});

		this.remotePanel.setLayout(layout);

		this.commandLineTextField.setEditable(false);
		this.commandLineTextField.setFocusable(false);
		this.commandLineTextField.setMinimumSize(new Dimension(RemoteView.BUTTON_WIDTH*4,RemoteView.BUTTON_HEIGHT));
		this.commandLineTextField.setPreferredSize(new Dimension(RemoteView.BUTTON_WIDTH*4,RemoteView.BUTTON_HEIGHT));
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = 0;
		this.constraints.gridwidth = 4;
		this.remotePanel.add(this.commandLineTextField, this.constraints);

		this.addValueButton('1', 1, 1, true);
		this.addValueButton('2', 1, 1, true);
		this.addValueButton('3', 1, 1, true);
		this.addValueButton('+', 1, 1, true);
		this.addValueButton('4', 2, 1, true);
		this.addValueButton('5', 2, 1, true);
		this.addValueButton('6', 2, 1, true);
		this.addValueButton('-', 2, 1, true);
		this.addValueButton('7', 3, 1, true);
		this.addValueButton('8', 3, 1, true);
		this.addValueButton('9', 3, 1, true);
		this.addValueButton('/', 3, 1, true);
		this.addValueButton(';', 4, 1, true);
		this.addValueButton('0', 4, 1, true);
		this.addValueButton('@', 4, 1, true);
		this.addValueButton('F', 4, 1, true);
		this.delButton = this.addButton(Messages.getString("remoteview.delete"), 5, 1, false); //$NON-NLS-1$
		this.enterButton = this.addButton(Messages.getString("remoteview.enter"), 5, 1, false); //$NON-NLS-1$
		this.resetButton = this.addButton(Messages.getString("remoteview.reset"), 5, 1, false); //$NON-NLS-1$
		this.addValueButton('D', 5, 1, true);
	}

	/**
	 * Gets the remote panel.
	 * @return the panel
	 */

	public JComponent getViewComponent() {
		return this.remotePanel;
	}

	/**
	 * Sets text of the command line field.
	 * @param value the text to set
	 */
	public void setCommandLineFieldValue(String value) {
		this.commandLineTextField.setText(value);
	}

	/**
	 * Adds an element to the list of listeners of the remote panel.
	 * @param listener the listener to add
	 */
	public void addRemotePanelKeyListener(KeyListener listener) {
		this.remotePanel.addKeyListener(listener);
	}

	/**
	 * Removes an element from the list of listeners of the remote panel.
	 * @param listener the listener to remove
	 */
	public void removeRemotePanelKeyListener(KeyListener listener) {
		this.remotePanel.removeKeyListener(listener);
	}

	/**
	 * Adds an element to the list of listeners of the value buttons.
	 * @param listener the listener to add
	 */
	public void addValueButtonsListener(ActionListener listener) {
		for (JButton button : valueButtonsList) {
			button.addActionListener(listener);
		}
	}

	/**
	 * Removes an element from the list of listeners of the value buttons.
	 * @param listener the listener to remove
	 */
	public void removeValueButtonsListener(ActionListener listener) {
		for (JButton button : valueButtonsList) {
			button.removeActionListener(listener);
		}
	}

	/**
	 * Adds an element to the list of listeners of the del button.
	 * @param actionListener the listener to add
	 */
	public void addDelButtonListener(ActionListener actionListener) {
		this.delButton.addActionListener(actionListener);
	}

	/**
	 * Removes an element from the list of listeners of the del button.
	 * @param actionListener the listener to remove
	 */
	public void removeDelButtonListener(ActionListener actionListener) {
		this.delButton.removeActionListener(actionListener);
	}

	/**
	 * Adds an element to the list of listeners of the enter button.
	 * @param actionListener the listener to add
	 */
	public void addEnterButtonListener(ActionListener actionListener) {
		this.enterButton.addActionListener(actionListener);
	}

	/**
	 * Removes an element from the list of listeners of the enter button.
	 * @param actionListener the listener to remove
	 */
	public void removeEnterButtonListener(ActionListener actionListener) {
		this.enterButton.removeActionListener(actionListener);
	}

	/**
	 * Adds an element to the list of listeners of the reset button.
	 * @param actionListener the listener to add
	 */
	public void addResetButtonListener(ActionListener actionListener) {
		this.resetButton.addActionListener(actionListener);
	}

	/**
	 * Removes an element from the list of listeners of the reset button.
	 * @param actionListener the listener to remove
	 */
	public void removeResetButtonListener(ActionListener actionListener) {
		this.resetButton.removeActionListener(actionListener);
	}

	/**
	 * Gets the input map of the remote panel.
	 * @return the input map
	 */
	public InputMap getRemotePanelInputMap() {
		return this.remotePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Gets the action map of the remote panel.
	 * @return the action map
	 */
	public ActionMap getRemotePanelActionMap() {
		return this.remotePanel.getActionMap();
	}

	/**
	 * Determines whether the enter button is enabled.
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isEnterButtonEnabled() {
		return this.enterButton.isEnabled();
	}

	/**
	 * Determines whether the reset button is enabled.
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isResetButtonEnabled() {
		return this.resetButton.isEnabled();
	}

	/**
	 * Determines whether the del button is enabled.
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isDelButtonEnabled() {
		return this.delButton.isEnabled();
	}

	/**
	 * Gets the list of values on the buttons.
	 * @return the values list
	 */
	public List<Character> getValuesList() {
		return this.valuesList;
	}

	private void addValueButton(final Character c, int gridY, int gridWidth, boolean enabled) {
		JButton button = addButton(c.toString(), gridY, gridWidth, enabled);
		valuesList.add(c);
		valueButtonsList.add(button);
	}

	private JButton addButton(String text, int gridY, int gridWidth, boolean enabled) {
		JButton button = new JButton(text);
		button.setEnabled(enabled);
		button.setFocusable(false);
		
		button.setMinimumSize(new Dimension(RemoteView.BUTTON_WIDTH,RemoteView.BUTTON_HEIGHT));
		button.setPreferredSize(new Dimension(RemoteView.BUTTON_WIDTH,RemoteView.BUTTON_HEIGHT));

		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = gridY;
		this.constraints.gridwidth = gridWidth;

		remotePanel.add(button, constraints);
		return button;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
	    return Messages.getString("remoteview.title");
	}
}
