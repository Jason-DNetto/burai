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
package burai.app.project.viewer.result.log;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXPhononButton extends QEFXLogButton {
    
    private static final String BUTTON_TITLE = "DYN MAT";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(deepskyblue, -12.0%)";
    
    public static QEFXResultButtonWrapper<QEFXPhononButton> getWrapper(QEFXProjectController projectController, Project project, int index) {
        String dirPath = project == null ? null : project.getDirectoryPath();
        String filePrefix = "dyn";
        String fileName = filePrefix + Integer.toString(index);
        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }
        try {
            if (file != null && file.isFile() && (file.length() > 0L)) {
                final File file_ = file;
                return () -> new QEFXPhononButton(projectController, file_, index);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private QEFXPhononButton(QEFXProjectController projectController, File file, int index) {
        super(projectController, BUTTON_TITLE, "#"+index, file);

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }
}
