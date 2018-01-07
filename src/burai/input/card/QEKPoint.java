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

package burai.input.card;

import burai.com.math.Calculator;

public class QEKPoint {

    private double x;

    private double y;

    private double z;

    private double weight;

    private String letter;

    public QEKPoint(String letter, double x, double y, double z, double weight) {
        if (weight < 0.0) {
            throw new IllegalArgumentException("weight is negative.");
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = weight;
        this.letter = letter == null ? null : letter.trim();
    }

    public QEKPoint(double x, double y, double z, double weight) {
        this(null, x, y, z, weight);
    }

    public QEKPoint(String letter, double weight) {
        this(letter, 0.0, 0.0, 0.0, weight);

        if (this.letter == null || this.letter.isEmpty()) {
            throw new IllegalArgumentException("letter is empty.");
        }
    }

    public QEKPoint(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line is null.");
        }

        String[] subLines = line.trim().split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IllegalArgumentException("line is incorrect: " + line.trim());
        }

        boolean letterMode = false;
        try {
            Calculator.expr(subLines[0]);
        } catch (Exception e) {
            letterMode = true;
        }

        if (!letterMode) {
            try {
                this.x = Calculator.expr(subLines[0]);
                this.y = Calculator.expr(subLines[1]);
                this.z = Calculator.expr(subLines[2]);
                this.weight = Calculator.expr(subLines[3]);
                this.letter = null;
            } catch (Exception e) {
                throw new IllegalArgumentException("line is incorrect: " + line.trim());
            }

        } else {
            try {
                this.x = 0.0;
                this.y = 0.0;
                this.z = 0.0;
                this.weight = Calculator.expr(subLines[1]);
                this.letter = subLines[0].trim();
            } catch (Exception e) {
                throw new IllegalArgumentException("line is incorrect: " + line.trim());
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getWeight() {
        return this.weight;
    }
    
    public void setX(double value){
        this.x=value;
    }
    public void setY(double value){
        this.y=value;
    }
    public void setZ(double value){
        this.z=value;
    }
    public void setWeight(double value){
        this.weight=value;
    }
    
    public void setLetter(String letter){
        this.letter=letter;
    }

    public String getLetter() {
        return this.letter;
    }

    public boolean hasLetter() {
        return !(this.letter == null || this.letter.isEmpty());
    }

    public String toString(boolean asInteger) {
        int number = 0;
        if (asInteger) {
            number = (int) (Math.rint(this.weight) + 0.1);
        }

        if (!this.hasLetter()) {
            if (asInteger) {
                return String.format("%10.6f %10.6f %10.6f  %d", this.x, this.y, this.z, number);
            } else {
                return String.format("%10.6f %10.6f %10.6f %10.6f", this.x, this.y, this.z, this.weight);
            }

        } else {
            if (asInteger) {
                return String.format("%-5s  %d", this.letter, number);
            } else {
                return String.format("%-5s %10.6f", this.letter, this.weight);
            }
        }
    }

    @Override
    public String toString() {
        return this.toString(false);
    }
}
