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

import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnetremote.gui.models.RemoteModel;

/**
 * The view of the Logs Line.
 * This view displays the last line of logs in a text field.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class LogsLineView {

	private final RemoteModel remoteModel;

	private final JTextField logField = new JTextField();

	/**
	 * The constructor of the class.
	 * @param remoteModel the model associated with this view
	 */
	public LogsLineView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.logField.setEditable(false);

		this.initRemoteModelListener();
	}

	private void initRemoteModelListener() {
		this.remoteModel.getLogsListModel().addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				logField.setText("");
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				logField.setText((String) remoteModel.getLogsListModel().getElementAt(e.getIndex1()));
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				logField.setText((String) remoteModel.getLogsListModel().getElementAt(e.getIndex1()));
			}
		});

	}

	/**
	 * Returns the log field.
	 * @return the log field of the view
	 */
	public JTextField getLogField() {
		return this.logField;
	}
}
