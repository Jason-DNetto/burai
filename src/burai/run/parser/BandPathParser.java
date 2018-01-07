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

package burai.run.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectProperty;
import java.util.ArrayList;
import java.util.List;

public class BandPathParser extends LogParser {

    private ProjectBandPaths bandPaths;

    public BandPathParser(ProjectProperty property) {
        super(property);

        this.bandPaths = this.property.getBandPaths();
    }

    @Override
    public void parse(File file) throws IOException {
        if (this.bandPaths != null) {
            this.bandPaths.clearBandPaths();
        }

        try {
            this.parseKernel(file,null);

        } catch (IOException e) {
            if (this.bandPaths != null) {
                this.bandPaths.clearBandPaths();
            }
            throw e;

        } finally {
            this.property.saveBandPaths();
        }
    }
    
    @Override
    public void parse(File file, File inpFile) throws IOException {
        if (this.bandPaths != null) {
            this.bandPaths.clearBandPaths();
        }

        try {
            this.parseKernel(file, inpFile);

        } catch (IOException e) {
            if (this.bandPaths != null) {
                this.bandPaths.clearBandPaths();
            }
            throw e;

        } finally {
            this.property.saveBandPaths();
        }
    }

    private void parseKernel(File file, File inpFile) throws IOException {

        BufferedReader reader = null;

        try {
            String line = null;
            List<String> graphLabels = new ArrayList<String>();
            //edit by Jason D'Netto 23/3/17
            //get the list of band labels from the k point specification in esprsso.band.in
            if (inpFile!=null) {
                
                BufferedReader reader2 = new BufferedReader(new FileReader(inpFile));
                Boolean reading = Boolean.FALSE;
                int skipped_lines=0;
                int skip_lines=2;
                String graphLabel;
                while ((line = reader2.readLine()) != null){
                    if (line.startsWith("K_POINTS")) {
                        reading=Boolean.TRUE;
                    }
                    if (reading&&line.trim().isEmpty()){reading=Boolean.FALSE;}
                    if(reading){
                        skipped_lines++;
                        if (skipped_lines>skip_lines){
                            graphLabel=line.split(" ")[0];
                            graphLabels.add(graphLabel);
                        }
                    }
                }
            }
            reader = new BufferedReader(new FileReader(file));
            int pointCount=0;
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
                    if (this.bandPaths != null) {
                        //this.bandPaths.addPoint(kx, ky, kz, coord,"?");
                        if(pointCount<graphLabels.size()){
                            //DEBUG
                            //System.out.println("this.bandPaths.addPoint("+Double.toString(kx)+","+Double.toString(ky)+","+Double.toString(kz)+","+Double.toString(coord)+","+graphLabels.get(pointCount)+")");
                            this.bandPaths.addPoint(kx, ky, kz, coord, graphLabels.get(pointCount));
                            pointCount++;
                        } else {
                            //DEBUG
                            //System.out.println("this.bandPaths.addPoint("+Double.toString(kx)+","+Double.toString(ky)+","+Double.toString(kz)+","+Double.toString(coord)+")");
                            this.bandPaths.addPoint(kx, ky, kz, coord);
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
