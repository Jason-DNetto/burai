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

package burai.run.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;

public class ScfParser extends LogParser {

    private FermiParser fermiParser;

    private ProjectEnergies scfEnergies;

    public ScfParser(ProjectProperty property) {
        super(property);

        this.fermiParser = new FermiParser(this.property);

        this.scfEnergies = this.property.getScfEnergies();
    }

    @Override
    public void parse(File file, File inpfile) throws IOException {
        parse(file);//inpFile not used
    }
    @Override
    public void parse(File file) throws IOException {
        this.fermiParser.parse(file);
        this.parseScf(file);
    }

    public void parseScf(File file) throws IOException {
        if (this.scfEnergies != null) {
            this.scfEnergies.clearEnergies();
        }

        try {
            this.parseScfKernel(file);

        } catch (IOException e) {
            if (this.scfEnergies != null) {
                this.scfEnergies.clearEnergies();
            }
            throw e;

        } finally {
            this.property.saveScfEnergies();
        }
    }

    private void parseScfKernel(File file) throws IOException {

        Deque<Energy> energyQueue1 = new LinkedList<Energy>();
        Deque<Energy> energyQueue2 = new LinkedList<Energy>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                Energy energy = this.getEnergy(line);
                if (energy != null) {
                    energyQueue1.offerLast(energy);
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

        if (energyQueue1.isEmpty()) {
            return;
        }

        Energy energy1 = energyQueue1.pollLast();
        if (energy1 != null) {
            energyQueue2.offerFirst(energy1);
        }

        while ((energy1 = energyQueue1.pollLast()) != null) {
            if (energy1.isConverged()) {
                break;
            }
            energyQueue2.offerFirst(energy1);
        }

        if (energyQueue2.isEmpty()) {
            return;
        }

        Energy energy2 = energyQueue2.peekLast();
        if (energy2 != null) {
            if (this.scfEnergies != null) {
                this.scfEnergies.setConverged(energy2.isConverged());
            }
        }

        while ((energy2 = energyQueue2.pollFirst()) != null) {
            if (this.scfEnergies != null) {
                this.scfEnergies.addEnergy(energy2.getValue());
            }
        }
    }

    private Energy getEnergy(String line) {
        if (line == null) {
            return null;
        }

        String line2 = line.trim();
        if (line2.isEmpty()) {
            return null;
        }

        double value = 0.0;
        boolean converged = false;

        if (line2.charAt(0) == '!') {
            line2 = line2.substring(1);
            line2 = line2 == null ? null : line2.trim();
            converged = true;
        }

        if (line2 != null && line2.startsWith("total energy")) {
            String[] subLines = line2.split("\\s+");
            if (subLines != null && subLines.length > 3) {
                String strValue = subLines[3];
                if (strValue != null) {
                    try {
                        value = Double.parseDouble(strValue);
                        return new Energy(value, converged);
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            }
        }

        return null;
    }

    private static class Energy {

        private double value;

        private boolean converged;

        public Energy(double value, boolean converged) {
            this.value = value;
            this.converged = converged;
        }

        public double getValue() {
            return this.value;
        }

        public boolean isConverged() {
            return this.converged;
        }
    }
}
