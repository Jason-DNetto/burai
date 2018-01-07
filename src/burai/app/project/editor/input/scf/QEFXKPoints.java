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

package burai.app.project.editor.input.scf;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import burai.app.project.editor.input.items.QEFXItem;
import burai.input.card.QECardEvent;
import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;

public class QEFXKPoints {

    private static final String SPECIAL_K_NUMBER = "*";

    private boolean busyScreen;

    private QEKPoints card;

    private TextField[] kpointFields;

    private Label kpointLabel;

    private Button kpointButton;

    private String[] originalStyles;
    
    private Integer myindex = null;

    public QEFXKPoints(QEKPoints card, TextField[] kpointFields, Label kpointLabel, Button kpointButton) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        if (kpointFields == null || (kpointFields.length != 3)&&(kpointFields.length != 4)) {
            throw new IllegalArgumentException("kpointFields is incorrect.");
        }

        for (int i = 0; i < kpointFields.length; i++) {
            if (kpointFields[i] == null) {
                throw new IllegalArgumentException("kpointFields is incorrect.");
            }
        }

        if (kpointLabel == null) {
            throw new IllegalArgumentException("kpointLabel is null.");
        }

        if (kpointButton == null) {
            throw new IllegalArgumentException("kpointButton is null.");
        }

        this.busyScreen = false;
        this.card = card;
        this.kpointFields = kpointFields;
        this.kpointLabel = kpointLabel;
        this.kpointButton = kpointButton;
        this.originalStyles = null;
        this.myindex=null;
        this.initialize();
    }
    
    public QEFXKPoints(QEKPoints card, TextField[] kpointFields, Label kpointLabel, Button kpointButton, int index) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        if (kpointFields == null || (kpointFields.length != 3)&&(kpointFields.length != 4)) {
            throw new IllegalArgumentException("kpointFields is incorrect.");
        }

        for (int i = 0; i < kpointFields.length; i++) {
            if (kpointFields[i] == null) {
                throw new IllegalArgumentException("kpointFields is incorrect.");
            }
        }

        if (kpointLabel == null) {
            throw new IllegalArgumentException("kpointLabel is null.");
        }

        if (kpointButton == null) {
            throw new IllegalArgumentException("kpointButton is null.");
        }

        this.busyScreen = false;
        this.card = card;
        this.kpointFields = kpointFields;
        this.kpointLabel = kpointLabel;
        this.kpointButton = kpointButton;
        this.originalStyles = null;
        this.myindex=(Integer)index;
        this.initialize();
    }

    public void setDisable(boolean disable) {
        for (int i = 0; i < this.kpointFields.length; i++) {
            this.kpointFields[i].setDisable(disable);
        }

        this.kpointLabel.setDisable(disable);

        this.kpointButton.setDisable(disable);
    }

    private void initialize() {
        this.createOriginalStyles();
        this.setupKpointFields();
        this.setupKpointButton();
        this.setupCardKPoints();
        this.editKPoint();
    }

    private void createOriginalStyles() {
        this.originalStyles = new String[this.kpointFields.length];
        for (int i = 0; i < this.kpointFields.length; i++) {
            this.originalStyles[i] = this.kpointFields[i].getStyle();
        }
    }

    private void setupKpointFields() {
        this.updateKpointFields();
        for (int i = 0; i < this.kpointFields.length; i++) {
            this.setupKpointField(i);
        }
    }

    private void updateKpointFieldStyle(int i) {
        TextField kpointField = this.kpointFields[i];
        if (kpointField == null) {
            return;
        }

        String kpointStyle = this.originalStyles[i];

        if (SPECIAL_K_NUMBER.equals(kpointField.getText())) {
            kpointStyle = QEFXItem.WARNING_STYLE;

        } else {
            try {
                Double.parseDouble(kpointField.getText());
            } catch (Exception e) {
                kpointStyle = QEFXItem.ERROR_STYLE;
            }
        }

        kpointField.setStyle(kpointStyle);
    }
    
    private void editKPoint(){
        double[] kGrid = new double[this.kpointFields.length];
        try {
            kGrid[0] = Double.parseDouble(this.kpointFields[0].getText());
        } catch (Exception e) {
            kGrid[0] = 0.0;
        }
        try {
            kGrid[1] = Double.parseDouble(this.kpointFields[1].getText());
        } catch (Exception e) {
            kGrid[1] = 0.0;
        }
        try {
            kGrid[2] = Double.parseDouble(this.kpointFields[2].getText());
        } catch (Exception e) {
            kGrid[2] = 0.0;
        }
        if(this.kpointFields.length==4){
            try {
                kGrid[3] = Double.parseDouble(this.kpointFields[3].getText());
            } catch (Exception e) {
                kGrid[3] = 0.0;
            }
        }
        //0 is a premitted value
        if(kGrid[0]>=0||kGrid[1]>=0||kGrid[2]>=0){
            this.busyScreen = true;
            if(this.kpointFields.length==3){
                int[] intKGrid = new int[3];
                for (int loop=0;loop<3;loop++){
                    intKGrid[loop] = (int)kGrid[loop];
                }
                this.card.setKGrid(intKGrid);
            }
            else{
                if(kGrid[3]>=0){
                    if(this.myindex!=null){
                        this.card.setKPoint((int)this.myindex,kGrid[0],kGrid[1],kGrid[2],kGrid[3]);
                    }else{
                        this.myindex = this.card.addKPoint(new QEKPoint(kGrid[0],kGrid[1],kGrid[2],kGrid[3]));
                    }
                }
            }
        }
    }

    private void setupKpointField(int i) {
        TextField kpointField = this.kpointFields[i];
        if (kpointField == null) {
            return;
        }

        this.updateKpointFieldStyle(i);

        kpointField.setTooltip(new Tooltip("0 < nk" + (i + 1) + " < Inf."));

        kpointField.textProperty().addListener(o -> {
            this.updateKpointFieldStyle(i);
            this.editKPoint();
        });
    }

    private void updateKpointFields() {
        if(this.card.isGamma()){return;}//do nothing, gamma disables K points elsewhere
        else{
            if (this.card.isAutomatic()){//automatic k points
                int[] kGrid = this.card.getKGrid();
                for (int i = 0; i < 3; i++) {
                    this.kpointFields[i].setText(String.valueOf(kGrid[i]));
                }
            }
            else{//list of k points
                if(myindex!=null){
                    QEKPoint temp = this.card.getKPoint((int)myindex);
                    if(temp!=null){
                        this.kpointFields[0].setText(String.valueOf(temp.getX()));
                        this.kpointFields[1].setText(String.valueOf(temp.getY()));
                        this.kpointFields[2].setText(String.valueOf(temp.getZ()));
                        this.kpointFields[3].setText(String.valueOf(temp.getWeight()));
                    }
                }
            }
        }
    }

    private void setupKpointButton() {
        QEFXItem.setupDefaultButton(this.kpointButton);
    }

    private void setupCardKPoints() {
        this.card.addListener(event -> {
            if (event == null) {
                return;
            }

            if (this.busyScreen) {
                return;
            }

            int eventType = event.getEventType();
            if (eventType == QECardEvent.EVENT_TYPE_KGRID_CHANGED
                    || eventType == QECardEvent.EVENT_TYPE_KPOINT_ADDED
                    || eventType == QECardEvent.EVENT_TYPE_KPOINT_CHANGED
                    || eventType == QECardEvent.EVENT_TYPE_KPOINT_REMOVED
                    || eventType == QECardEvent.EVENT_TYPE_KPOINT_CLEARED
                    || eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED
                    || eventType == QECardEvent.EVENT_TYPE_NULL) {
                this.updateKpointFields();
            }
        });
    }

    public void setDefault(EventHandler<ActionEvent> handler) {
        this.kpointButton.setOnAction(handler);
    }
}
