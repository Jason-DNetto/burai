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

import java.util.ArrayList;
import java.util.List;

public class ProjectBandPaths {

    private static final String DEFAULT_LABEL = "";

    private List<Point> points;

    public ProjectBandPaths() {
        this.points = null;
    }

    public synchronized void clearBandPaths() {
        if (this.points != null) {
            this.points.clear();
        }
    }

    public synchronized int numPoints() {
        return this.points == null ? 0 : this.points.size();
    }

    private Point getPoint(int i) throws IndexOutOfBoundsException {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i);
    }

    public synchronized double getKx(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).kx;
    }

    public synchronized double getKy(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).ky;
    }

    public synchronized double getKz(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).kz;
    }

    public synchronized double getCoordinate(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).coord;
    }

    public synchronized String getLabel(int i) throws IndexOutOfBoundsException {
        String label = this.getPoint(i).label;
        return label == null ? DEFAULT_LABEL : label;
    }

    public synchronized void removePoint(int i) throws IndexOutOfBoundsException {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        this.points.remove(i);
    }

    public synchronized void addPoint(double kx, double ky, double kz, double coord, String label) {
        if (this.points == null) {
            this.points = new ArrayList<Point>();
        }

        this.points.add(new Point(kx, ky, kz, coord, label));
    }
    
    public synchronized void addPoint(double kx, double ky, double kz, double coord) {
        if (this.points == null) {
            this.points = new ArrayList<Point>();
        }

        this.points.add(new Point(kx, ky, kz, coord));
    }

    public synchronized void setLabel(int i, String label) throws IndexOutOfBoundsException {
        this.getPoint(i).label = label;
    }

    public synchronized ProjectBandPaths copyBandPaths() {
        ProjectBandPaths other = new ProjectBandPaths();

        if (this.points == null) {
            other.points = null;

        } else {
            other.points = new ArrayList<Point>(this.points);
        }

        return other;
    }

    private static class Point {
        public double kx;
        public double ky;
        public double kz;
        public double coord;
        public String label;
        
        public Point(double kx, double ky, double kz, double coord, String label){
            this.kx = kx;
            this.ky = ky;
            this.kz = kz;
            this.coord = coord;
            this.label=label;
        }
        public Point(double kx, double ky, double kz, double coord) {
            this(kx,ky,kz,coord,DEFAULT_LABEL);
        }
    }
}
