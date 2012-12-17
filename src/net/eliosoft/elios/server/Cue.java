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

package net.eliosoft.elios.server;

/**
 * An object representing a DMX universe state.
 *
 * @author Jeremie GASTON-RAOUL
 *
 */
public class Cue {
    private String name;
    private byte[] dmxArray = new byte[ArtNetServerManager.DMX_CHANNELS_COUNT];

    /**
     * Constructs an empty {@link Cue}. This constructor is mainly used for
     * deserialization.
     */
    Cue() {
        // nothing to do here.
    }

    /**
     * The constructor method for the cue object.
     *
     * @param name
     *            the name of the cue
     * @param dmxArray
     *            the dmx array of the cue
     */
    public Cue(final String name, final byte[] dmxArray) {
        this.name = name;
        System.arraycopy(dmxArray, 0, this.dmxArray, 0,
                ArtNetServerManager.DMX_CHANNELS_COUNT);
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Return the DMX Array.
     *
     * @return the dmxArray
     */
    public byte[] getDmxArray() {
        return dmxArray;
    }

    /**
     * Sets the DMX Array.
     *
     * @param dmxArray
     *            the dmxArray to set
     */
    void setDmxArray(final byte[] dmxArray) {
        System.arraycopy(dmxArray, 0, this.dmxArray, 0,
                ArtNetServerManager.DMX_CHANNELS_COUNT);
    }

}
