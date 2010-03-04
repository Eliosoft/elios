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
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

/**
 * @author jeremie
 * The view of the remote
 */
public class RemoteView {
	private final RemoteModel remoteModel;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel remotePanel = new JPanel();
	private final JTextField commandLine = new JTextField();
	
	private final List<JButton> valueButtonsList = new ArrayList<JButton>();
	private final List<Character> valuesList = new ArrayList<Character>();
	private final JButton enterButton;
	private final JButton resetButton;
	private final JButton delButton;

	
	/**
	 * The default contructor of the remote view
	 * @param remoteModel the model associated to the view
	 */
	public RemoteView(final RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {
				commandLine.setText(event.getCommand());
				if(event.getCommand().isEmpty()){
					delButton.setEnabled(false);
					resetButton.setEnabled(false);
				}
				else{
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
		});
		
		this.remotePanel.setLayout(layout);
		
		this.commandLine.setEditable(false);
		this.commandLine.setFocusable(false);
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = 0;
		this.constraints.gridwidth = 6;
		this.remotePanel.add(this.commandLine,this.constraints);
		
		this.addValueButton('1', 1, 2, true);
		this.addValueButton('2', 1, 2, true);
		this.addValueButton('3', 1, 2, true);
		this.addValueButton('4', 2, 2, true);
		this.addValueButton('5', 2, 2, true);
		this.addValueButton('6', 2, 2, true);
		this.addValueButton('7', 3, 2, true);
		this.addValueButton('8', 3, 2, true);
		this.addValueButton('9', 3, 2, true);
		this.addValueButton(';', 4, 2, true);
		this.addValueButton('0', 4, 2, true);
		this.addValueButton('@', 4, 2, true);
		this.delButton = this.addButton(Messages.getString("remoteview.delete"), 5, 2, false); //$NON-NLS-1$
		this.enterButton = this.addButton(Messages.getString("remoteview.enter"), 5, 2, false); //$NON-NLS-1$
		this.resetButton = this.addButton(Messages.getString("remoteview.reset"), 5, 2, false); //$NON-NLS-1$
	}
	
	/**
	 * get the remote panel
	 * @return the panel
	 */
	public JPanel getRemotePanel(){
		return this.remotePanel;
	}
	
	/**
	 * set text of the command line field
	 * @param value the text to set
	 */
	public void setCommandLineFieldValue(String value){
		this.commandLine.setText(value);
	}
	
	/**
	 * add an element to the list of listeners of the remote panel
	 * @param listener the listener to add
	 */
	public void addRemotePanelKeyListener(KeyListener listener){
		this.remotePanel.addKeyListener(listener);
	}

	/**
	 * remove an element from the list of listeners of the remote panel
	 * @param listener the listener to remove
	 */
	public void removeRemotePanelKeyListener(KeyListener listener){
		this.remotePanel.removeKeyListener(listener);
	}
	
	/**
	 * add an element to the list of listeners of the value buttons
	 * @param listener the listener to add
	 */
	public void addValueButtonsListener(ActionListener listener) {
		for(JButton button : valueButtonsList){
			button.addActionListener(listener);
		}
	}

	/**
	 * remove an element from the list of listeners of the value buttons
	 * @param listener the listener to remove
	 */
	public void removeValueButtonsListener(ActionListener listener) {
		for(JButton button : valueButtonsList){
			button.removeActionListener(listener);
		}
	}
	
	/**
	 * add an element to the list of listeners of the del button
	 * @param actionListener the listener to add
	 */
	public void addDelButtonListener(ActionListener actionListener) {
		this.delButton.addActionListener(actionListener);
	}

	/**
	 * remove an element from the list of listeners of the del button
	 * @param actionListener the listener to remove
	 */
	public void removeDelButtonListener(ActionListener actionListener) {
		this.delButton.removeActionListener(actionListener);
	}

	/**
	 * add an element to the list of listeners of the enter button
	 * @param actionListener the listener to add
	 */
	public void addEnterButtonListener(ActionListener actionListener) {
		this.enterButton.addActionListener(actionListener);
	}

	/**
	 * remove an element from the list of listeners of the enter button
	 * @param actionListener the listener to remove
	 */
	public void removeEnterButtonListener(ActionListener actionListener) {
		this.enterButton.removeActionListener(actionListener);
	}
	
	/**
	 * add an element to the list of listeners of the reset button
	 * @param actionListener the listener to add
	 */
	public void addResetButtonListener(ActionListener actionListener) {
		this.resetButton.addActionListener(actionListener);
	}
	
	/**
	 * remove an element from the list of listeners of the reset button
	 * @param actionListener the listener to remove
	 */
	public void removeResetButtonListener(ActionListener actionListener) {
		this.resetButton.removeActionListener(actionListener);
	}
	
	/**
	 * get the input map of the remote panel
	 * @return the input map
	 */
	public InputMap getRemotePanelInputMap(){
		return this.remotePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
	/**
	 * get the action map of the remote panel
	 * @return the action map
	 */
	public ActionMap getRemotePanelActionMap(){
		return this.remotePanel.getActionMap();
	}
	
	/**
	 * determines whether the enter button is enabled
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isEnterButtonEnabled() {
		return this.enterButton.isEnabled();
	}

	/**
	 * determines whether the reset button is enabled
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isResetButtonEnabled() {
		return this.resetButton.isEnabled();
	}
	
	/**
	 * determines whether the del button is enabled
	 * @return true if it enabled or false it is disabled
	 */
	public boolean isDelButtonEnabled() {
		return this.delButton.isEnabled();
	}
	
	/**
	 * get the list of values on the buttons
	 * @return the values list
	 */
	public List<Character> getValuesList(){
		return this.valuesList;
	}
	
	private void addValueButton(final Character c, int gridY, int gridWidth, boolean enabled){
		JButton button = addButton(c.toString(), gridY, gridWidth, enabled);
		valuesList.add(c);
		valueButtonsList.add(button);
	}

	private JButton addButton(String text, int gridY, int gridWidth, boolean enabled) {
		JButton button = new JButton(text);
		button.setEnabled(enabled);
		button.setFocusable(false);
		
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = gridY;
		this.constraints.gridwidth = gridWidth;
		
		remotePanel.add(button,constraints);
		return button;
	}
	
	
}
