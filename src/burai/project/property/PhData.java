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
package burai.project.property;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class PhData {
    private static final long INIT_TIME_STAMP = 0L;
    private File file;
    private long timeStamp;
    private boolean preLoading;
    private Object preLoadingLock;
    private List<Point> points;
    
    public PhData(File file){
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }
        this.file = file;
        this.timeStamp = INIT_TIME_STAMP;
        this.preLoading = false;
        this.preLoadingLock = new Object();

        this.points = null;
        this.reload();
    }
    public synchronized int numPoints() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.points == null ? 0 : this.points.size();
    }
    private boolean reloadTimeStamp() {
        long timeStamp2 = INIT_TIME_STAMP;

        try {
            if (this.file.exists()) {
                timeStamp2 = this.file.lastModified();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (this.timeStamp != INIT_TIME_STAMP && this.timeStamp == timeStamp2) {
            return false;
        }

        this.timeStamp = timeStamp2;
        return true;
    }
    
    private static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public boolean reload() {
        if (!this.reloadTimeStamp()) {
            return false;
        }

        Thread thread = new Thread(() -> {
            try {
                this.reloadKernel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        synchronized (this.preLoadingLock) {
            this.preLoading = true;
        }

        thread.start();

        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private synchronized void reloadKernel() throws IOException {

        synchronized (this.preLoadingLock) {
            this.preLoading = false;
            this.preLoadingLock.notifyAll();
        }

        BufferedReader reader = null;

        try {
            String line = null;
            reader = new BufferedReader(new FileReader(this.file));

            if (this.points == null) {
                this.points = new ArrayList<Point>();
            } else {
                this.points.clear();
            }

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] subLines = line.split("[\\s,]+");
                try {
                    double x = Double.parseDouble(subLines[0]);
                    double y = Double.parseDouble(subLines[1]);
                    this.points.add(new Point(x, y));
                } catch (Exception e) {
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

                } catch (Exception e3) {
                    throw e3;
                }
            }
        }
    }
    
    public synchronized double getX(int i) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).x;
    }

    public synchronized double getY(int i) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).y;
    }
    
    @Override
    public String toString() {
        return this.file.toString();
    }

    @Override
    public int hashCode() {
        return this.file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        return this.file.equals(((PhData) obj).file);
    }
}
