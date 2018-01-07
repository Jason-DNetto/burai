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

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import java.io.IOException;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXPh_IrrRep  extends QEFXEditorComponent<QEFXPh_IrrRepController>{
    
    public QEFXPh_IrrRep(QEFXMainController mainController, QEInput input, QEAtomicPositions atomicPositions, QEAtomicSpecies atomicSpecies) throws IOException {
        super("QEFXPh_IrrRep.fxml", new QEFXPh_IrrRepController(mainController, input, atomicPositions, atomicSpecies));
    }

    @Override
    public void notifyEditorOpened() {
        // No Op
    }
}
