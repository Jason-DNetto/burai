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

package burai.app.project.viewer.result.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class GraphProperty {

    private boolean avail;

    private int calcID;

    private String title;

    private String xLabel;

    private boolean xAuto;

    private double xLower;

    private double xUpper;

    private double xTick;

    private String yLabel;

    private boolean yAuto;

    private double yLower;

    private double yUpper;

    private double yTick;

    private List<SeriesProperty> seriesList;

    public GraphProperty() {
        this.avail = true;
        this.calcID = -1;
        this.title = "TITLE";
        this.xLabel = "X-AXIS";
        this.xAuto = true;
        this.xLower = 0.0;
        this.xUpper = 0.0;
        this.xTick = -1.0;
        this.yLabel = "Y-AXIS";
        this.yAuto = true;
        this.yLower = 0.0;
        this.yUpper = 0.0;
        this.yTick = -1.0;
        this.seriesList = null;
    }
    
    public GraphProperty(double[] XLim, double[] YLim) {
        this.avail = true;
        this.calcID = -1;
        this.title = "TITLE";
        this.xLabel = "X-AXIS";
        this.xAuto = false;
        this.xLower = XLim[0];
        this.xUpper = XLim[1];
        this.xTick = -1.0;
        this.yLabel = "Y-AXIS";
        this.yAuto = false;
        this.yLower = YLim[0];
        this.yUpper = YLim[1];
        this.yTick = -1.0;
        this.seriesList = null;
    }

    public int getCalcID() {
        return this.calcID;
    }

    public void setCalcID(int calcID) {
        this.calcID = calcID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXLabel() {
        return this.xLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public boolean isXAuto() {
        return this.xAuto;
    }

    public void setXAuto(boolean xAuto) {
        this.xAuto = xAuto;
    }

    public double getXLower() {
        return this.xLower;
    }

    public void setXLower(double xLower) {
        this.xLower = xLower;
    }

    public double getXUpper() {
        return this.xUpper;
    }

    public void setXUpper(double xUpper) {
        this.xUpper = xUpper;
    }

    public double getXTick() {
        return this.xTick;
    }

    public void setXTick(double xTick) {
        this.xTick = xTick;
    }

    public String getYLabel() {
        return this.yLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public boolean isYAuto() {
        return this.yAuto;
    }

    public void setYAuto(boolean yAuto) {
        this.yAuto = yAuto;
    }

    public double getYLower() {
        return this.yLower;
    }

    public void setYLower(double yLower) {
        this.yLower = yLower;
    }

    public double getYUpper() {
        return this.yUpper;
    }

    public void setYUpper(double yUpper) {
        this.yUpper = yUpper;
    }

    public double getYTick() {
        return this.yTick;
    }

    public void setYTick(double yTick) {
        this.yTick = yTick;
    }

    public int numSeries() {
        return this.seriesList == null ? 0 : this.seriesList.size();
    }

    public SeriesProperty getSeries(int i) {
        if (this.seriesList == null || i < 0 || i >= this.seriesList.size()) {
            throw new IndexOutOfBoundsException("incorrect index of seriesList: " + i + ".");
        }

        return this.seriesList.get(i);
    }

    public void removeSeries(int i) {
        if (this.seriesList == null || i < 0 || i >= this.seriesList.size()) {
            throw new IndexOutOfBoundsException("incorrect index of seriesList: " + i + ".");
        }

        this.seriesList.remove(i);
    }

    public void addSeries(SeriesProperty series) {
        if (series == null) {
            throw new IllegalArgumentException("series is null.");
        }

        if (this.seriesList == null) {
            this.seriesList = new ArrayList<SeriesProperty>();
        }

        this.seriesList.add(series);
    }

    @Override
    public String toString() {
        return this.title;
    }

    public void readFile(File file) throws IOException {
        if (file == null) {
            return;
        }

        GraphProperty obj = null;

        Reader reader = null;

        try {
            if (!file.isFile()) {
                return;
            }

            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            obj = gson.<GraphProperty> fromJson(reader, GraphProperty.class);

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        if (obj != null && obj.avail) {
            this.calcID = obj.calcID;
            this.title = obj.title;
            this.xLabel = obj.xLabel;
            this.xAuto = obj.xAuto;
            this.xLower = obj.xLower;
            this.xUpper = obj.xUpper;
            this.xTick = obj.xTick;
            this.yLabel = obj.yLabel;
            this.yAuto = obj.yAuto;
            this.yLower = obj.yLower;
            this.yUpper = obj.yUpper;
            this.yTick = obj.yTick;

            if (this.seriesList != null && obj.seriesList != null) {
                int numSeries = Math.min(this.seriesList.size(), obj.seriesList.size());
                for (int i = 0; i < numSeries; i++) {
                    SeriesProperty series1 = this.seriesList.get(i);
                    if (series1 == null) {
                        continue;
                    }

                    SeriesProperty series2 = obj.seriesList.get(i);
                    if (series2 == null) {
                        continue;
                    }

                    series1.setColor(series2.getColor());
                    series1.setWidth(series2.getWidth());
                    series1.setDash(series2.getDash());
                    series1.setWithSymbol(series2.isWithSymbol());
                }
            }
        }
    }

    public void writeFile(File file) throws IOException {
        if (file == null) {
            return;
        }

        Writer writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(this, writer);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }
}
