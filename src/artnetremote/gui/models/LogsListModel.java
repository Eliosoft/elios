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

package artnetremote.gui.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;

/**
 * A {@code ListModel} that wrap one or more {@code java.util.Logger}.
 *
 * @author Jeremie GASTON-RAOUL
 * @author Alexandre COLLIGNON
 */
public class LogsListModel extends AbstractListModel {

	private static final long serialVersionUID = -3782927318483496410L;

	/** list of logs. **/
	private List<String> logs;

	/**
	 * Constructs a {@code LogsListModel} that wraps the
	 * {@code java.util.Logger} given in argument.
	 *
	 * @param logger {@code java.util.Logger} to wrap.
	 */
	public LogsListModel(Logger logger) {
		logs = new ArrayList<String>();
	}

	/**
	 * Adds a {@code java.util.Logger} to wrap.
	 *
	 * @param logger {@code java.util.Logger} to wrap
	 */
	public void addLogger(final Logger logger) {
		logger.addHandler(new Handler() {

			@Override
			public void publish(LogRecord record) {
				addLogLine("[" + logger.getName() + "] " + record.getMessage());
			}

			@Override
			public void flush() {
				// nothing
			}

			@Override
			public void close() {
				logs.add(logger.getName() + " is closing");
			}
		});
	}

	/**
	 * Adds a log Line in the list.
	 *
	 * @param logLine the log line to add
	 */
	private void addLogLine(String logLine) {
		logs.add(logLine);
		this.fireIntervalAdded(this, logs.size() - 1, logs.size() - 1);
	}

	/**
	 * Removes all log lines of the list.
	 */
	public void clearLogsList() {
		int lastSize = logs.size();
		if (lastSize > 0) {
			logs.clear();
			this.fireIntervalRemoved(this, 0, lastSize - 1);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getElementAt(int index) {
		return logs.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return logs.size();
	}
}
