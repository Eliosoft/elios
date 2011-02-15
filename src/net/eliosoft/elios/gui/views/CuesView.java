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
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.main.Elios;
import net.eliosoft.elios.server.Cue;

/**
 * The view of the Cues. This view displays and manages the cues.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class CuesView implements ViewInterface {

	private final JPanel cuesPanel = new JPanel(new BorderLayout());
	private final RemoteModel remoteModel;
	
	private final JButton storeButton;
	private final JButton loadButton;
	private final JButton removeButton;
	private final JList cuesList;

	/**
	 * The constructor of the class.
	 * 
	 * @param remoteModel the model associated to the view
	 */
	public CuesView(final RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		cuesList = new JList(this.remoteModel.getCuesListModel());

		cuesList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            int index = cuesList.locationToIndex(e.getPoint());
		            remoteModel.loadCue(remoteModel.getCuesListModel().getElementAt(index));
		         }
		    }
		});

		JScrollPane scrollPane = new JScrollPane(cuesList);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		cuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cuesList.setCellRenderer(new DefaultListCellRenderer() {
			
			private static final long serialVersionUID = -3018960552174916077L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Cue cue = (Cue)value;
				setText(cue.getName());

				return this;
			}
		});
		cuesList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(getSelectedCue() == null){
					loadButton.setEnabled(false);
					removeButton.setEnabled(false);
				}
				else{
					loadButton.setEnabled(true);
					removeButton.setEnabled(true);
				}
			}
		});

		storeButton = new JButton(Messages.getString("cuesview.storebutton"), new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/document-save-as.png")));
		loadButton = new JButton(Messages.getString("cuesview.loadbutton"), new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/document-open.png")));
		loadButton.setEnabled(false);
		removeButton = new JButton(Messages.getString("cuesview.removebutton"), new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/edit-delete.png")));
		removeButton.setEnabled(false);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(storeButton);
		buttonsPanel.add(loadButton);
		buttonsPanel.add(removeButton);
		
		cuesPanel.add(scrollPane,BorderLayout.CENTER);
		cuesPanel.add(buttonsPanel,BorderLayout.SOUTH);
	}

	/**
	 * Returns the cues panel
	 * 
	 * @return the view component
	 */
	@Override
	public JComponent getViewComponent() {
		return this.cuesPanel;
	}
	
	/**
	 * Adds an action listener to the store button.
	 * @param listener the listener to add
	 */
	public void addStoreButtonListener(ActionListener listener) {
		this.storeButton.addActionListener(listener);
	}

	/**
	 * Removes an action listener to the store button.
	 * @param listener the listener to remove
	 */
	public void removeStoreButtonListener(ActionListener listener) {
		this.storeButton.removeActionListener(listener);
	}
	
	/**
	 * Adds an action listener to the load button.
	 * @param listener the listener to add
	 */
	public void addLoadButtonListener(ActionListener listener) {
		this.loadButton.addActionListener(listener);
	}

	/**
	 * Removes an action listener to the load button.
	 * @param listener the listener to remove
	 */
	public void removeLoadButtonListener(ActionListener listener) {
		this.loadButton.removeActionListener(listener);
	}
	
	/**
	 * Adds an action listener to the remove button.
	 * @param listener the listener to add
	 */
	public void addRemoveButtonListener(ActionListener listener) {
		this.removeButton.addActionListener(listener);
	}

	/**
	 * Removes an action listener to the remove button.
	 * @param listener the listener to remove
	 */
	public void removeRemoveButtonListener(ActionListener listener) {
		this.removeButton.removeActionListener(listener);
	}
	
	/**
	 * Gets the selected cue.
	 * @return the selected cue
	 */
	public Cue getSelectedCue(){
		return (Cue)cuesList.getSelectedValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
		return Messages.getString("cuesview.title");
	}
}
