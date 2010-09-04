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

import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.eliosoft.elios.gui.models.RemoteModel;



/**
 * The view of the Logs Line.
 * This view displays the last line of logs in a text field.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class LogsLineView implements ViewInterface {

	private final RemoteModel remoteModel;
	
	private final static JLabel EMPTY_LABEL = new JLabel(" ");

	private final JPanel logsLinePanel = new JPanel();

	/**
	 * The constructor of the class.
	 * @param remoteModel the model associated with this view
	 */
	public LogsLineView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.logsLinePanel.setBorder(BorderFactory.createEtchedBorder());
		this.logsLinePanel.add(LogsLineView.EMPTY_LABEL);

		this.initRemoteModelListener();
	}

	private void initRemoteModelListener() {
		this.remoteModel.getLogsListModel().addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				replaceLabel();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				replaceLabel();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				replaceLabel();
			}
		});

	}

	/**
	 * Returns the log field.
	 * @return the log field of the view
	 */
	public JComponent getViewComponent() {
		return this.logsLinePanel;
	}
	
	private void replaceLabel(){
		logsLinePanel.removeAll();
		if(remoteModel.getLogsListModel().getSize() > 0){
			logsLinePanel.add(new JLogRecordLabel((LogRecord) remoteModel.getLogsListModel().getElementAt(remoteModel.getLogsListModel().getSize()-1)));
		}
		else{
			logsLinePanel.add(LogsLineView.EMPTY_LABEL);
		}
		logsLinePanel.revalidate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
	    return Messages.getString("logsview.title");
	}
}
