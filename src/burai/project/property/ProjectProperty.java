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

package burai.project.property;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;

public class ProjectProperty {

    private static final String FILE_NAME_STATUS = ".burai.status";
    private static final String FILE_NAME_SCF = ".burai.elec";
    private static final String FILE_NAME_FERMI = ".burai.fermi";
    private static final String FILE_NAME_OPT = ".burai.opt";
    private static final String FILE_NAME_MD = ".burai.md";
    private static final String FILE_NAME_PATH = ".burai.path";
    private static final String FILE_NAME_PH = ".burai.ph";

    public static boolean hasStatus(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }

        try {
            File file = new File(directoryPath, FILE_NAME_STATUS);
            if (file.isFile()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private String directoryPath;

    private String prefixName;

    private ProjectStatus status;

    private ProjectEnergies scfEnergies;

    private ProjectEnergies fermiEnergies;
    
    private ProjectEnergies phFrequencies;

    private ProjectGeometryList optList;

    private ProjectGeometryList mdList;

    private ProjectDosFactory dosFactory;

    private ProjectBandPaths bandPaths;

    private ProjectBandFactory bandFactory;
    
    private ProjectPhononPaths phononPaths;
    
    private ProjectPhFactory phFactory;
    
    

    public ProjectProperty(String directoryPath, String prefixName) {
        if (directoryPath == null) {
            throw new IllegalArgumentException("directoryPath is null.");
        }

        if (prefixName == null) {
            throw new IllegalArgumentException("prefixName is null.");
        }

        this.directoryPath = directoryPath;
        this.prefixName = prefixName;

        this.status = null;
        this.scfEnergies = null;
        this.fermiEnergies = null;
        this.phFrequencies = null;
        this.optList = null;
        this.mdList = null;
        this.dosFactory = new ProjectDosFactory();
        this.dosFactory.setPath(this.directoryPath, this.prefixName);
        this.bandPaths = null;
        this.bandFactory = new ProjectBandFactory();
        this.bandFactory.setPath(this.directoryPath, this.prefixName);
        this.phononPaths = null;
        this.phFactory = new ProjectPhFactory();
        this.phFactory.setPath(this.directoryPath, this.prefixName);
    }

    public synchronized void copyProperty(ProjectProperty property) {
        if (property == null) {
            return;
        }

        this.status = property.getStatus();
        this.scfEnergies = property.getScfEnergies();
        this.fermiEnergies = property.getFermiEnergies();
        this.phFrequencies = property.getPhFrequencies();
        this.optList = property.getOptList();
        this.mdList = property.getMdList();
        this.dosFactory = property.getDosFactory();
        this.dosFactory.setPath(this.directoryPath, this.prefixName);
        this.bandPaths = property.getBandPaths();
        this.bandFactory = property.getBandFactory();
        this.bandFactory.setPath(this.directoryPath, this.prefixName);
        this.phononPaths = property.getPhononPaths();
        this.phFactory = property.getPhFactory();
        this.phFactory.setPath(this.directoryPath, this.prefixName);
    }

    public void saveProperty() {
        this.saveStatus();
        this.saveScfEnergies();
        this.saveFermiEnergies();
        this.savePhFrequencies();
        this.saveOptList();
        this.saveMdList();
        this.saveBandPaths();
        this.savePhononPaths();
    }

    public synchronized ProjectStatus getStatus() {
        if (this.status == null) {
            this.createStatus();
        }

        return this.status;
    }

    public synchronized ProjectEnergies getScfEnergies() {
        if (this.scfEnergies == null) {
            this.createScfEnergies();
        }

        return this.scfEnergies;
    }

    public synchronized ProjectEnergies getFermiEnergies() {
        if (this.fermiEnergies == null) {
            this.createFermiEnergies();
        }

        return this.fermiEnergies;
    }
    
    public synchronized ProjectEnergies getPhFrequencies() {
        if (this.phFrequencies == null) {
            this.createPhFrequencies();
        }
        return this.phFrequencies;
    }

    public synchronized ProjectGeometryList getOptList() {
        if (this.optList == null) {
            this.createOptList();
        }

        return this.optList;
    }

    public synchronized ProjectGeometryList getMdList() {
        if (this.mdList == null) {
            this.createMdList();
        }

        return this.mdList;
    }

    public synchronized ProjectDosFactory getDosFactory() {
        return this.dosFactory;
    }

    public synchronized ProjectDos getDos() {
        return this.dosFactory == null ? null : this.dosFactory.getProjectDos();
    }

    public synchronized ProjectBandPaths getBandPaths() {
        if (this.bandPaths == null) {
            this.createBandPaths();
        }

        return this.bandPaths;
    }

    public synchronized ProjectBandFactory getBandFactory() {
        return this.bandFactory;
    }

    public synchronized ProjectBand getBand() {
        return this.bandFactory == null ? null : this.bandFactory.getProjectBand();
    }
    
    public synchronized ProjectPhononPaths getPhononPaths() {
        if (this.phononPaths == null) {
            this.createPhononPaths();
        }
        return this.phononPaths;
    }
    
    public synchronized ProjectPh getPh(){
        return this.phFactory == null ? null : this.phFactory.getProjectPh();
    }
    
    public synchronized ProjectPhFactory getPhFactory(){
        return this.phFactory;
    }

    private void createStatus() {
        try {
            this.status = this.<ProjectStatus> readFile(FILE_NAME_STATUS, ProjectStatus.class);
        } catch (IOException e) {
            this.status = null;
        }

        if (this.status == null) {
            this.status = new ProjectStatus();
        }
    }

    private void createScfEnergies() {
        try {
            this.scfEnergies = this.<ProjectEnergies> readFile(FILE_NAME_SCF, ProjectEnergies.class);
        } catch (IOException e) {
            this.scfEnergies = null;
        }

        if (this.scfEnergies == null) {
            this.scfEnergies = new ProjectEnergies();
        }
    }

    private void createFermiEnergies() {
        try {
            this.fermiEnergies = this.<ProjectEnergies> readFile(FILE_NAME_FERMI, ProjectEnergies.class);
        } catch (IOException e) {
            this.fermiEnergies = null;
        }

        if (this.fermiEnergies == null) {
            this.fermiEnergies = new ProjectEnergies();
        }
    }
    
    private void createPhFrequencies() {
        try {
            this.phFrequencies = this.<ProjectEnergies> readFile(FILE_NAME_PH, ProjectEnergies.class);
        } catch (IOException e) {
            this.phFrequencies = null;
        }

        if (this.phFrequencies == null) {
            this.phFrequencies = new ProjectEnergies();
        }
    }

    private void createOptList() {
        try {
            this.optList = this.<ProjectGeometryList> readFile(FILE_NAME_OPT, ProjectGeometryList.class);
        } catch (IOException e) {
            this.optList = null;
        }

        if (this.optList == null) {
            this.optList = new ProjectGeometryList();
        }
    }

    private void createMdList() {
        try {
            this.mdList = this.<ProjectGeometryList> readFile(FILE_NAME_MD, ProjectGeometryList.class);
        } catch (IOException e) {
            this.mdList = null;
        }

        if (this.mdList == null) {
            this.mdList = new ProjectGeometryList();
        }
    }

    private void createBandPaths() {
        try {
            this.bandPaths = this.<ProjectBandPaths> readFile(FILE_NAME_PATH, ProjectBandPaths.class);
        } catch (IOException e) {
            this.bandPaths = null;
        }

        if (this.bandPaths == null) {
            this.bandPaths = new ProjectBandPaths();
        }
    }
    
    private void createPhononPaths() {
        try {
            this.phononPaths = this.<ProjectPhononPaths> readFile(FILE_NAME_PATH, ProjectPhononPaths.class);
        } catch (IOException e) {
            //System.out.println("failed to read ProjectPhononPaths.class");
            this.phononPaths = null;
        }

        if (this.phononPaths == null) {
            this.phononPaths = new ProjectPhononPaths();
        }
    }

    public synchronized void saveStatus() {
        if (this.status == null) {
            this.createStatus();
        }

        try {
            this.<ProjectStatus> writeFile(FILE_NAME_STATUS, this.status);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveScfEnergies() {
        try {
            this.<ProjectEnergies> writeFile(FILE_NAME_SCF, this.scfEnergies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveFermiEnergies() {
        try {
            this.<ProjectEnergies> writeFile(FILE_NAME_FERMI, this.fermiEnergies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void savePhFrequencies() {
        try {
            this.<ProjectEnergies> writeFile(FILE_NAME_PH, this.phFrequencies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveOptList() {
        try {
            this.<ProjectGeometryList> writeFile(FILE_NAME_OPT, this.optList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveMdList() {
        try {
            this.<ProjectGeometryList> writeFile(FILE_NAME_MD, this.mdList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveBandPaths() {
        try {
            this.<ProjectBandPaths> writeFile(FILE_NAME_PATH, this.bandPaths);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void savePhononPaths() {
        try{
            this.<ProjectPhononPaths> writeFile(FILE_NAME_PATH, this.phononPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T readFile(String fileName, Class<T> classT) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            //System.out.println("readFile failed, no filename");
            return null;
        }

        T objT = null;
        Reader reader = null;

        try {
            File file = new File(this.directoryPath, fileName);
            if (!file.isFile()) {
                //System.out.println("readFile failed,"+ fileName +" not a file");
                return null;
            }

            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            objT = gson.<T> fromJson(reader, classT);

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
        //System.out.println("readFile sucess");
        return objT;
    }

    private <T> void writeFile(String fileName, T objT) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        if (objT == null) {
            return;
        }

        Writer writer = null;

        try {
            File file = new File(this.directoryPath, fileName);
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(objT, writer);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }
}
