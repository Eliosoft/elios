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

package net.eliosoft.elios.gui.listeners;

import net.eliosoft.elios.gui.events.ArtNetStartedEvent;
import net.eliosoft.elios.gui.events.ArtNetStoppedEvent;
import net.eliosoft.elios.gui.events.CommandLineValueChangedEvent;
import net.eliosoft.elios.gui.events.HttpStartedEvent;
import net.eliosoft.elios.gui.events.HttpStoppedEvent;
import net.eliosoft.elios.server.events.AdditiveModeValueChangedEvent;

/**
 * This interface describes the methods that must be implemented by classes
 * which want to be a listener of the remote model.
 *
 * @author Jeremie GASTON-RAOUL
 */
public interface RemoteModelListener {
    /**
     * This method is called when the value of the command line has changed.
     *
     * @param event
     *            the event corresponding to the change of the command line
     */
    void commandLineValueChanged(CommandLineValueChangedEvent event);

    /**
     * This method is called when the ArtNet Server is started.
     *
     * @param event
     *            the event corresponding to the start of the server
     */
    void artNetStarted(ArtNetStartedEvent event);

    /**
     * This method is called when the ArtNet Server is stopped.
     *
     * @param event
     *            the event corresponding to the stop of the server
     */
    void artNetStopped(ArtNetStoppedEvent event);

    /**
     * This method is called when the Http Server is started.
     *
     * @param event
     *            the event corresponding to the start of the server
     */
    void httpStarted(HttpStartedEvent event);

    /**
     * This method is called when the Http Server is stopped.
     *
     * @param event
     *            the event corresponding to the stop of the server
     */
    void httpStopped(HttpStoppedEvent event);

    /**
     * This method is called when the value of the additive mode has changed.
     *
     * @param event
     *            the event corresponding to the change of the additive mode
     */
    void additiveModeValueChanged(AdditiveModeValueChangedEvent event);
}
