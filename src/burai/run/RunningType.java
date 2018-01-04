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

package burai.run;

import java.util.ArrayList;
import java.util.List;

import burai.com.env.Environments;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.project.Project;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectPhononPaths;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;
import burai.run.parser.BandPathParser;
import burai.run.parser.FermiParser;
import burai.run.parser.GeometryParser;
import burai.run.parser.LogParser;
import burai.run.parser.PhParser;
import burai.run.parser.PhPlotbandParser;
import burai.run.parser.ScfParser;
import burai.run.parser.VoidParser;
import java.io.File;

public enum RunningType {
    SCF("SCF", Project.INPUT_MODE_SCF),
    OPTIMIZ("Optimize", Project.INPUT_MODE_OPTIMIZ),
    MD("MD", Project.INPUT_MODE_MD),
    DOS("DOS", Project.INPUT_MODE_DOS),
    BAND("Band", Project.INPUT_MODE_BAND),
    PHONON("Phonon", Project.INPUT_MODE_PHONON);

    private static final String VAR_PROC = "$NP";
    private static final String VAR_INPUT = "$IN";

    private static final String PROP_KEY_MPIRUN = "command_mpirun";
    private static final String PROP_KEY_PWSCF = "command_pwscf";
    private static final String PROP_KEY_DOS = "command_dos";
    private static final String PROP_KEY_PROJWFC = "command_projwfc";
    private static final String PROP_KEY_BAND = "command_band";
    private static final String PROP_KEY_PHONON = "command_phonon";
    private static final String PROP_KEY_Q2R = "command_q2r";
    private static final String PROP_KEY_MATDYN = "command_matdyn";
    private static final String PROP_KEY_PLOTBAND = "command_plotband";

    private String label;

    private int inputMode;

    private RunningType(String label, int inputMode) {
        this.label = label;
        this.inputMode = inputMode;
    }

    @Override
    public String toString() {
        return label;
    }

    public static RunningType getRunningType(Project project) {
        if (project == null) {
            return null;
        }

        int inputMode = project.getInputMode();

        switch (inputMode) {
        case Project.INPUT_MODE_SCF:
            return SCF;

        case Project.INPUT_MODE_OPTIMIZ:
            return OPTIMIZ;

        case Project.INPUT_MODE_MD:
            return MD;

        case Project.INPUT_MODE_DOS:
            return DOS;

        case Project.INPUT_MODE_BAND:
            return BAND;
        case Project.INPUT_MODE_PHONON:
            return PHONON;
            
        default:
            return null;
        }
    }

    public QEInput getQEInput(Project project) {
        if (project == null) {
            return null;
        }

        QEInput srcInput = null;
        QEInput dstInput = null;

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            srcInput = project.getQEInputScf();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            srcInput = project.getQEInputOptimiz();
            break;

        case Project.INPUT_MODE_MD:
            srcInput = project.getQEInputMd();
            break;

        case Project.INPUT_MODE_DOS:
            srcInput = project.getQEInputDos();
            break;

        case Project.INPUT_MODE_BAND:
            srcInput = project.getQEInputBand();
            break;
            
        case Project.INPUT_MODE_PHONON:
            srcInput = project.getQEInputPhonon();
            break;

        default:
            srcInput = null;
            break;
        }

        if (srcInput != null) {
            dstInput = srcInput.copy();
        }

        if (dstInput != null) {
            this.modifyQEInput(dstInput, project);
        }

        return dstInput;
    }

    private void modifyQEInput(QEInput input, Project project) {
        //System.out.println("input modification function");
        if (input == null) {
            //System.out.println("input is null");
            return;
        }

        if (project == null) {
            //System.out.println("project is null");
            return;
        }

        String fileName = project.getRelatedFileName();
        fileName = fileName == null ? null : fileName.trim();
        if (fileName != null) {
            //System.out.println("related file name = "+fileName);
        } else {
            //System.out.println("no related file name");
        }

        String prefix = project.getPrefixName();
        prefix = prefix == null ? null : prefix.trim();
        if (prefix == null) {
            //System.out.println("no prefix");
        } else {
            //System.out.println("prefix = "+prefix);
        }

        QENamelist nmlControl = input.getNamelist(QEInput.NAMELIST_CONTROL);
        if (nmlControl != null) {
            if (fileName != null && (!fileName.isEmpty())) {
                nmlControl.setValue("title = " + fileName + "(" + this.label + ")");
            }

            if (prefix != null && (!prefix.isEmpty())) {
                nmlControl.setValue("prefix = " + prefix);
            }

            if (nmlControl.getValue("wf_collect") == null) {
                nmlControl.setValue("wf_collect = .TRUE.");
            }

            nmlControl.setValue("outdir = .");
            nmlControl.setValue("wfcdir = .");
            nmlControl.setValue("pseudo_dir = " + Environments.getPseudosPath());
            //DEBUG
            //System.out.println(nmlControl.getValue("pseudo_dir").getCharacterValue());
        }

        QENamelist nmlDos = input.getNamelist(QEInput.NAMELIST_DOS);
        if (nmlDos != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlDos.setValue("prefix = " + prefix + "");
                nmlDos.setValue("fildos = " + prefix + ".dos");
            }

            nmlDos.setValue("outdir = .");
        }

        QENamelist nmlProjwfc = input.getNamelist(QEInput.NAMELIST_PROJWFC);
        if (nmlProjwfc != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlProjwfc.setValue("prefix = " + prefix + "");
                nmlProjwfc.setValue("filpdos = " + prefix + "");
            }

            nmlProjwfc.setValue("outdir = .");
        }

        QENamelist nmlBand = input.getNamelist(QEInput.NAMELIST_BANDS);
        if (nmlBand != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlBand.setValue("prefix = " + prefix + "");
                nmlBand.setValue("filband = " + prefix + ".band1");
                nmlBand.setValue("spin_component = 1");
            }

            nmlBand.setValue("outdir = .");
        }
        //System.out.println("testing inputph namelist");
        QENamelist nmlPh = input.getNamelist(QEInput.NAMELIST_INPUTPH);
        if(nmlPh!=null){
            if(prefix!=null&&(!prefix.isEmpty())){
                nmlPh.setValue("prefix = "+prefix+"");
                
            } else {
                //System.out.println("prefix is null");
            }
            nmlPh.setValue("fildyn = dyn");
            nmlPh.setValue("fildrho = matdrho");
            nmlPh.setValue("fildvscf = matdvscf");
            nmlPh.setValue("outdir = .");
            if((nmlPh.getValueBuffer("dvscf_star%open").hasValue())&&(nmlPh.getValueBuffer("dvscf_star%open").getLogicalValue())){
                nmlPh.setValue("dvscf_star%dir = .");
                nmlPh.setValue("dvscf_star%ext = .dvscf");
            }
            if((nmlPh.getValueBuffer("drho_star%open").hasValue())&&(nmlPh.getValueBuffer("drho_star%open").getLogicalValue())){
                nmlPh.setValue("drho_star%dir = .");
                nmlPh.setValue("drho_star%ext = .drho");
            }
        } else {
            //System.out.println("inputph namelist is null");
        }
        
        QENamelist nmlIn = input.getNamelist(QEInput.NAMELIST_INPUT);
        if (nmlIn!=null){
            if (nmlIn.getValue("fildyn")!=null) {
                nmlIn.setValue("fildyn = dyn");
            }
            nmlIn.setValue("flfrc = q2r.out");
            if (nmlIn.getValue("flfrq")!=null) {
                nmlIn.setValue("flfrq = matdyn.out");
            }
            if (nmlIn.getValue("q_in_band_form")!=null){
                nmlIn.setValue("q_in_band_form = .TRUE.");
            }
        }
        
        QENamelist nmlPB = input.getNamelist(QEInput.NAMELIST_PLOTBANDS);//used to get gnuplot data from matdyn output
        if (nmlPB!=null) {
            //not modified after instanced
        }
    }

    public List<String[]> getCommandList(String fileName) {
        return this.getCommandList(fileName, 1);
    }

    public List<String[]> getCommandList(String fileName, int numProc) {
        String fileName2 = fileName == null ? null : fileName.trim();
        if (fileName2 == null || fileName2.isEmpty()) {
            return null;
        }

        int numProc2 = Math.max(1, numProc);

        String[] command = null;
        List<String[]> commandList = new ArrayList<String[]>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
        case Project.INPUT_MODE_OPTIMIZ:
        case Project.INPUT_MODE_MD:
            // pw.x
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_DOS:
            // pw.x (scf)
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // pw.x (nscf)
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // dos.x
            command = this.createCommand(PROP_KEY_DOS, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // projwfc.x
            command = this.createCommand(PROP_KEY_PROJWFC, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_BAND:
            // pw.x (scf)
            //fileName2="espresso.scf.in";
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // pw.x (bands)
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //fileName2=fileName.trim();
            // bands.x (up spin)
            command = this.createCommand(PROP_KEY_BAND, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // bands.x (down spin)
            command = this.createCommand(PROP_KEY_BAND, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;
        
        case Project.INPUT_MODE_PHONON:
            //run required commands for phonon calculation
            //pw.x (scf)
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //ph.x
            command = this.createCommand(PROP_KEY_PHONON, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //q2r.x
            command = this.createCommand(PROP_KEY_Q2R, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //matdyn.x
            command = this.createCommand(PROP_KEY_MATDYN, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //plotand.x
            command = this.createCommand(PROP_KEY_PLOTBAND, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }
            //run all the others? can only run some of the others if the right options were selected for ph.x
            break;
        default:
            // NOP
            break;
        }

        return commandList;
    }

    private String[] createCommand(String propKey, String fileName, int numProc) {
        if (propKey == null) {
            return null;
        }

        if (fileName == null) {
            return null;
        }

        String strMain = Environments.getProperty(propKey);
        if (strMain == null) {
            return null;
        }
        
        if (!propKey.equals(PROP_KEY_PLOTBAND)&&!propKey.equals(PROP_KEY_PHONON)){
            String strMPI = numProc < 2 ? null : Environments.getProperty(PROP_KEY_MPIRUN);
            if (strMPI != null) {
                strMain = strMPI + " " + strMain;
            }
        }

        String[] command = strMain.trim().split("\\s+");
        if (command != null) {
            for (int i = 0; i < command.length; i++) {
                if (VAR_PROC.equals(command[i])) {
                    command[i] = Integer.toString(numProc);
                } else if (VAR_INPUT.equals(command[i])) {
                    command[i] = fileName;
                }
            }
        }

        return command;
    }

    public List<RunningCondition> getConditionList() {
        List<RunningCondition> conditionList = new ArrayList<RunningCondition>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_MD:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_DOS:
            conditionList.add((project, input) -> {
                ProjectProperty projectProperty = project == null ? null : project.getProperty();
                if (projectProperty == null) {
                    return true;
                }

                ProjectStatus projectStatus = projectProperty.getStatus();
                if (projectStatus == null) {
                    return true;
                }

                if (projectStatus.isScfDone() || projectStatus.isOptDone()) {
                    return false;
                } else {
                    return true;
                }
            });

            conditionList.add((project, input) -> true);
            conditionList.add((project, input) -> true);
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_BAND:
            conditionList.add((project, input) -> {
                ProjectProperty projectProperty = project == null ? null : project.getProperty();
                if (projectProperty == null) {
                    return true;
                }

                ProjectStatus projectStatus = projectProperty.getStatus();
                if (projectStatus == null) {
                    return true;
                }

                if (projectStatus.isScfDone() || projectStatus.isOptDone()) {
                    return false;
                } else {
                    return true;
                }
            });

            conditionList.add((project, input) -> true);
            conditionList.add((project, input) -> true);

            conditionList.add((project, input) -> {
                QENamelist nmlSystem = input == null ? null : input.getNamelist(QEInput.NAMELIST_SYSTEM);
                if (nmlSystem == null) {
                    return false;
                }

                QEValue value = nmlSystem.getValue("nspin");
                int nspin = value == null ? 1 : value.getIntegerValue();
                return nspin == 2;
            });

            break;
            
        case Project.INPUT_MODE_PHONON:
            conditionList.add((project, input) -> {//condition for pw.x to run
                ProjectProperty projectProperty = project == null ? null : project.getProperty();
                if (projectProperty == null) {
                    return true;
                }

                ProjectStatus projectStatus = projectProperty.getStatus();
                if (projectStatus == null) {
                    return true;
                }

                if (projectStatus.isScfDone() || projectStatus.isOptDone()) {
                    return false;
                } else {
                    return true;
                }
            });
            conditionList.add((project, input) -> true);//condition for ph.x
            conditionList.add((project, input) -> true);//condition for q2r.x
            conditionList.add((project, input) -> true);//condition for matdyn.x
            conditionList.add((project, input) -> true);//condition for plotband.x
            break;

        default:
            // NOP
            break;
        }

        return conditionList;
    }

    public List<InputEditor> getInputEditorList(Project project) {
        if (project == null) {
            //System.out.println("project is null");
            return null;
        }

        List<InputEditor> editorList = new ArrayList<InputEditor>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_MD:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_DOS:
            editorList.add((input) -> {
                QEInput input2 = project.getQEInputScf();
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });

            editorList.add((input) -> input);
            editorList.add((input) -> input);
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_BAND:
            editorList.add((input) -> {
                QEInput input2 = project.getQEInputScf();
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });

            editorList.add((input) -> input);
            editorList.add((input) -> input);

            editorList.add((input) -> {
                QEInput input2 = input == null ? null : input.copy();
                QENamelist nmlBand = input2 == null ? null : input2.getNamelist(QEInput.NAMELIST_BANDS);
                if (nmlBand != null) {
                    QEValue value = nmlBand.getValue("filband");
                    String filband = value == null ? null : value.getCharacterValue();
                    if (filband != null && (!filband.isEmpty())) {
                        filband = filband.substring(0, filband.length() - 1) + "2";
                        nmlBand.setValue("filband = " + filband);
                    }
                    nmlBand.setValue("spin_component = 2");
                }
                return input2;
            });

            break;
            
        case Project.INPUT_MODE_PHONON:
            editorList.add((input) -> {//scf.x
                QEInput input2 = project.getQEInputScf();
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });

            editorList.add((input) -> {
                //System.out.println("adding ph.x input to editorList");
                QEInput input2 = project.getQEInputPhonon();
                if (input2 == null) {
                    //System.out.println("failed to get phonon input");
                }
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    //System.out.println("modifying input");
                    this.modifyQEInput(input3, project);
                } else {
                    //System.out.println("input failed to copy");
                }
                return input3;
            });//ph.x
            editorList.add((input) -> {//q2r.x
                    QEInput input2 = project.getQEInputQ2R();
                    QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                    return input3;
            });
            editorList.add((input) -> {//matdyn.x
                QEInput input2 = project.getQEInputMatdyn();
                QEInput input3 = input2 == null ? null : input2.copy();
                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });
            editorList.add((input) -> {//plotband.x
                QEInput input2 = project.getQEInputPlotband();
                //System.out.println("input2 ="+input2.toString());
                /*QEInput input3 = input2 == null ? null : input2.copy();
                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }*/
                //System.out.println("input3 ="+input3.toString());
                return input2;
            });
            break;

        default:
            // NOP
            break;
        }

        return editorList;
    }

    public List<LogParser> getParserList(Project project) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        List<LogParser> parserList = new ArrayList<LogParser>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            parserList.add(new ScfParser(projectProperty));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            parserList.add(new GeometryParser(projectProperty, false));
            break;

        case Project.INPUT_MODE_MD:
            parserList.add(new GeometryParser(projectProperty, true));
            break;

        case Project.INPUT_MODE_DOS:
            parserList.add(new ScfParser(projectProperty));
            parserList.add(new FermiParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;

        case Project.INPUT_MODE_BAND:
            parserList.add(new ScfParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            parserList.add(new BandPathParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;
            
        case Project.INPUT_MODE_PHONON:
            parserList.add(new ScfParser(projectProperty));//pw.x
            parserList.add(new PhParser(projectProperty));//ph.x
            parserList.add(new VoidParser(projectProperty));//q2d.x
            parserList.add(new VoidParser(projectProperty));//matdyn.x
            parserList.add(new PhPlotbandParser(projectProperty));//plotband.x
            break;

        default:
            // NOP
            break;
        }

        return parserList;
    }

    public List<PostOperation> getPostList() {
        List<PostOperation> postList = new ArrayList<PostOperation>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_MD:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_DOS:
            postList.add((project) -> {
                if (project != null) {
                    this.setProjectStatus(project, Project.INPUT_MODE_SCF);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_BAND:
            postList.add((project) -> {
                if (project != null) {
                    this.setProjectStatus(project, Project.INPUT_MODE_SCF);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                if (project != null) {
                    this.setupSymmetricKPoints(project);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            break;
            
        case Project.INPUT_MODE_PHONON:
            postList.add((project) -> {//pw.x
                if (project != null) {
                    this.setProjectStatus(project, Project.INPUT_MODE_SCF);
                }
                return;
            });
            postList.add((project) -> {//ph.x
                return;
            });
            postList.add((project) -> {//q2r.x
                return;
            });
            postList.add((project) -> {//matdyn.x
                return;
            });
            postList.add((project) -> {//plotband.x
                if (project != null) {
                    this.setupSymmetricPhononKPoints(project);
                }
                return;
            });
            break;

        default:
            // NOP
            break;
        }

        return postList;
    }

    private void setupSymmetricPhononKPoints(Project project) {
        // take QEKPoints from band interface
        QEInput input = project == null ? null : project.getQEInputBand();
        input = input == null ? null : input.copy();
        if (input == null) {
            return;
        }

        QEKPoints kpoints = null;
        QECard card = input.getCard(QEKPoints.CARD_NAME);
        if (card != null && card instanceof QEKPoints) {
            kpoints = (QEKPoints) card;
        }

        if (kpoints == null || kpoints.numKPoints() < 1) {
            return;
        }

        // keep ProjectPhononPaths
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectPhononPaths projectPhononPaths = projectProperty.getPhononPaths();
        if (projectPhononPaths == null || projectPhononPaths.numPoints() < 1) {
            return;
        }

        // copy QEKPoints -> ProjectPhononPaths
        synchronized (projectPhononPaths) {
            int numData = projectPhononPaths.numPoints();
            if (kpoints.numKPoints() < numData) {
                return;
            }

            for (int i = 0; i < numData; i++) {
                QEKPoint kpoint = kpoints.getKPoint(i);

                String klabel = null;
                if (kpoint != null && kpoint.hasLetter()) {
                    klabel = kpoint.getLetter();
                }

                if (klabel != null && !(klabel.isEmpty())) {
                    if (klabel.equalsIgnoreCase("gG")) {
                        klabel = "Γ";
                    } else if (klabel.equalsIgnoreCase("gS")) {
                        klabel = "Σ";
                    } else if (klabel.equalsIgnoreCase("gS1")) {
                        klabel = "Σ1";
                    }

                    projectPhononPaths.setLabel(i, klabel);
                }
            }
        }

        projectProperty.savePhononPaths();
    }
    
    private void setupSymmetricKPoints(Project project) {
        // keep QEKPoints
        QEInput input = project == null ? null : project.getQEInputBand();
        input = input == null ? null : input.copy();
        if (input == null) {
            return;
        }

        QEKPoints kpoints = null;
        QECard card = input.getCard(QEKPoints.CARD_NAME);
        if (card != null && card instanceof QEKPoints) {
            kpoints = (QEKPoints) card;
        }

        if (kpoints == null || kpoints.numKPoints() < 1) {
            return;
        }

        // keep ProjectBandPaths
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectBandPaths projectBandPaths = projectProperty.getBandPaths();
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        // copy QEKPoints -> ProjectBandPaths
        synchronized (projectBandPaths) {
            int numData = projectBandPaths.numPoints();
            if (kpoints.numKPoints() < numData) {
                return;
            }

            for (int i = 0; i < numData; i++) {
                QEKPoint kpoint = kpoints.getKPoint(i);

                String klabel = null;
                if (kpoint != null && kpoint.hasLetter()) {
                    klabel = kpoint.getLetter();
                }

                if (klabel != null && !(klabel.isEmpty())) {
                    if (klabel.equalsIgnoreCase("gG")) {
                        klabel = "Γ";
                    } else if (klabel.equalsIgnoreCase("gS")) {
                        klabel = "Σ";
                    } else if (klabel.equalsIgnoreCase("gS1")) {
                        klabel = "Σ1";
                    }

                    projectBandPaths.setLabel(i, klabel);
                }
            }
        }

        projectProperty.saveBandPaths();
    }

    public void setProjectStatus(Project project) {
        this.setProjectStatus(project, this.inputMode);
    }

    private void setProjectStatus(Project project, int inputMode) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null) {
            return;
        }

        switch (inputMode) {
        case Project.INPUT_MODE_SCF:
            projectStatus.updateScfCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            projectStatus.updateOptDone();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_MD:
            projectStatus.updateMdCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_DOS:
            projectStatus.updateDosCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_BAND:
            projectStatus.updateBandDone();
            projectProperty.saveStatus();
            break;
            
        case Project.INPUT_MODE_PHONON:
            projectStatus.updatePhDone();
            projectProperty.saveStatus();
            break;
        }
        
    }
}
