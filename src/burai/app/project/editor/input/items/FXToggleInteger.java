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
package burai.app.project.editor.input.items;

import javafx.scene.control.ToggleButton;

/**
 *
 * @author Jason D'Netto
 */
public class FXToggleInteger extends FXToggleButton<Integer>{
    int onValue;
    int offValue;
    public FXToggleInteger(ToggleButton controlItem, boolean defaultSelected) {
        super(controlItem, defaultSelected);
        this.onValue=1;
        this.offValue=0;
    }
    public FXToggleInteger(ToggleButton controlItem, boolean defaultSelected, double customWidth) {
        super(controlItem, defaultSelected,customWidth);
        this.onValue=1;
        this.offValue=0;
    }
    public FXToggleInteger(ToggleButton controlItem, boolean defaultSelected, int onValue, int offValue) {
        super(controlItem, defaultSelected);
        this.onValue=onValue;
        this.offValue=offValue;
        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }
    public FXToggleInteger(ToggleButton controlItem, boolean defaultSelected, int onValue, int offValue, double customWidth) {
        super(controlItem, defaultSelected,customWidth);
        this.onValue=onValue;
        this.offValue=offValue;
        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }
    public FXToggleInteger(ToggleButton controlItem, boolean defaultSelected, int onValue, int offValue, double customWidth, double customHeight) {
        super(controlItem, defaultSelected,customWidth,customHeight);
        this.onValue=onValue;
        this.offValue=offValue;
        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }
    
    public ToggleButton getControlItem(){
        return this.controlItem;
    }
    public Integer getValue(){
        if (this.controlItem.isSelected()) {
            return (Integer)this.onValue;
        }
        else{return (Integer)this.offValue;}
    }
    @Override
    protected boolean setToControlItem(Integer value, boolean selected) {
        if (value == null) {return false;}

        this.controlItem.setSelected(selected);
        return true;
    }
}
