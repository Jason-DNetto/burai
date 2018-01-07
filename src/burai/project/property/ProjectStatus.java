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
 * and Samantha Adnett, formerly QUT
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.project.property;

import java.util.Date;

public class ProjectStatus {

    private String date;

    private String cellAxis;
    private boolean molecule;

    private int scfCount;
    private int optCount;
    private int mdCount;
    private int dosCount;
    private int bandCount;
    private int phCount;

    public ProjectStatus() {
        this.updateDate();

        this.cellAxis = null;
        this.molecule = false;

        this.scfCount = 0;
        this.optCount = 0;
        this.mdCount = 0;
        this.dosCount = 0;
        this.bandCount = 0;
        this.phCount = 0;
    }

    private void updateDate() {
        Date objDate = new Date();
        this.date = objDate.toString();
    }

    public synchronized String getDate() {
        return this.date;
    }

    public synchronized String getCellAxis() {
        return this.cellAxis;
    }

    public synchronized void setCellAxis(String cellAxis) {
        this.cellAxis = cellAxis;
    }

    public synchronized boolean isMolecule() {
        return this.molecule;
    }

    public synchronized void setMolecule(boolean molecule) {
        this.molecule = molecule;
    }

    public synchronized boolean isScfDone() {
        return this.scfCount > 0;
    }

    public synchronized int getScfCount() {
        return this.scfCount;
    }

    public synchronized void updateScfCount() {
        this.updateDate();
        this.scfCount++;
    }

    public synchronized boolean isOptDone() {
        return this.optCount > 0;
    }

    public synchronized int getOptCount() {
        return this.optCount;
    }

    public synchronized void updateOptDone() {
        this.updateDate();
        this.optCount++;
    }

    public synchronized boolean isMdDone() {
        return this.mdCount > 0;
    }

    public synchronized int getMdCount() {
        return this.mdCount;
    }

    public synchronized void updateMdCount() {
        this.updateDate();
        this.mdCount++;
    }

    public synchronized boolean isDosDone() {
        return this.dosCount > 0;
    }

    public synchronized int getDosCount() {
        return this.dosCount;
    }

    public synchronized void updateDosCount() {
        this.updateDate();
        this.dosCount++;
    }

    public synchronized boolean isBandDone() {
        return this.bandCount > 0;
    }

    public synchronized int getBandCount() {
        return this.bandCount;
    }

    public synchronized void updateBandDone() {
        this.updateDate();
        this.bandCount++;
    }
    
    public synchronized boolean isPhDone(){
        return this.phCount > 0;
    }
    
    public synchronized int getPhCount(){
        return this.phCount;
    }
    
    public synchronized void updatePhDone(){
        this.updateDate();
        this.phCount++;
    }
}
