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
import javafx.util.Callback;
import burai.com.graphic.ToggleGraphics;
//import static javafx.scene.input.KeyCode.V;
/*import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;*/

public abstract class FXToggleButton<V> extends FXDummyItem<ToggleButton> {

    private static final String TOGGLE_STYLE = "-fx-base: transparent";

    private static final double GRAPHIC_WIDTH = 185.0;
    private static final double GRAPHIC_HEIGHT = 24.0;
    private static final String GRAPHIC_TEXT_YES = "yes";
    private static final String GRAPHIC_TEXT_NO = "no";
    private static final String GRAPHIC_STYLE_YES = "toggle-graphic-on";
    private static final String GRAPHIC_STYLE_NO = "toggle-graphic-off";

    private boolean defaultSelected;

    private Callback<Boolean, V> valueFactory;
    private double myWidth;
    private double myHeight;
    protected FXToggleButton(ToggleButton controlItem, boolean defaultSelected) {
        this(controlItem,defaultSelected,GRAPHIC_WIDTH,GRAPHIC_HEIGHT);
    }
    protected FXToggleButton(ToggleButton controlItem, boolean defaultSelected, double customWidth){
        this(controlItem,defaultSelected,customWidth,GRAPHIC_HEIGHT);
    }
    
    protected FXToggleButton(ToggleButton controlItem, boolean defaultSelected, double customWidth, double customHeight){
        super(controlItem);
        this.myWidth = customWidth;
        this.myHeight = customHeight;
        this.defaultSelected = defaultSelected;
        this.valueFactory = null;
        this.setupToggleGraphics();
        this.setupToggleButton();
    }

    //protected abstract void setToValueBuffer(V value);

    protected abstract boolean setToControlItem(V value, boolean selected);

    private void setupToggleGraphics() {
        this.controlItem.setText("");
        this.controlItem.setStyle(TOGGLE_STYLE);
        this.updateToggleGraphics();
        this.controlItem.selectedProperty().addListener(o -> {
            this.updateToggleGraphics();
        });
    }

    private void updateToggleGraphics() {
        if (this.controlItem.isSelected()) {
            this.controlItem.setGraphic(ToggleGraphics.getGraphic(
                    this.myWidth, this.myHeight, true, GRAPHIC_TEXT_YES, GRAPHIC_STYLE_YES));
        } else {
            this.controlItem.setGraphic(ToggleGraphics.getGraphic(
                    this.myWidth, this.myHeight, false, GRAPHIC_TEXT_NO, GRAPHIC_STYLE_NO));
        }
    }

    private void setupToggleButton() {
        /*if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.controlItem.setSelected(this.defaultSelected);
        }*/
        
        this.controlItem.setSelected(this.defaultSelected);

        /*this.controlItem.setOnAction(event -> {
            boolean selected = this.controlItem.isSelected();

            /*if (this.valueFactory == null) {
                this.valueBuffer.setValue(selected);
                return;
            }*/

            /*V backedValue = this.valueFactory.call(selected);
            if (backedValue != null) {
                this.setToValueBuffer(backedValue);
            }
        });*/
    }

    @Override
    protected void onValueChanged() {
        if (this.valueFactory == null) {
            this.controlItem.setSelected(this.controlItem.isSelected());
            return;
        }
        boolean[] selectedList = { true, false };
        for (boolean selected : selectedList) {
            V backedValue = this.valueFactory.call(selected);
            if (backedValue != null) {
                boolean hasSet = this.setToControlItem(backedValue, selected);
                if (hasSet) {
                    return;
                }
            }
        }
        this.controlItem.setSelected(this.defaultSelected);
    }

    public void setValueFactory(Callback<Boolean, V> valueFactory) {
        this.valueFactory = valueFactory;
        this.onValueChanged();
    }
}
