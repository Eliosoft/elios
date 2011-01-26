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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.logging.LogRecord;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.main.Elios;



/**
 * The view of the logs. This class print logs in a list.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class LogsView implements ViewInterface {

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
					@Override
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
		this.logsList.setCellRenderer(new ListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return new JLogRecordLabel((LogRecord) value);
			}
		});

		JScrollPane scrollPane = new JScrollPane(logsList);
		scrollPane.setMinimumSize(new Dimension(200, 100));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.constraints.fill = GridBagConstraints.BOTH;
		this.constraints.gridy = 0;
		this.constraints.weightx = 1;
		this.constraints.weighty = 1;
		this.logsPanel.add(scrollPane, this.constraints);

		this.clearLogsButton = new JButton(Messages.getString("logsview.clear"), new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/edit-clear.png"))); //$NON-NLS-1$
		this.constraints.fill = GridBagConstraints.NONE;
		this.constraints.gridy = 1;
		this.constraints.weightx = 0;
		this.constraints.weighty = 0;
		logsPanel.add(this.clearLogsButton, this.constraints);
	}

	/**
	 * Returns the logs panel of the view.
	 * @return the logs panel
	 */
	@Override
	public JComponent getViewComponent() {
		return this.logsPanel;
	}

	/**
	 * Adds an action listener to the clear logs button.
	 * @param listener the listener to add
	 */
	public void addClearLogsButtonListener(ActionListener listener) {
		this.clearLogsButton.addActionListener(listener);
	}

	/**
	 * Removes an action listener to the clear logs button.
	 * @param listener the listener to remove
	 */
	public void removeClearLogsButtonListener(ActionListener listener) {
		this.clearLogsButton.removeActionListener(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
	    return Messages.getString("logsview.title");
	}
}
