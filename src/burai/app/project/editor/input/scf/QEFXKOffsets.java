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
package burai.app.project.editor.input.scf;

import burai.app.project.editor.input.items.FXToggleInteger;
import burai.app.project.editor.input.items.QEFXItem;
import burai.input.card.QEKPoints;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXKOffsets {
    private QEKPoints card;
    private Label kOffLabel;
    private FXToggleInteger[] kOffsets;
    private Button kOffButton;
    private double TOGGLE_WIDTH = 60.0;
    public QEFXKOffsets(QEKPoints card, ToggleButton[] kOffsets, Label kOffLabel, Button kOffButton){
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }
        if (kOffsets == null || kOffsets.length != 3) {
            throw new IllegalArgumentException("kOffsets is incorrect.");
        }
        for (int i = 0; i < 3; i++) {
            if (kOffsets[i] == null) {
                throw new IllegalArgumentException("kOffsets is incorrect.");
            }
        }
        if (kOffLabel == null) {
            throw new IllegalArgumentException("kOffLabel is null.");
        }
        if (kOffButton == null) {
            throw new IllegalArgumentException("kOffButton is null.");
        }
        this.card = card;
        this.kOffsets = new FXToggleInteger[3];
        for (int i=0;i<3;i++){
            this.kOffsets[i] = new FXToggleInteger(kOffsets[i],false,TOGGLE_WIDTH);
        }
        this.kOffLabel = kOffLabel;
        this.kOffButton = kOffButton;
        this.initialize();
    }
    
    public void setDisable(boolean disable) {
        for (int i = 0; i < 3; i++) {this.kOffsets[i].setDisable(disable);}
        this.kOffLabel.setDisable(disable);
        this.kOffButton.setDisable(disable);
    }
    
    private void initialize(){
        this.setupKOffsets();
        this.setupKOffButton();
    }
    private void setupKOffsets(){
        for (int i=0;i<3;i++){
            this.setupKOffset(i);
        }
    }
    private void setupKOffset(int i) {
        if (this.kOffsets[i] == null) {
            return;
        }
        this.kOffsets[i].getControlItem().selectedProperty().addListener(o->{
            int[] newKOffsets = new int[3];
            for (int loop=0;loop<3;loop++){
                newKOffsets[loop] = (int)kOffsets[loop].getValue();
            }
            this.card.setKOffset(newKOffsets);
        });    
    }
    public void setDefault(EventHandler<ActionEvent> handler) {
        this.kOffButton.setOnAction(handler);
    }
    private void setupKOffButton() {
        QEFXItem.setupDefaultButton(this.kOffButton);
    }
    public Button getButton(){
        return this.kOffButton;
    }
    public void setOff(){
        int[] newKOffsets = new int[3];
            for (int loop=0;loop<3;loop++){
                newKOffsets[loop] = 0;
            }
        this.card.setKOffset(newKOffsets);
    }
    public void setOn(){
        int[] newKOffsets = new int[3];
            for (int loop=0;loop<3;loop++){
                newKOffsets[loop] = 1;
            }
        this.card.setKOffset(newKOffsets);
    }
}
