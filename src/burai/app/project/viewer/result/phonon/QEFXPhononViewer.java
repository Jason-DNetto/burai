/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.phonon;

import burai.app.project.viewer.result.phonon.*;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewer;
import burai.project.property.ProjectProperty;

public class QEFXPhononViewer extends QEFXResultViewer<QEFXPhononViewerController> {

    public QEFXPhononViewer(QEFXProjectController projectController, ProjectProperty projectProperty) throws IOException {

        super("QEFXPhononViewer.fxml", new QEFXPhononViewerController(projectController, projectProperty));
    }

}
