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
package burai.run.parser;

import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectPhononPaths;
import burai.project.property.ProjectProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class PhParser extends LogParser {
    private ProjectEnergies phononFrequencies;
    
    public PhParser(ProjectProperty property){
        super(property);
        this.phononFrequencies = this.property.getPhFrequencies();
    }
    @Override
    public void parse(File file, File inpfile) throws IOException {
        parse(file);//inpFile not used
        
    }
    @Override
    public void parse(File file) throws IOException {
        this.phononFrequencies.clearEnergies();
        try {
            this.parseKernel(file);
        } catch (IOException e) {
            this.phononFrequencies.clearEnergies();
            throw e;

        } finally {
            this.property.savePhFrequencies();
        }
    }
    
    private void parseYData(String strPoint){
        //split string on whitespace, get index 7 (cm-1 value)
        String[] subLines = strPoint.split("\\s+");
        String strFreq;
        if (subLines != null && subLines.length > 7) {
            strFreq = subLines[7];
        } else {return;}
        if (strFreq != null) {
            try {
                double freq = Double.parseDouble(strFreq);
                if (this.phononFrequencies != null) {
                    this.phononFrequencies.addEnergy(freq);
                }
            } catch (NumberFormatException e) {
                // NOP
            }
        }
    }
    
    private void parseKernel(File file) throws IOException {
        BufferedReader reader = null;
        try {
            String line = null;
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                final String Yheader = "freq (";
                if (line.startsWith(Yheader)){
                    if (line.contains("-->")) {continue;}//skip mode symmetry
                    parseYData(line);
                }  
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }
}
