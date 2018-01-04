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
package burai.app.project.editor.input.phonon;

import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;
import burai.input.namelist.QENamelist;
import javafx.scene.control.TableView;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
class KBinder {
    private TableView<QEKPoint> k_table;
    private QEKPoints kPoints;
    private QENamelist nmlSystem;
    public KBinder(TableView<QEKPoint> k_table, QEKPoints kPoints) {
        if (k_table == null) {throw new IllegalArgumentException("k_table is null.");}
        if (kPoints == null) {throw new IllegalArgumentException("kPoints is null.");}
        //if (nmlSystem == null) {throw new IllegalArgumentException("nmlSystem is null.");}
        this.k_table = k_table;
        this.kPoints = kPoints;
        //this.nmlSystem = nmlSystem;
        setupKTable();
    }
    
    private void setupKTable(){
        int numKPoints = this.kPoints.numKPoints();
        for (int i=0;i<numKPoints;i++){
            QEKPoint k_point = this.kPoints.getKPoint(i);
            if (k_point != null){
                this.k_table.getItems().add(k_point);
            }
        }
    }
    
    public void bindtable(){
        this.setupKTable();
    }
}
