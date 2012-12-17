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

package net.eliosoft.elios.server.listeners;

import net.eliosoft.elios.server.events.AdditiveModeValueChangedEvent;
import net.eliosoft.elios.server.events.SubnetValueChangedEvent;
import net.eliosoft.elios.server.events.UniverseValueChangedEvent;

/**
 * This interface describes the methods that must be implemented by classes
 * which want to be a listener of the ArtNet server manager.
 *
 * @author Jeremie GASTON-RAOUL
 */
public interface ArtNetServerManagerListener {
    /**
     * This method is called when the value of the subnet has changed.
     *
     * @param event
     *            the event corresponding to the change of the subnet
     */
    void subnetValueChanged(SubnetValueChangedEvent event);

    /**
     * This method is called when the value of the universe has changed.
     *
     * @param event
     *            the event corresponding to the change of the universe
     */
    void universeValueChanged(UniverseValueChangedEvent event);

    /**
     * This method is called when the value of the additive mode has changed.
     *
     * @param event
     *            the event corresponding to the change of the additive mode
     */
    void additiveModeValueChanged(AdditiveModeValueChangedEvent event);
}
