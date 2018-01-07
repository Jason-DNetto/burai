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
package burai.app.project.editor.input.phonon;

import burai.app.project.editor.input.items.QEFXItem;
import burai.input.card.QEQPoint;
import burai.input.card.QEQPoints;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXQPoints {
    private static final String SPECIAL_Q_NUMBER = "*";
    private boolean busyScreen;
    private QEQPoints card;
    private TextField[] qpointFields;
    private Label qpointLabel;
    private Button qpointButton;
    private String[] originalStyles;
    private static final double FLOATINGPOINTERROR = 1e-30;
    private Integer myindex = null;
    
    public QEFXQPoints(QEQPoints card, TextField[] qpointFields, Label qpointLabel, Button qpointButton){
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        if (qpointFields == null || (qpointFields.length != 3 && qpointFields.length != 4)) {
            if(qpointFields == null){
                throw new IllegalArgumentException("qpointFields is null.");
            }else{
                throw new IllegalArgumentException("qpointFields is incorrect. qpointFields.length is "+qpointFields.length);
            }
        }

        for (int i = 0; i < qpointFields.length; i++) {
            if (qpointFields[i] == null) {
                throw new IllegalArgumentException("qpointFields is incorrect.");
            }
        }

        if (qpointLabel == null) {
            throw new IllegalArgumentException("qpointLabel is null.");
        }

        if (qpointButton == null) {
            throw new IllegalArgumentException("qpointButton is null.");
        }

        this.busyScreen = false;
        this.card = card;
        this.qpointFields = qpointFields;
        this.qpointLabel = qpointLabel;
        this.qpointButton = qpointButton;
        this.originalStyles = null;
        this.createOriginalStyles();
        this.setupQpointFields();
        this.setupQpointButton();
    }
    public QEFXQPoints(QEQPoints card, TextField[] qpointFields, Label qpointLabel, Button qpointButton, int index){
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        if (qpointFields == null || (qpointFields.length != 3 && qpointFields.length != 4)) {
            if(qpointFields == null){
                throw new IllegalArgumentException("qpointFields is null.");
            }else{
                throw new IllegalArgumentException("qpointFields is incorrect. qpointFields.length is "+qpointFields.length);
            }
        }

        for (int i = 0; i < qpointFields.length; i++) {
            if (qpointFields[i] == null) {
                throw new IllegalArgumentException("qpointFields is incorrect.");
            }
        }

        if (qpointLabel == null) {
            throw new IllegalArgumentException("qpointLabel is null.");
        }

        if (qpointButton == null) {
            throw new IllegalArgumentException("qpointButton is null.");
        }

        this.busyScreen = false;
        this.card = card;
        this.qpointFields = qpointFields;
        this.qpointLabel = qpointLabel;
        this.qpointButton = qpointButton;
        this.originalStyles = null;
        this.myindex = (Integer)index;
        this.createOriginalStyles();
        this.setupQpointFields();
        this.setupQpointButton();
    }
    
    public void setDisable(boolean disable) {
        for (int i = 0; i < qpointFields.length; i++) {
            this.qpointFields[i].setDisable(disable);
        }
        this.qpointLabel.setDisable(disable);
        this.qpointButton.setDisable(disable);
    }
    private void createOriginalStyles() {
        this.originalStyles = new String[qpointFields.length];
        for (int i = 0; i < qpointFields.length; i++) {
            this.originalStyles[i] = this.qpointFields[i].getStyle();
        }
    }
    private void setupQpointFields() {
        //this.updateQpointFields();

        for (int i = 0; i < qpointFields.length; i++) {
            this.setupQpointField(i);
        }
    }
    /*private void updateQpointFields() {
        for (int i = 0; i < qpointFields.length; i++) {
            this.qpointFields[i].setText(SPECIAL_Q_NUMBER);
        }
    }*/
    private void textChanged(int i){
        this.updateQpointFieldStyle(i);
        double[] qGrid;
        if(this.qpointFields.length==3){qGrid = new double[3];}
        else{qGrid = new double[4];}
        try {
            qGrid[0] = Double.parseDouble(this.qpointFields[0].getText());
            //qGrid[0] = Math.max(0, Integer.parseInt(this.qpointFields[0].getText()));
        } catch (Exception e) {qGrid[0] = -1.0;}
        try {
            qGrid[1] = Double.parseDouble(this.qpointFields[1].getText());
            //qGrid[1] = Math.max(0, Integer.parseInt(this.qpointFields[1].getText()));
        } catch (Exception e) {qGrid[1] = -1.0;}
        try {
            qGrid[2] = Double.parseDouble(this.qpointFields[2].getText());
            //qGrid[2] = Math.max(0, Integer.parseInt(this.qpointFields[2].getText()));
        } catch (Exception e) {qGrid[2] = -1.0;}
        if(qpointFields.length==4){
            try {
                qGrid[3] = Double.parseDouble(this.qpointFields[3].getText());
                //qGrid[3] = Math.max(0, Integer.parseInt(this.qpointFields[3].getText()));
            } catch (Exception e) {qGrid[3] = -1.0;}
        }
        /*double qMult;
        if(qpointFields.length==3){
            qMult = qGrid[0] * qGrid[1] * qGrid[2];
            
        }else{
            qMult = qGrid[0] * qGrid[1] * qGrid[2] * qGrid[3];
        }*/
        //if (qMult >= 0) {
        if(qGrid[0]>=0&&qGrid[1]>=0&&qGrid[2]>=0){
            this.busyScreen = true;
            if(myindex==null){
                if(qpointFields.length==3){
                    myindex = this.card.addQPoint(new QEQPoint(qGrid[0],qGrid[1],qGrid[2]));
                }
                else{
                    if(qGrid[3]>=0){
                    myindex = this.card.addQPoint(new QEQPoint(qGrid[0],qGrid[1],qGrid[2],qGrid[3]));}
                }
            }
            else{
                if(qpointFields.length==3){
                    this.card.setQPoint(myindex.intValue()-1,new QEQPoint(qGrid[0],qGrid[1],qGrid[2]));
                }
                else{
                    if(qGrid[3]>=0){
                    this.card.setQPoint(myindex.intValue()-1,new QEQPoint(qGrid[0],qGrid[1],qGrid[2],qGrid[3]));}
                }
            }
            this.busyScreen = false;
        }
    }
    
    private void setupQpointField(int i) {
        TextField qpointField = this.qpointFields[i];
        if (qpointField == null) {return;}
        this.updateQpointFieldStyle(i);
        qpointField.setTooltip(new Tooltip("0 < nq" + (i + 1) + " < Inf."));
        qpointField.setOnInputMethodTextChanged(o->textChanged(i));;
        qpointField.textProperty().addListener(o -> textChanged(i));
    }
    public void setDefault(EventHandler<ActionEvent> handler) {
        this.qpointButton.setOnAction(handler);
    }
    private void setupQpointButton() {
        QEFXItem.setupDefaultButton(this.qpointButton);
    }
    private void updateQpointFieldStyle(int i) {
        TextField qpointField = this.qpointFields[i];
        if (qpointField == null) {
            return;
        }
        String qpointStyle = this.originalStyles[i];
        if (SPECIAL_Q_NUMBER.equals(qpointField.getText())) {
            qpointStyle = QEFXItem.WARNING_STYLE;

        } else {
            try {
                Double.parseDouble(qpointField.getText());
            } catch (Exception e) {
                qpointStyle = QEFXItem.ERROR_STYLE;
            }
        }
        qpointField.setStyle(qpointStyle);
    }
}
