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
public class PhPlotbandParser extends LogParser {
    
    private ProjectPhononPaths phononPaths;
    private List<String> graphLabels;
    private int pointCount;
    
    public PhPlotbandParser(ProjectProperty property){
        super(property);
        this.phononPaths = this.property.getPhononPaths();
        this.graphLabels = new ArrayList<String>();
    }
    @Override
    public void parse(File file, File inpfile) throws IOException {
        if (this.phononPaths != null) {
            this.phononPaths.clearPhononPaths();
        }

        try {
            this.parseKernel(file,inpfile);
        } catch (IOException e) {
            if (this.phononPaths != null) {
                this.phononPaths.clearPhononPaths();
            }
            throw e;

        } finally {
            this.property.savePhononPaths();
            this.property.savePhFrequencies();
        }
    }
    @Override
    public void parse(File file) throws IOException {
        parseKernel(file,null);
    }
    
    private void parseKernel(File file, File inpFile) throws IOException {
        //System.out.println("input file is "+inpFile.getPath());

        BufferedReader reader = null;

        try {
            String line = null;
            String compare_string = "K_POINTS";
            //edit by Jason D'Netto 30/06/17
            //get the list of band labels from the k point specification in inpFile, either espresso.band.in or espresso.matdyn.in
            if (inpFile!=null) {
                this.graphLabels = new ArrayList<String>();
                if (inpFile.getName().equalsIgnoreCase("espresso.matdyn.in")) {
                    compare_string = "/";//K_POINTS for espresso.band.in, / for espresso.matdun.in
                }
                BufferedReader reader2 = new BufferedReader(new FileReader(inpFile));
                Boolean reading = Boolean.FALSE;
                int skip_lines = 2;
                int skipped_lines=0;
                String graphLabel;
                while ((line = reader2.readLine()) != null){
                    if (line.trim().startsWith(compare_string)) {
                        reading=Boolean.TRUE;
                    }
                    if (reading&&line.trim().isEmpty()){reading=Boolean.FALSE;}
                    if(reading){
                        skipped_lines++;
                        if (skipped_lines>skip_lines){
                            graphLabel=line.trim().split(" ")[0];
                            this.graphLabels.add(graphLabel);
                            //System.out.println("adding GraphLabel "+graphLabel);
                        }
                    }
                }
            }
            
            reader = new BufferedReader(new FileReader(file));
            this.pointCount = 0;
            //System.out.println("number of graph labels = "+Integer.toString(this.graphLabels.size()));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String strPoint = null;
                final String header = "high-symmetry point: ";
                if (line.startsWith(header)) {
                    strPoint = line.substring(header.length());
                }

                if (strPoint == null || strPoint.length() < 21) {
                    continue;
                }
                String strKx = strPoint.substring(0, 7);
                if (strKx == null) {
                    continue;
                }

                String strKy = strPoint.substring(7, 14);
                if (strKy == null) {
                    continue;
                }

                String strKz = strPoint.substring(14, 21);
                if (strKz == null) {
                    continue;
                }

                strPoint = strPoint.substring(21);
                strPoint = strPoint == null ? null : strPoint.trim();
                if (strPoint == null || strPoint.isEmpty()) {
                    continue;
                }

                String strCoord = null;
                String[] subStr = strPoint.split("\\s+");
                if (subStr != null && subStr.length > 2) {
                    strCoord = subStr[2];
                }

                if (strCoord == null) {
                    continue;
                }
                try {
                    double kx = Double.parseDouble(strKx);
                    double ky = Double.parseDouble(strKy);
                    double kz = Double.parseDouble(strKz);
                    double coord = Double.parseDouble(strCoord);
                    if (this.phononPaths != null) {
                        //this.bandPaths.addPoint(kx, ky, kz, coord,"?");
                        if(pointCount<graphLabels.size()){
                            //DEBUG
                            ////System.out.println("this.phononPaths.addPoint("+Double.toString(kx)+","+Double.toString(ky)+","+Double.toString(kz)+","+Double.toString(coord)+","+graphLabels.get(pointCount)+")");
                            this.phononPaths.addPoint(kx, ky, kz, coord, graphLabels.get(pointCount));
                            pointCount++;
                        } else {
                            //DEBUG
                            ////System.out.println("this.phononPaths.addPoint("+Double.toString(kx)+","+Double.toString(ky)+","+Double.toString(kz)+","+Double.toString(coord)+")");
                            this.phononPaths.addPoint(kx, ky, kz, coord);
                        }
                    }
                } catch (NumberFormatException e) {
                    // NOP
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
