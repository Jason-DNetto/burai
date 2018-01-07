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
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.app.project.editor.input.geom;

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.atoms.model.Cell;
import burai.input.QEInput;

public class QEFXGeom extends QEFXEditorComponent<QEFXGeomController> {

    private QEFXCell cellComponent;
    private QEFXElements elementsComponent;
    private QEFXAtoms atomsComponent;

    public QEFXGeom(QEFXMainController mainController, QEInput input, Cell cell) throws IOException {
        super("QEFXGeom.fxml", new QEFXGeomController(mainController));

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.createComponents(input, cell);
    }

    private void createComponents(QEInput input, Cell cell) throws IOException {
        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        this.cellComponent = new QEFXCell(mainController, input, cell);
        this.elementsComponent = new QEFXElements(mainController, input, cell);
        this.atomsComponent = new QEFXAtoms(mainController, input, cell);

        if (this.controller != null) {
            this.controller.setCellPane(this.cellComponent.getNode());
            this.controller.setElementsPane(this.elementsComponent.getNode());
            this.controller.setAtomsPane(this.atomsComponent.getNode());
        }
    }

    @Override
    public void notifyEditorOpened() {
        this.cellComponent.notifyEditorOpened();
        this.elementsComponent.notifyEditorOpened();
        this.atomsComponent.notifyEditorOpened();
    }

    /*added by Jason D'Netto so that the atoms component can be displayed on another form*/
    public QEFXAtoms getAtoms(){
        return atomsComponent;
    }
    /*added by Jason D'Netto so that the mass of the atomic species can be used in the phonon input file*/
    public QEFXElements getElements(){
        return elementsComponent;
    }
}
