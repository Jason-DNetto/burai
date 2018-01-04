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
import java.io.IOException;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class QEFXPh_q extends QEFXEditorComponent<QEFXPh_qController>{
    
    public QEFXPh_q(QEFXMainController mainController, QEInput input) throws IOException {
        super("QEFXPh_q.fxml", new QEFXPh_qController(mainController, input));
    }

    @Override
    public void notifyEditorOpened() {
        //run a function that checks if the different sections should be enabled or disabled
        this.controller.QIsChanged(this.controller.getNmlPh());
    }
}