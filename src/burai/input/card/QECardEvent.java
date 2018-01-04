/*
 * Copyright (C) 2017 Queensland University Of Technology
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.input.card;

public class QECardEvent {

    public static final int EVENT_TYPE_NULL = 0;

    public static final int EVENT_TYPE_UNIT_CHANGED = 1;
    public static final int EVENT_TYPE_SPECIES_CHANGED = 2;
    public static final int EVENT_TYPE_SPECIES_ADDED = 3;
    public static final int EVENT_TYPE_SPECIES_REMOVED = 4;
    public static final int EVENT_TYPE_SPECIES_CLEARED = 5;
    public static final int EVENT_TYPE_ATOM_CHANGED = 6;
    public static final int EVENT_TYPE_ATOM_MOVED = 7;
    public static final int EVENT_TYPE_ATOM_ADDED = 8;
    public static final int EVENT_TYPE_ATOM_REMOVED = 9;
    public static final int EVENT_TYPE_ATOM_CLEARED = 10;
    public static final int EVENT_TYPE_KPOINT_CHANGED = 11;
    public static final int EVENT_TYPE_KPOINT_ADDED = 12;
    public static final int EVENT_TYPE_KPOINT_REMOVED = 13;
    public static final int EVENT_TYPE_KPOINT_CLEARED = 14;
    public static final int EVENT_TYPE_KGRID_CHANGED = 15;
    public static final int EVENT_TYPE_QPOINT_CHANGED = 16;
    public static final int EVENT_TYPE_QPOINT_ADDED = 17;
    public static final int EVENT_TYPE_QPOINT_REMOVED = 18;
    public static final int EVENT_TYPE_QPOINT_CLEARED = 19;

    private QECard card;

    private int eventType;

    private int speciesIndex;

    private int atomIndex;

    private int kpointIndex;
    
    private int qpointIndex;

    public QECardEvent(QECard card) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        this.card = card;
        this.eventType = EVENT_TYPE_NULL;
        this.atomIndex = -1;
        this.kpointIndex = -1;
        this.qpointIndex = -1;
    }

    public QECard getCard() {
        return this.card;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return this.eventType;
    }

    public void setSpeciesIndex(int speciesIndex) {
        this.speciesIndex = speciesIndex;
    }

    public int getSpeciesIndex() {
        return this.speciesIndex;
    }

    public void setAtomIndex(int atomIndex) {
        this.atomIndex = atomIndex;
    }

    public int getAtomIndex() {
        return this.atomIndex;
    }

    public void setKPointIndex(int kpointIndex) {
        this.kpointIndex = kpointIndex;
    }

    public int getKPointIndex() {
        return this.kpointIndex;
    }
    
    public void setQPointIndex(int qpointIndex) {
        this.qpointIndex = qpointIndex;
    }

    public int getQPointIndex() {
        return this.qpointIndex;
    }
}
