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

package burai.app.project.editor.input.phonon;

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.atoms.model.Cell;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;

public class QEFXPhonon extends QEFXEditorComponent<QEFXPhononController> {
    
    private QEFXPh_Calc calcComponent;
    private QEFXPh_IrrRep irrRepComponent;
    private QEFXPh_k kComponent;
    private QEFXPh_q qComponent;
    
    private QEAtomicPositions atomicPositions;
    private QEAtomicSpecies atomicSpecies;

    public QEFXPhonon(QEFXMainController mainController, QEInput input, QEAtomicPositions atomicPositions, QEAtomicSpecies atomicSpecies) throws IOException {
        super("QEFXPhonon.fxml", new QEFXPhononController(mainController));

        if(input == null) {
            throw new IllegalArgumentException("input is null.");
        }
        this.atomicPositions = atomicPositions;
        this.atomicSpecies = atomicSpecies;
        this.createComponents(input);
        
    }
    
    private void createComponents(QEInput input) throws IOException {
        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        this.calcComponent = new QEFXPh_Calc(mainController, input);
        this.irrRepComponent = new QEFXPh_IrrRep(mainController, input, this.atomicPositions, this.atomicSpecies);
        this.kComponent = new QEFXPh_k(mainController, input);
        this.qComponent = new QEFXPh_q(mainController, input);

        if (this.controller != null) {
            this.controller.setCalcPane(this.calcComponent.getNode());
            this.controller.setIrrRepPane(this.irrRepComponent.getNode());
            this.controller.setKPane(this.kComponent.getNode());
            this.controller.setQPane(this.qComponent.getNode());
        }
    }

    @Override
    public void notifyEditorOpened() {
        this.calcComponent.notifyEditorOpened();
        this.irrRepComponent.notifyEditorOpened();
        this.kComponent.notifyEditorOpened();
        this.qComponent.notifyEditorOpened();
    }

}
