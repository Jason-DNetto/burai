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
package burai.input.card;

import burai.com.math.Calculator;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class QEQPoint {
    private double x;
    private double y;
    private double z;
    boolean qplot;
    private double weight = 1.0;
    
    public QEQPoint(double x, double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;
        this.qplot=false;
        //System.out.println("making Q point at ["+x+","+y+","+z+"]");
    }
    
    public QEQPoint(double x, double y, double z, double weight){
        this(x,y,z);
        this.weight=weight;
        this.qplot=true;
        //System.out.println("making Q point at ["+x+","+y+","+z+"] with weight "+weight);
    }
    
    public QEQPoint(String line, boolean option){
        this.qplot=option;
        if (line == null) {
            throw new IllegalArgumentException("line is null.");
        }

        String[] subLines = line.trim().split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IllegalArgumentException("line is incorrect: " + line.trim());
        }
        
        if(this.qplot){
            try {
                this.x = Calculator.expr(subLines[0]);
                this.y = Calculator.expr(subLines[1]);
                this.z = Calculator.expr(subLines[2]);
                this.weight = Calculator.expr(subLines[3]);
                //System.out.println("making Q point at ["+x+","+y+","+z+"] with weight "+weight);
            } catch (Exception e) {
                throw new IllegalArgumentException("line is incorrect: " + line.trim());
            }
        }
        else{
            try {
                this.x = Calculator.expr(subLines[0]);
                this.y = Calculator.expr(subLines[1]);
                this.z = Calculator.expr(subLines[2]);
                //System.out.println("making Q point at ["+x+","+y+","+z+"]");
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
    
    public boolean getOption(){
        return this.qplot;
    }

    @Override
    public String toString() {
        if(this.qplot){
            return String.format("%10.6f %10.6f %10.6f %10.6f", this.x, this.y, this.z, this.weight);
        }
        else{
            return String.format("%10.6f %10.6f %10.6f", this.x, this.y, this.z);
        }
    }
}
