/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.modeler.slabmodel;

import burai.atoms.model.Cell;

public abstract class SlabModel {

    protected double offset;
    protected double vacuum;

    protected int scaleA;
    protected int scaleB;

    protected SlabModel(double offset, double vacuum) {
        this.offset = offset;
        this.vacuum = vacuum;
        this.scaleA = 1;
        this.scaleB = 1;
    }

    public final void setOffset(double offset) {
        this.offset = offset;
    }

    public final void setVacuum(double vacuum) {
        this.vacuum = vacuum;
    }

    public final void setScaleA(int scaleA) {
        this.scaleA = scaleA;
    }

    public final void setScaleB(int scaleB) {
        this.scaleB = scaleB;
    }

    public abstract SlabModel[] getSlabModels();

    public abstract boolean updateCell(Cell cell);

}
