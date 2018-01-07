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

package burai.app.project.viewer.result.band;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.band.QEFXBandEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.BandData;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXBandButton extends QEFXResultButton<QEFXBandViewer, QEFXBandEditor> {

    private static final String FILE_NAME = ".burai.graph.band";

    private static final String BUTTON_TITLE = "BAND";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(lightslategrey, -55.0%)";

    public static QEFXResultButtonWrapper<QEFXBandButton> getWrapper(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            return null;
        }

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null || (!projectStatus.isBandDone())) {
            return null;
        }

        ProjectEnergies projectEnergies = projectProperty.getFermiEnergies();
        if (projectEnergies == null) {//tell someone they made a mistake rather than telling them nothing
            return null;//return error message in output graph title
        }

        ProjectBand projectBand = projectProperty.getBand();
        if (projectBand == null) {
            return null;
        }

        ProjectBandPaths projectBandPaths = projectProperty.getBandPaths();
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return null;
        }

        BandData bandData = projectBand.getBandData();
        if (bandData == null) {
            return null;
        }

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : (project.getPrefixName() + ".band1.gnu");

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
            QEFXBandButton button = new QEFXBandButton(projectController, projectProperty);

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

    private QEFXBandButton(QEFXProjectController projectController, ProjectProperty projectProperty) {
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
    protected final QEFXBandViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        QEFXBandViewer viewer = new QEFXBandViewer(this.projectController, this.projectProperty);

        if (viewer != null) {
            QEFXBandViewerController controller = viewer.getController();
            if (controller != null) {
                controller.setPropertyFile(this.propertyFile);
            }
        }

        return viewer;
    }

    @Override
    protected final QEFXBandEditor createResultEditor(QEFXBandViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXBandEditor(this.projectController, resultViewer);
    }
}
