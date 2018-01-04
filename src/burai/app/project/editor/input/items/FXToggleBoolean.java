/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

public class FXToggleBoolean extends FXToggleButton<Boolean>{

    public FXToggleBoolean(ToggleButton controlItem, boolean defaultSelected) {
        super(controlItem, defaultSelected);
    }
    
    public void setFalse(){
        this.controlItem.selectedProperty().set(false);
    }
    
    public void setTrue(){
        this.controlItem.selectedProperty().set(true);
    }
    
    public boolean isSelected(){
        return this.controlItem.selectedProperty().getValue();
    }
    
    public ToggleButton getControlItem(){
        return this.controlItem;
    }

    public void setDefault(boolean b, Button button) {
        this.defaultButton = button;
        if (this.defaultButton != null) {
            setupDefaultButton(this.defaultButton);
            this.defaultButton.setOnAction(event -> {
                if (this.controlItem != null) {
                    this.controlItem.selectedProperty().set(b);
                    this.controlItem.requestFocus();
                }
            });
        }
    }
    
    /*@Override
    protected void setToControlItem(boolean selected){
        this.controlItem.setSelected(selected);
    }*/
    
    /*public FXToggleBoolean(
            ToggleButton controlItem, boolean defaultSelected, boolean inverse) {
        super(controlItem, defaultSelected);

        if (inverse) {
            this.setValueFactory(selected -> {
                return !selected;
            });
        }
    }*/

    @Override
    protected boolean setToControlItem(Boolean value, boolean selected) {
        if (value == null) {return false;}

        this.controlItem.setSelected(selected);
        return true;
    }
}
