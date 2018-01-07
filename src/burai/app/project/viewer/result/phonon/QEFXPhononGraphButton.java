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

package burai.app.project.viewer.result.phonon;

import burai.app.project.viewer.result.phonon.*;
import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.phonon.QEFXPhononEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.PhData;
import burai.project.property.ProjectPh;
import burai.project.property.ProjectPhononPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXPhononGraphButton extends QEFXResultButton<QEFXPhononViewer, QEFXPhononEditor> {

    private static final String FILE_NAME = ".burai.graph.phonon";

    private static final String BUTTON_TITLE = "PHONON";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(lightslategrey, -55.0%)";

    public static QEFXResultButtonWrapper<QEFXPhononGraphButton> getWrapper(QEFXProjectController projectController, Project project) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            //System.out.println("projectProperty is null, QEFXPhononGraphButton.java");//DEBUG
            return null;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null || (!projectStatus.isPhDone())) {
            //System.out.println("projectStatus is null, QEFXPhononGraphButton.java");//DEBUG
            return null;
        }

        ProjectEnergies projectFrequencies = projectProperty.getPhFrequencies();
        if (projectFrequencies == null) {//tell someone they made a mistake rather than telling them nothing
            //user error reported to user when displaying the graph
            return null;
        }

        ProjectPh projectPhonon = projectProperty.getPh();
        if (projectPhonon == null) {
            //System.out.println("projectPhonon is null, QEFXPhononGraphButton.java");//DEBUG
            return null;
        }

        ProjectPhononPaths projectPhononPaths = projectProperty.getPhononPaths();
        if (projectPhononPaths == null || projectPhononPaths.numPoints() < 1) {
            if (projectPhononPaths == null) {
                //System.out.println("projectPhononPaths is null, QEFXPhononGraphButton.java");//DEBUG
            } else {
                //System.out.println("projectPhononPaths.numpoints < 1, QEFXPhononGraphButton.java");//DEBUG
            }
            //return null;
        }

        PhData phData = projectPhonon.getPhdata();
        if (phData == null || phData.numPoints() < 1) {
            if(phData==null){
                //System.out.println("phData is null, QEFXPhononGraphButton.java");//DEBUG
            } else {
                //System.out.println("phData.numpoints < 1, QEFXPhononGraphButton.java");//DEBUG
            }
            return null;
        }

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : (project.getPrefixName() + ".phonon.gnu");

        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }

        try {
            if (file == null || (!file.isFile()) || (file.length() <= 0L)) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return () -> {
            QEFXPhononGraphButton button = new QEFXPhononGraphButton(projectController, projectProperty);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.propertyFile = propFile;
            }

            return button;
        };
    }

    private File propertyFile;

    private ProjectProperty projectProperty;

    private QEFXPhononGraphButton(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, BUTTON_TITLE, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.propertyFile = null;
        this.projectProperty = projectProperty;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected final QEFXPhononViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        QEFXPhononViewer viewer = new QEFXPhononViewer(this.projectController, this.projectProperty);

        if (viewer != null) {
            QEFXPhononViewerController controller = viewer.getController();
            if (controller != null) {
                controller.setPropertyFile(this.propertyFile);
            }
        }

        return viewer;
    }

    @Override
    protected final QEFXPhononEditor createResultEditor(QEFXPhononViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXPhononEditor(this.projectController, resultViewer);
    }
}
