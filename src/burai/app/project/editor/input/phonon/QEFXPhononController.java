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
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.app.project.editor.input.phonon;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class QEFXPhononController extends QEFXEditorController {
    @FXML
    private ScrollPane calcPane;
    @FXML
    private ScrollPane irrRepPane;
    @FXML
    private ScrollPane kPane;
    @FXML
    private ScrollPane qPane;
    
    
    public QEFXPhononController(QEFXMainController mainController) {
        super(mainController);
    }
    
    public void setCalcPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.calcPane != null) {
            this.calcPane.setContent(node);
        }
    }
    
    public void setIrrRepPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.irrRepPane != null) {
            this.irrRepPane.setContent(node);
        }
    }
    
    public void setKPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.kPane != null) {
            this.kPane.setContent(node);
        }
    }
    
    public void setQPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.qPane != null) {
            this.qPane.setContent(node);
        }
    }
}
