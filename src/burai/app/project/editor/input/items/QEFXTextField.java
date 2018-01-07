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

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXTextField extends QEFXItem<TextField> {

    private boolean busyText;

    protected Callback<String, String> valueFactory;

    protected Callback<String, String> textFactory;

    public QEFXTextField(QEValueBuffer valueBuffer, TextField controlItem) {
        super(valueBuffer, controlItem);

        this.busyText = false;
        this.valueFactory = null;
        this.textFactory = null;
        this.setupTextField();
    }

    private void setupTextField() {
        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.controlItem.setText("");
        }

        this.controlItem.textProperty().addListener(o -> {

            this.busyText = true;

            String text = this.controlItem.getText();
            String value = text;
            if (this.valueFactory != null) {
                value = this.valueFactory.call(value);
            }

            if (value != null && !value.trim().isEmpty()) {
                this.valueBuffer.setValue(value);
            } else {
                this.valueBuffer.removeValue();
            }

            this.busyText = false;
        });
    }

    @Override
    protected void onValueChanged(QEValue value) {
        if (this.busyText) {
            return;
        }

        String text = null;
        if (value != null) {
            text = value.getCharacterValue();
        }
        if (this.textFactory != null) {
            text = this.textFactory.call(text);
        }

        if (text == null) {
            this.controlItem.setText("");
        } else {
            this.controlItem.setText(text);
        }
    }

    public void setHintMessage(String message) {
        this.controlItem.setTooltip(new Tooltip(message));
    }

    public void setValueFactory(Callback<String, String> valueFactory) {
        this.valueFactory = valueFactory;
    }

    public void setTextFactory(Callback<String, String> textFactory) {
        this.textFactory = textFactory;

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }
    
    /*added by Jason D'Netto so that the text field instance can be emptied when
    the user selects a mutually exclusive option*/
    public void emptyField(){
        this.controlItem.setText("");
    }
}
