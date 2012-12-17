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

package net.eliosoft.elios.gui.models;

import javax.swing.AbstractListModel;

import net.eliosoft.elios.server.Cue;
import net.eliosoft.elios.server.CuesManager;
import net.eliosoft.elios.server.events.CueAddedEvent;
import net.eliosoft.elios.server.events.CueRemovedEvent;
import net.eliosoft.elios.server.listeners.CuesManagerListener;

/**
 * A {@code ListModel} that wrap a CuesList.
 *
 * @author Jeremie GASTON-RAOUL
 */
public class CuesListModel extends AbstractListModel {

    private static final long serialVersionUID = 5232976112210058420L;

    private final CuesManager cuesManager;

    /**
     * @param cuesMngr
     */
    public CuesListModel(final CuesManager cuesMngr) {
        this.cuesManager = cuesMngr;
        this.cuesManager
                .addCuesManagerChangedListener(new CuesManagerListener() {

                    @Override
                    public void cueRemoved(final CueRemovedEvent event) {
                        fireContentsChanged(this, 0, cuesManager.getCues()
                                .size());
                    }

                    @Override
                    public void cueAdded(final CueAddedEvent event) {
                        fireContentsChanged(this, 0, cuesManager.getCues()
                                .size());
                    }
                });
    }

    @Override
    public Cue getElementAt(final int index) {
        return cuesManager.getCues().get(index);
    }

    @Override
    public int getSize() {
        return cuesManager.getCues().size();
    }

    /**
     * add a cue to the cuesList.
     *
     * @param cue
     *            the cue to add
     */
    public void addCue(final Cue cue) {
        cuesManager.addCue(cue);
        int index = cuesManager.getCues().size() - 1;
        this.fireIntervalAdded(this, index, index);
    }

    /**
     * remove a cue from the cuesList.
     *
     * @param cue
     *            the cue to remove
     */
    public void removeCue(final Cue cue) {
        int index = cuesManager.getCues().indexOf(cue);
        cuesManager.removeCue(cue.getName());
        this.fireIntervalRemoved(this, index, index);
    }

    /**
     * Returns a Cue name according to the state of the model.
     *
     * @return a string that could be used as a cue name.
     */
    public String getNextDefaultCueName() {
        return cuesManager.getUnusedCueName();
    }

}
