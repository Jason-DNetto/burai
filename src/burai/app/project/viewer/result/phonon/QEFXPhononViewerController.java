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

package burai.app.project.viewer.result.phonon;

import burai.app.project.viewer.result.phonon.*;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;
import burai.app.project.viewer.result.graph.SeriesProperty;
import burai.com.consts.Constants;
import burai.com.env.Environments;
import burai.com.parallel.Parallel;
import burai.project.property.PhData;
import burai.project.property.ProjectPh;
import burai.project.property.ProjectPhFactory;
import burai.project.property.ProjectPhononPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectPh;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXPhononViewerController extends QEFXGraphViewerController {

    private static final int NUM_LOADING_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private static final String XAXIS_CLASS = "invisible-axis";
    private static final String COORD_CLASS = "coord-field";
    private static final double COORD_WIDTH = 40.0;
    private static final double COORD_OFFSET = 4.2;

    private static final double DELTA_COORD = 1.0e-10;
    private static final double DELTA_ENERGY = 1.0e-10;
    private static final double VLINE_BUFFER = 1.0; 

    private ProjectProperty projectProperty;
    
    private ProjectStatus projectStatus;

    private ProjectEnergies projectFrequencies;

    private ProjectPhFactory projectPhFactory;

    private ProjectPhononPaths projectPhononPaths;
    
    private PhData phData;
    
    private double[] XLim;
    private double[] YLim;

    @FXML
    private AnchorPane coordPane;

    public QEFXPhononViewerController(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }
        this.projectProperty = projectProperty;
        this.projectStatus = projectProperty.getStatus();
        this.projectFrequencies = projectProperty.getPhFrequencies();
        this.projectPhFactory = projectProperty.getPhFactory();
        this.projectPhononPaths = projectProperty.getPhononPaths();
    }

    @Override
    protected int getCalculationID() {
        if (this.projectStatus == null) {
            return 0;
        }
        return this.projectStatus.getPhCount();
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty(this.XLim,this.YLim);

        property.setTitle("Phonon");
        property.setXLabel("");
        property.setYLabel("Frequency (cm-1)");

        ProjectEnergies projectFrequencies = null;
        if (this.projectFrequencies != null) {
            projectFrequencies = this.projectFrequencies.copyEnergies();
        }
        if (projectFrequencies == null || projectFrequencies.numEnergies() < 1){
            /*if (projectFrequencies == null){
                //System.out.println("projectFrequencies is null");
            } else {
                //System.out.println("projectFrequencies.numEnergies() is < 1");
            }*/
            property.setTitle("Cant find what is used for Y-Axis!");
        }

        ProjectPhononPaths projectPhononPaths = null;
        if (this.projectPhononPaths != null) {
            projectPhononPaths = this.projectPhononPaths.copyPhononPaths();
        }

        if (this.phData != null) {
            this.createPhProperty(property);
        }

        if (projectPhononPaths != null && projectPhononPaths.numPoints() > 0) {
            this.createCoordProperty(property);
        }

        if (projectFrequencies != null && projectFrequencies.numEnergies()>0){
            this.createYAxisProperty(property);
        }
        
        return property;
    }
    
    private void createYAxisProperty(GraphProperty property) {
        if (property == null) {
            return;
        }
        
        boolean empty = (this.phData == null || this.phData.numPoints() < 1);
        if (empty) {
            return;
        }

        if ((this.YLim[1] - this.YLim[0]) >= DELTA_ENERGY) {
            property.setYAuto(false);
            property.setYLower(this.YLim[0]);
            property.setYUpper(this.YLim[1]);
            //three ticks between lower and upper limits
            property.setYTick((YLim[1]-YLim[0])/4);
        }
    }

    private void createPhProperty(GraphProperty property) {
        if (property == null) {
            return;
        }

        SeriesProperty seriesProperty = new SeriesProperty();
        seriesProperty.setName("Frequency");
        seriesProperty.setColor("blue");
        seriesProperty.setDash(SeriesProperty.DASH_NULL);
        seriesProperty.setWithSymbol(false);
        seriesProperty.setWidth(1.0);//Line Width
        property.addSeries(seriesProperty);
    }

    //private void createCoordProperty(GraphProperty property, ProjectPhononPaths projectPhononPaths) {
    private void createCoordProperty(GraphProperty property) {
        if (property == null) {
            return;
        }

        if ((this.XLim[1] - this.XLim[0]) >= DELTA_COORD) {
            property.setXAuto(false);
            property.setXLower(this.XLim[0]);
            property.setXUpper(this.XLim[1]);
        }
    }

    @Override
    protected void initializeLineChart(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }
        //get data, axis minimum and maximum
        this.phData = projectPhFactory.getProjectPh().getPhdata();
        if (this.phData == null) {return;}
        this.XLim = this.getCoordRange();
        this.YLim = this.getYRange();

        Axis<Number> xAxis = lineChart.getXAxis();
        if (xAxis == null) {
            return;
        }

        xAxis.getStyleClass().add(XAXIS_CLASS);

        xAxis.widthProperty().addListener(o -> {
            //System.out.println("detected change in xAxis width property");
            if (this.projectPhononPaths != null && this.projectPhononPaths.numPoints() > 0) {
                this.updateCoordPane(lineChart, this.projectPhononPaths);
            }
        });

        xAxis.layoutXProperty().addListener(o -> {
            //System.out.println("detected change in xAxis layout property");
            if (this.projectPhononPaths != null && this.projectPhononPaths.numPoints() > 0) {
                this.updateCoordPane(lineChart, this.projectPhononPaths);
            }
        });
        
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        lineChart.getData().clear();
        
        this.projectStatus = this.projectProperty.getStatus();
        this.projectFrequencies = this.projectProperty.getPhFrequencies();
        this.projectPhFactory = this.projectProperty.getPhFactory();
        this.projectPhononPaths = this.projectProperty.getPhononPaths();

        ProjectPh projectPh = null;
        if (this.projectPhFactory != null) {
            projectPh = this.projectPhFactory.getProjectPh();
        }

        if (projectPh != null) {
            this.phData = projectPh.getPhdata();
            this.YLim = this.getYRange();
            this.XLim = this.getCoordRange();
        }
        

        if (projectFrequencies == null || projectFrequencies.numEnergies() < 1) {
            return;
        }

        if (this.phData != null) {
            this.reloadPhData(lineChart);
        }

        if (this.projectPhononPaths != null && this.projectPhononPaths.numPoints() > 0) {
            this.reloadVLine(lineChart, this.projectPhononPaths);
        }

        if (projectPhononPaths != null && projectPhononPaths.numPoints() > 0) {
            this.updateCoordPane(lineChart, projectPhononPaths);
        }
    }

    private void updateCoordPane(LineChart<Number, Number> lineChart, ProjectPhononPaths projectPhononPaths) {
        if (this.coordPane == null) {
            return;
        }

        Axis<Number> xAxis = lineChart == null ? null : lineChart.getXAxis();
        if (xAxis == null) {
            return;
        }

        if (projectPhononPaths == null || projectPhononPaths.numPoints() < 1) {
            return;
        }

        double minCoord = this.XLim[0];
        double maxCoord = this.XLim[1];
        if ((maxCoord - minCoord) < DELTA_COORD) {
            return;
        }

        List<String> labList = new ArrayList<String>();
        List<Integer> mulList = new ArrayList<Integer>();
        List<Double> posList = new ArrayList<Double>();

        double minPos = xAxis.getLayoutX();
        double maxPos = xAxis.getLayoutX() + xAxis.getWidth();

        double coordOld = projectPhononPaths.getCoordinate(0);
        String labelOld = null;
        int multOld = 0;

        for (int i = 0; i < projectPhononPaths.numPoints(); i++) {
            double coord = 0.0;
            if ((i + 1) < projectPhononPaths.numPoints()) {
                coord = projectPhononPaths.getCoordinate(i + 1);
            } else {
                coord = projectPhononPaths.getCoordinate(i) + Math.max(1.0, 2.0 * DELTA_COORD);
            }

            String label = projectPhononPaths.getLabel(i);
            label = label == null ? "" : label;

            if (labelOld == null) {
                labelOld = label;
                multOld = 1;
            } else {
                labelOld = labelOld + " | " + label;
                multOld++;
            }

            if (Math.abs(coord - coordOld) >= DELTA_COORD) {
                labList.add(labelOld);
                mulList.add(multOld);
                posList.add(coordOld * (maxPos - minPos) / (maxCoord - minCoord) + minPos);
                coordOld = coord;
                labelOld = null;
                multOld = 0;
            }
        }

        int numList = labList.size();
        if (numList != mulList.size() || numList != posList.size()) {
            return;
        }

        this.coordPane.getChildren().clear();

        for (int i = 0; i < numList; i++) {
            String label = labList.get(i);
            if (label == null) {
                continue;
            }

            int mult = mulList.get(i);
            if (mult < 1) {
                return;
            }

            double position = posList.get(i);
            if (position < 0.0) {
                return;
            }

            TextField field = new TextField(label);
            field.getStyleClass().add(COORD_CLASS);
            field.setPrefWidth(mult * COORD_WIDTH);
            field.setLayoutX(position - 0.5 * mult * COORD_WIDTH + COORD_OFFSET);
            this.coordPane.getChildren().add(field);
        }
    }

    private void reloadPhData(LineChart<Number, Number> lineChart) {

        if (lineChart == null) {
            return;
        }

        if (this.phData == null) {
            return;
        }

        int numData = this.phData.numPoints();

        Series<Number, Number> series = new Series<Number, Number>();

        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            Data<Number, Number>[] dataList = new Data[numData];

            Integer[] indexes = new Integer[numData];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(indexes);
            parallel.setNumThreads(NUM_LOADING_THREADS);
            parallel.forEach(i -> {
                double x = this.phData.getX(i);
                //double y = this.phData.getY(i) * Constants.RYTOEV - fermi;
                double y = this.phData.getY(i);
                synchronized (dataList) {
                    dataList[i] = new Data<Number, Number>(x, y);
                }
                return null;
            });

            for (Data<Number, Number> data : dataList) {
                if (data != null) {
                    series.getData().add(data);
                }
            }
        });

        lineChart.getData().add(series);
    }

    private void reloadVLine(LineChart<Number, Number> lineChart,
            ProjectPhononPaths projectPhononPaths) {

        if (lineChart == null) {
            return;
        }

        if (projectPhononPaths == null || projectPhononPaths.numPoints() < 1) {
            return;
        }

        double coordOld = -1.0;
        //adding a vertical line for each item in projectPhononPaths which has a label
        for (int i = 0; i < projectPhononPaths.numPoints(); i++) {
            if (projectPhononPaths.getLabel(i) != null && !projectPhononPaths.getLabel(i).equals("")) {
                double coord = projectPhononPaths.getCoordinate(i);
                //System.out.println("Label "+projectPhononPaths.getLabel(i)+" at X coordinate "+coord);
                if (Math.abs(coord - coordOld) < DELTA_COORD) {
                    continue;
                }
                coordOld = coord;
                Data<Number, Number> data1 = new Data<Number, Number>(coord, this.YLim[0]);
                Data<Number, Number> data2 = new Data<Number, Number>(coord, this.YLim[1]);
                Series<Number, Number> series = new Series<Number, Number>();
                series.getData().add(data1);
                series.getData().add(data2);
                lineChart.getData().add(series);
            } /*else {
                //System.out.println("phononPaths coordinate "+Integer.toString(i)+" empty");
            }*/
        }
    }

    private double[] getCoordRange() {//getXRange
        if (this.phData == null || this.phData.numPoints() < 1) {
            return null;
        }

        double minCoord = Double.MAX_VALUE;
        double maxCoord = Double.MIN_VALUE;

        double coordOld = -1.0;
        for (int i = 0; i < this.phData.numPoints(); i++) {
            double coord = this.phData.getX(i);
            if (Math.abs(coord - coordOld) < DELTA_COORD) {
                continue;
            }
            coordOld = coord;

            minCoord = Math.min(minCoord, coord);
            maxCoord = Math.max(maxCoord, coord);
        }
        //System.out.println("X min = "+Double.toString(minCoord)+", max = "+Double.toString(maxCoord));
        return new double[] { minCoord, maxCoord };
    }

    //private double[] getYRange(PhData phData, double fermi) {
    private double[] getYRange() {
        boolean empty = (this.phData == null || this.phData.numPoints() < 1);
        if (empty) {
            //System.out.println("phData is empty");
            return null;
        }

        double minEnergy = +Double.MAX_VALUE;
        double maxEnergy = -Double.MAX_VALUE;

        if (!empty) {
            for (int i = 0; i < this.phData.numPoints(); i++) {
                double Y = this.phData.getY(i);// * Constants.RYTOEV - fermi;
                minEnergy = Math.min(minEnergy, Y);
                maxEnergy = Math.max(maxEnergy, Y);
            }
        }

        minEnergy = Math.floor(minEnergy) - VLINE_BUFFER;
        maxEnergy = Math.ceil(maxEnergy) + VLINE_BUFFER;
        //System.out.println("Y min = "+Double.toString(minEnergy)+", max = "+Double.toString(maxEnergy));
        return new double[] { minEnergy, maxEnergy };
    }
}
