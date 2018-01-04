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

package burai.app.project.editor.input.items;

import javafx.scene.control.ToggleButton;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXToggleBoolean extends QEFXToggleButton<Boolean> {

    public QEFXToggleBoolean(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        this(valueBuffer, controlItem, defaultSelected, false);
    }

    public QEFXToggleBoolean(
            QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected, boolean inverse) {
        super(valueBuffer, controlItem, defaultSelected);

        if (inverse) {
            this.setValueFactory(selected -> {
                return !selected;
            });
        }
    }
    
    /*Edited by Jason D'Netto
    adding custom widths for ToggleBoolean*/
    
    public QEFXToggleBoolean(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected, double customWidth) {
        this(valueBuffer, controlItem, defaultSelected, false,customWidth);
    }
    
    public QEFXToggleBoolean(
            QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected, boolean inverse, double customWidth) {
        super(valueBuffer, controlItem, defaultSelected,customWidth);

        if (inverse) {
            this.setValueFactory(selected -> {
                return !selected;
            });
        }
    }

    @Override
    protected void setToValueBuffer(Boolean value) {
        if (value != null) {
            this.valueBuffer.setValue(value.booleanValue());
        }
    }

    @Override
    protected boolean setToControlItem(Boolean value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.booleanValue() == qeValue.getLogicalValue()) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
}
