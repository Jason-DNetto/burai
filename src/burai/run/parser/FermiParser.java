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

import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;

public class FermiParser extends LogParser {

    private ProjectEnergies fermiEnergies;

    public FermiParser(ProjectProperty property) {
        super(property);

        this.fermiEnergies = this.property.getFermiEnergies();
    }

    @Override
    public void parse(File file, File inpfile) throws IOException {
        parse(file);//inpFile not used
    }
    @Override
    public void parse(File file) throws IOException {
        if (this.fermiEnergies != null) {
            this.fermiEnergies.clearEnergies();
        }

        try {
            this.parseKernel(file);

        } catch (IOException e) {
            if (this.fermiEnergies != null) {
                this.fermiEnergies.clearEnergies();
            }
            throw e;

        } finally {
            this.property.saveFermiEnergies();
        }
    }

    private void parseKernel(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String strFermi = null;
                if (line.startsWith("the Fermi energy")) {
                    String[] subLines = line.split("\\s+");
                    if (subLines != null && subLines.length > 4) {
                        strFermi = subLines[4];
                    }
                }

                if (strFermi != null) {
                    try {
                        double fermi = Double.parseDouble(strFermi);
                        if (this.fermiEnergies != null) {
                            this.fermiEnergies.addEnergy(fermi);
                        }
                    } catch (NumberFormatException e) {
                        // NOP
                    }
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
