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

package burai.app.project.editor.input.items;

import javafx.scene.control.ToggleButton;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXToggleInteger extends QEFXToggleButton<Integer> {

    public QEFXToggleInteger(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        super(valueBuffer, controlItem, defaultSelected);
    }

    public QEFXToggleInteger(QEValueBuffer valueBuffer,
            ToggleButton controlItem, boolean defaultSelected, int onValue, int offValue) {
        this(valueBuffer, controlItem, defaultSelected);

        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }
    
    /*edited by Jason D'Netto*/
    /*adding a constructor with only an on value so that it can be used in a group*/
    public QEFXToggleInteger(QEValueBuffer valueBuffer,
            ToggleButton controlItem, boolean defaultSelected, int onValue) {
        this(valueBuffer, controlItem, defaultSelected);

        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return null;
            }
        });
    }
    

    @Override
    protected void setToValueBuffer(Integer value) {
        if (value != null) {
            this.valueBuffer.setValue(value.intValue());
        }
    }

    @Override
    protected boolean setToControlItem(Integer value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.intValue() == qeValue.getIntegerValue()) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
    
    public ToggleButton getControlItem(){
        return this.controlItem;
    }
}
