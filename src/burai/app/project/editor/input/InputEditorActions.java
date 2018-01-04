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
package burai.app.project.editor.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.app.project.ProjectAction;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.EditorActions;
import burai.app.project.editor.QEFXEditorComponent;
import burai.app.project.editor.input.band.QEFXBand;
import burai.app.project.editor.input.dos.QEFXDos;
import burai.app.project.editor.input.geom.QEFXAtoms;
import burai.app.project.editor.input.geom.QEFXGeom;
import burai.app.project.editor.input.md.QEFXMd;
import burai.app.project.editor.input.neb.QEFXNeb;
import burai.app.project.editor.input.opt.QEFXOpt;
import burai.app.project.editor.input.phonon.QEFXPhonon;
import burai.app.project.editor.input.scf.QEFXScf;
import burai.app.project.editor.input.tddft.QEFXTddft;
import burai.atoms.model.Cell;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.project.Project;

public class InputEditorActions extends EditorActions {

    private static final String[] EDITOR_ITEMS = {
            "Geometry", "SCF", "Optimize", "MD", "DOS", "Band", "NEB", "TD-DFT", "Phonon" };

    private static final int[] EDITOR_INPUT_MODE = {
            Project.INPUT_MODE_GEOMETRY,
            Project.INPUT_MODE_SCF,
            Project.INPUT_MODE_OPTIMIZ,
            Project.INPUT_MODE_MD,
            Project.INPUT_MODE_DOS,
            Project.INPUT_MODE_BAND,
            Project.INPUT_MODE_NEB,
            Project.INPUT_MODE_TDDFT,
            Project.INPUT_MODE_PHONON
    };

    private Map<String, QEFXEditorComponent<? extends QEFXAppController>> components;

    public InputEditorActions(Project project, QEFXProjectController controller) throws IOException {
        super(project, controller);

        this.createComponents();
        this.setupActions();
    }

    @Override
    public void actionInitially() {
        if (this.controller == null) {
            return;
        }

        this.controller.addEditorMenuItems(EDITOR_ITEMS);

        String item = EDITOR_ITEMS[0];
        ProjectAction action = this.actions.get(item);
        if (action != null) {
            action.actionOnProject(this.controller);
        }
    }

    private void createComponents() throws IOException {
        this.components = new HashMap<String, QEFXEditorComponent<? extends QEFXAppController>>();

        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        if (mainController != null) {
            Cell cell = null;
            QEInput input = null;

            cell = this.project == null ? null : this.project.getCell();
            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null && cell != null) {
                this.components.put(EDITOR_ITEMS[0], new QEFXGeom(mainController, input, cell));
            }

            input = this.project == null ? null : this.project.getQEInputScf();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[1], new QEFXScf(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputOptimiz();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[2], new QEFXOpt(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputMd();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[3], new QEFXMd(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputDos();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[4], new QEFXDos(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputBand();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[5], new QEFXBand(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[6], new QEFXNeb(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[7], new QEFXTddft(mainController, input));
            }

            /*edited by Jason D'Netto*/
            input = this.project == null ? null : this.project.getQEInputPhonon();
            if (input != null) {
                    QEFXGeom tempref = (QEFXGeom) this.components.get(EDITOR_ITEMS[0]);
                    QEAtomicPositions readonly = tempref.getAtoms().getController().getAtomicPositions();
                    QEAtomicSpecies readSpecies = tempref.getElements().getController().getAtomicSpecies();
                this.components.put(EDITOR_ITEMS[8], new QEFXPhonon(mainController, input, readonly, readSpecies));
                
            }
        }
    }

    private void setupActions() {
        if (EDITOR_ITEMS.length != EDITOR_INPUT_MODE.length) {
            throw new RuntimeException("EDITOR_ITEMS.length != EDITOR_INPUT_MODE.length.");
        }

        for (int i = 0; i < EDITOR_ITEMS.length; i++) {
            String item = EDITOR_ITEMS[i];
            int inputMode = EDITOR_INPUT_MODE[i];

            QEFXEditorComponent<? extends QEFXAppController> component = this.components.get(item);
            if (component == null) {
                continue;
            }

            this.actions.put(item, controller2 -> {
                if (controller2 == null) {
                    return;
                }

                controller2.setEditorText(item);
                controller2.setEditorPane(component.getNode());
                component.notifyEditorOpened();

                if (this.project != null) {
                    this.project.resolveQEInputs();
                    this.project.setInputMode(inputMode);
                }
            });
        }
    }
}
