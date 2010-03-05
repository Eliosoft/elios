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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnetremote.gui.models.RemoteModel;

/**
 * The view of the logs. This class print logs in a list.
 * @author Jeremie GASTON-RAOUL
 */
public class LogsView {

	private RemoteModel remoteModel;
	private JList logsList;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel logsPanel = new JPanel();
	private final JButton clearLogsButton;

	/**
	 * The constructor of the Logs View.
	 * @param remoteModel the RemoteModel used by the view
	 */
	public LogsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;

		this.logsPanel.setLayout(this.layout);

		this.logsList = new JList(this.remoteModel.getLogsListModel());
		this.logsList.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(final ListDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						logsList.ensureIndexIsVisible(e.getIndex0());
					}
				});
			}

			@Override
			public void intervalRemoved(ListDataEvent e) { }

			@Override
			public void contentsChanged(ListDataEvent e) { }
		});

		JScrollPane scrollPane = new JScrollPane(logsList);
		scrollPane.setMinimumSize(new Dimension(200, 100));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.constraints.fill = GridBagConstraints.BOTH;
		this.constraints.gridy = 0;
		this.logsPanel.add(scrollPane, this.constraints);

		this.clearLogsButton = new JButton(Messages.getString("logsview.clear")); //$NON-NLS-1$
		this.constraints.fill = GridBagConstraints.NONE;
		this.constraints.gridy = 1;
		logsPanel.add(this.clearLogsButton, this.constraints);
	}

	/**
	 * Returns the logs panel of the view.
	 * @return the logs panel
	 */
	public JPanel getLogsPanel() {
		return this.logsPanel;
	}

	/**
	 * Adds an action listener to the clear logs button.
	 * @param listener the listener to add
	 */
	public void addClearLogsButtonListener(ActionListener listener){
		this.clearLogsButton.addActionListener(listener);
	}

	/**
	 * Removes an action listener to the clear logs button.
	 * @param listener the listener to remove
	 */
	public void removeClearLogsButtonListener(ActionListener listener){
		this.clearLogsButton.removeActionListener(listener);
	}
}
