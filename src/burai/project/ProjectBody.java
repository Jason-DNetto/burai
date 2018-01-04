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

package burai.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import burai.atoms.model.Cell;
import burai.atoms.reader.AtomsReader;
import burai.atoms.reader.QEReader;
import burai.input.QEBandInput;
import burai.input.QEDOSInput;
import burai.input.QEGeometryInput;
import burai.input.QEInput;
import burai.input.QEInputReader;
import burai.input.QEMDInput;
import burai.input.QEMatdynInput;
import burai.input.QEOptInput;
import burai.input.QEPhononInput;
import burai.input.QEPlotbandInput;
import burai.input.QEQ2RInput;
import burai.input.QESCFInput;
import burai.input.QESecondaryInput;
import burai.input.card.QEKPoints;
import burai.input.card.QEQPoints;
import burai.project.property.ProjectProperty;
import java.nio.file.Files;

public class ProjectBody extends Project {

    private static final String PREFIX_NAME = "espresso";
    private static final String FILE_NAME_INP = "espresso.in";
    private static final String FILE_NAME_LOG = "espresso.log";
    private static final String FILE_NAME_ERR = "espresso.err";
    private static final String FILE_NAME_GEOM = "espresso.geom.in";
    private static final String FILE_NAME_SCF = "espresso.scf.in";
    private static final String FILE_NAME_OPT = "espresso.opt.in";
    private static final String FILE_NAME_MD = "espresso.md.in";
    private static final String FILE_NAME_DOS = "espresso.dos.in";
    private static final String FILE_NAME_BAND = "espresso.band.in";
    private static final String FILE_NAME_PHONON = "espresso.phonon.in";
    private static final String FILE_NAME_PH_Q2R = "espresso.q2r.in";
    private static final String FILE_NAME_MATDYN = "espresso.matdyn.in";
    private static final String FILE_NAME_PLOTBAND = "espresso.plotband.in";

    private String prefixName;
    private String inpFileName;
    private String logFileName;
    private String errFileName;

    private InputData geomData;
    private InputData scfData;
    private InputData optData;
    private InputData mdData;
    private InputData dosData;
    private InputData bandData;
    private InputData phononData;
    private InputData q2rData;
    private InputData matdynData;
    private InputData plotbandData;

    private Cell cell;

    private ProjectProperty property;

    private String mapQEInputs;

    protected ProjectBody(String rootFilePath, String directoryPath) throws IOException {
        super(rootFilePath, directoryPath);

        if (this.getDirectoryPath() != null) {
            if (!this.createDirectory()) {
                throw new IOException("cannot create directory: " + this.getDirectoryPath());
            }
        }

        this.createFileNames();

        this.createInputData();

        this.cell = null;

        this.property = null;

        this.mapQEInputs = null;

        if (this.getRootFilePath() != null) {
            this.buildFromRootFile();
        } else if (this.getDirectoryPath() != null) {
            this.buildFromDirectory();
        }

        this.setupDirectoryAction();
    }

    private void createFileNames() {
        this.prefixName = PREFIX_NAME;
        this.inpFileName = FILE_NAME_INP;
        this.logFileName = FILE_NAME_LOG;
        this.errFileName = FILE_NAME_ERR;
    }

    private void createInputData() {
        this.geomData = new InputData(FILE_NAME_GEOM);
        this.scfData = new InputData(FILE_NAME_SCF);
        this.optData = new InputData(FILE_NAME_OPT);
        this.mdData = new InputData(FILE_NAME_MD);
        this.dosData = new InputData(FILE_NAME_DOS);
        this.bandData = new InputData(FILE_NAME_BAND);
        this.phononData = new InputData(FILE_NAME_PHONON);
        this.q2rData = new InputData(FILE_NAME_PH_Q2R);
        this.matdynData = new InputData(FILE_NAME_MATDYN);
        this.plotbandData = new InputData(FILE_NAME_PLOTBAND);
    }

    private boolean createDirectory() {
        return this.createDirectory(this.getDirectoryPath());
    }

    private boolean createDirectory(String directoryPath) {
        boolean status = true;
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            status = status && directory.mkdirs();
        }
        if (!directory.canExecute()) {
            status = status && directory.setExecutable(true);
        }
        if (!directory.canRead()) {
            status = status && directory.setReadable(true);
        }
        if (!directory.canWrite()) {
            status = status && directory.setWritable(true);
        }

        return status;
    }

    private void setupDirectoryAction() {
        this.addOnFilePathChanged(filePath -> {
            if (this.getDirectoryPath() == null) {
                this.property = null;
                return;
            }

            if (this.getPrefixName() == null) {
                this.property = null;
                return;
            }

            if (!this.createDirectory()) {
                this.property = null;
                return;
            }

            ProjectProperty property = new ProjectProperty(this.getDirectoryPath(), this.getPrefixName());
            if (this.property != null) {
                property.copyProperty(this.property);
            }

            this.property = property;
        });
    }

    private void buildFromRootFile() throws IOException {
        File rootFile = new File(this.getRootFilePath());

        try {
            if (!rootFile.exists()) {
                throw new IOException("no such file: " + rootFile);
            }
            if (!rootFile.canRead()) {
                throw new IOException("cannot read file: " + rootFile);
            }
        } catch (IOException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new IOException(e2);
        }

        AtomsReader reader = AtomsReader.getInstance(rootFile.getPath());
        if (reader == null) {
            throw new IOException("cannot create an AtomsReader.");
        }

        try {
            this.cell = reader.readCell();
        } catch (IOException e) {
            //e.printStackTrace();
            throw e;
        } finally {
            reader.close();
        }

        if (reader instanceof QEReader) {
            QEInput input = ((QEReader) reader).getInput();
            if (input == null) {
                throw new IOException("cannot get QEInput from QEReader.");
            }
            this.setupInputGenerators(rootFile, input.getReader());
            this.geomData.setQEInput(input);

        } else {
            this.setupInputGenerators(null, null);
            this.geomData.setQEInput(new QEGeometryInput(this.cell));
        }
    }

    private void buildFromDirectory() throws IOException {
        this.setupInputGenerators(null, null);
        this.geomData.loadQEInput(this);

        QEInput input = this.geomData.getQEInput();
        if (input != null && input instanceof QEGeometryInput) {
            this.cell = ((QEGeometryInput) input).getCell();
        }

        if (this.getDirectoryPath() != null && this.getPrefixName() != null) {
            this.property = new ProjectProperty(this.getDirectoryPath(), this.getPrefixName());
        }
    }

    private void setupInputGenerators(File inputFile, QEInputReader inputReader) {
        this.geomData.setInputGenerator(file -> {
            QEInput input = null;
            if (file != null) {
                input = new QEGeometryInput(file);
            } else if (inputReader != null) {
                input = new QEGeometryInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEGeometryInput(inputFile);
            } else {
                input = new QEGeometryInput();
            }

            return input;
        });

        this.scfData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QESCFInput(file);
            } else if (inputReader != null) {
                input = new QESCFInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QESCFInput(inputFile);
            } else {
                input = new QESCFInput();
            }

            QEInput parentInput = this.geomData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.optData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEOptInput(file);
            } else if (inputReader != null) {
                input = new QEOptInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEOptInput(inputFile);
            } else {
                input = new QEOptInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.mdData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEMDInput(file);
            } else if (inputReader != null) {
                input = new QEMDInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEMDInput(inputFile);
            } else {
                input = new QEMDInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.dosData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEDOSInput(file);
            } else if (inputReader != null) {
                input = new QEDOSInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEDOSInput(inputFile);
            } else {
                input = new QEDOSInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.bandData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEBandInput(file);
            } else if (inputReader != null) {
                input = new QEBandInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEBandInput(inputFile);
            } else {
                input = new QEBandInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });
        
        this.phononData.setInputGenerator(file ->{
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEPhononInput(file);
            } else if (inputReader != null) {
                input = new QEPhononInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEPhononInput(inputFile);
            } else {
                input = new QEPhononInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });
        
        this.q2rData.setInputGenerator(file ->{
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEQ2RInput(file);
            } else if (inputReader != null) {
                input = new QEQ2RInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEQ2RInput(inputFile);
            } else {
                input = new QEQ2RInput();
            }

            QEInput parentInput = this.phononData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }
            
            return input;
        });
        
        this.matdynData.setInputGenerator(file ->{
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEMatdynInput(file);
            } else if (inputReader != null) {
                input = new QEMatdynInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEMatdynInput(inputFile);
            } else {
                input = new QEMatdynInput();
            }

            QEInput parentInput = this.q2rData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }
            if (this.bandData!=null){
                if(this.bandData.getQEInput() != null){
                    if (this.bandData.getQEInput().getCard(QEKPoints.CARD_NAME)!= null) {
                        this.bandData.getQEInput().getCard(QEKPoints.CARD_NAME).copyTo(input.getCard(QEKPoints.CARD_NAME));
                    } else {
                        //System.out.println("bandData K card does not exist");
                    }
                } else {
                    //System.out.println("bandData input does not exist");
                }
            } else {
                //System.out.println("bandData does not exist");
            }
            return input;
        });
        
        this.plotbandData.setInputGenerator(file ->{
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEPlotbandInput(file);
            } else if (inputReader != null) {
                input = new QEPlotbandInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEPlotbandInput(inputFile);
            } else {
                input = new QEPlotbandInput();
            }
            QEInput parentInput = this.matdynData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }
            
            return input;
        });
    }

    @Override
    public void setNetProject(Project project) {
        return;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isSameAs(Project project) {
        if (project == null) {
            return false;
        }

        if (project instanceof ProjectBody) {
            return this == project;
        }

        return project.isSameAs(this);
    }

    @Override
    public ProjectProperty getProperty() {
        return this.property;
    }

    @Override
    public String getPrefixName() {
        return this.prefixName;
    }

    @Override
    public String getInpFileName() {
        return this.inpFileName;
    }

    @Override
    public String getLogFileName(int i) {
        return this.logFileName + "." + i;
    }

    @Override
    public String getErrFileName(int i) {
        return this.errFileName + "." + i;
    }

    @Override
    public String getExitFileName() {
        return this.prefixName + ".EXIT";
    }

    @Override
    public QEInput getQEInputGeometry() {
        if (this.getDirectoryPath() != null) {
            this.geomData.requestQEInput(this);
        }

        return this.geomData.getQEInput();
    }

    @Override
    public QEInput getQEInputScf() {
        if (this.getDirectoryPath() != null) {
            this.scfData.requestQEInput(this);
        }

        return this.scfData.getQEInput();
    }

    @Override
    public QEInput getQEInputOptimiz() {
        if (this.getDirectoryPath() != null) {
            this.optData.requestQEInput(this);
        }

        return this.optData.getQEInput();
    }

    @Override
    public QEInput getQEInputMd() {
        if (this.getDirectoryPath() != null) {
            this.mdData.requestQEInput(this);
        }

        return this.mdData.getQEInput();
    }

    @Override
    public QEInput getQEInputDos() {
        if (this.getDirectoryPath() != null) {
            this.dosData.requestQEInput(this);
        }

        return this.dosData.getQEInput();
    }

    @Override
    public QEInput getQEInputBand() {
        if (this.getDirectoryPath() != null) {
            this.bandData.requestQEInput(this);
        }

        return this.bandData.getQEInput();
    }
    
    @Override
    public QEInput getQEInputPhonon(){
        if (this.getDirectoryPath() != null) {
            this.phononData.requestQEInput(this);
        } else {
            //System.out.println("directory path is null");
        }
        
        return this.phononData.getQEInput();
    }
    
    @Override
    public QEInput getQEInputQ2R(){
        if (this.getDirectoryPath() != null) {
            this.q2rData.requestQEInput(this);
        }
        return this.q2rData.getQEInput();
    }
    
    @Override
    public QEInput getQEInputMatdyn(){
        if (this.getDirectoryPath() != null) {
            this.matdynData.requestQEInput(this);
        }
        return this.matdynData.getQEInput();
    }
    
    @Override
    public QEInput getQEInputPlotband(){
        if (this.getDirectoryPath() != null) {
            this.plotbandData.requestQEInput(this);
        }
        return this.plotbandData.getQEInput();
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    protected void loadQEInputs() {
        if (this.getDirectoryPath() != null) {
            this.geomData.requestQEInput(this);
            this.scfData.requestQEInput(this);
            this.optData.requestQEInput(this);
            this.mdData.requestQEInput(this);
            this.dosData.requestQEInput(this);
            this.bandData.requestQEInput(this);
            this.phononData.requestQEInput(this);
            this.q2rData.requestQEInput(this);
            this.matdynData.requestQEInput(this);
            this.plotbandData.requestQEInput(this);
        }
    }

    @Override
    public void resolveQEInputs() {
        this.loadQEInputs();
        this.geomData.resolveQEInput();
        this.scfData.resolveQEInput();
        this.optData.resolveQEInput();
        this.mdData.resolveQEInput();
        this.dosData.resolveQEInput();
        this.bandData.resolveQEInput();
        this.phononData.resolveQEInput();
        this.q2rData.resolveQEInput();
        this.matdynData.resolveQEInput();
        this.plotbandData.resolveQEInput();
    }

    @Override
    public void markQEInputs() {
        QEInput input = null;
        this.mapQEInputs = "";

        input = this.getQEInputGeometry();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[geometry]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }

        input = this.getQEInputScf();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[scf]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }

        input = this.getQEInputOptimiz();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[optimiz]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }

        input = this.getQEInputMd();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[md]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }

        input = this.getQEInputDos();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[dos]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }

        input = this.getQEInputBand();
        if (input != null) {
            this.mapQEInputs = this.mapQEInputs + "[band]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }
        
        input = this.getQEInputPhonon();
        if(input!=null){
            this.mapQEInputs = this.mapQEInputs + "[phonon]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }
        
        input = this.getQEInputQ2R();
        if(input!=null){
            this.mapQEInputs = this.mapQEInputs + "[q2r]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }
        
        input = this.getQEInputMatdyn();
        if(input!=null){
            this.mapQEInputs = this.mapQEInputs + "[matdyn]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }
        
        input = this.getQEInputPlotband();
        if(input!=null){
            this.mapQEInputs = this.mapQEInputs + "[plotband]" + System.lineSeparator();
            this.mapQEInputs = this.mapQEInputs + input.toString() + System.lineSeparator();
        }
    }

    @Override
    public boolean isQEInputChanged() {
        String mapQEInputsOld = this.mapQEInputs;
        this.markQEInputs();
        String mapQEInputsNew = this.mapQEInputs;

        boolean changed = false;
        if (mapQEInputsOld == null) {
            changed = (mapQEInputsOld != mapQEInputsNew);
        } else {
            changed = (!mapQEInputsOld.equals(mapQEInputsNew));
        }

        this.mapQEInputs = mapQEInputsOld;
        return changed;
    }

    @Override
    public void saveQEInputs(String directoryPath) {
        String directoryPath2 = directoryPath == null ? null : directoryPath.trim();
        if (directoryPath2 == null || directoryPath2.isEmpty()) {
            directoryPath2 = this.getDirectoryPath();
        }

        if (directoryPath2 == null || directoryPath2.isEmpty()) {
            return;
        }

        if (!directoryPath2.equals(this.getDirectoryPath())) {
            this.loadQEInputs();
            this.setDirectoryPath(directoryPath2);
        }

        /* Edited by Jason D'Netto 06/07/17
        if a directory does not already exist, try to create it,
        exit if trying fails, 
        not because you cant create something that already legitimately exists
        */
        if (!(new File(directoryPath2).exists())) {
            if (!this.createDirectory(directoryPath2)) {
                return;
            }
        }

        this.resolveQEInputs();
        this.markQEInputs();

        this.saveQEInput(this.geomData.getFileName(), this.getQEInputGeometry());
        this.saveQEInput(this.scfData.getFileName(), this.getQEInputScf());
        this.saveQEInput(this.optData.getFileName(), this.getQEInputOptimiz());
        this.saveQEInput(this.mdData.getFileName(), this.getQEInputMd());
        this.saveQEInput(this.dosData.getFileName(), this.getQEInputDos());
        this.saveQEInput(this.bandData.getFileName(), this.getQEInputBand());
        this.saveQEInput(this.phononData.getFileName(), this.getQEInputPhonon());
        this.saveQEInput(this.q2rData.getFileName(), this.getQEInputQ2R());
        this.saveQEInput(this.matdynData.getFileName(), this.getQEInputMatdyn());
        this.saveQEInput(this.plotbandData.getFileName(), this.getQEInputPlotband());

        if (this.property != null) {
            this.property.saveProperty();
        }
    }

    private void saveQEInput(String fileName, QEInput input) {
        if (fileName == null) {
            return;
        }

        if (input == null) {
            return;
        }

        PrintWriter writer = null;
        File file = new File(this.getDirectoryPath(), fileName);

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(input.toString());

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
