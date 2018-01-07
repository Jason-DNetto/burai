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


import javafx.fxml.FXML;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXPh_kController extends QEFXInputController {

    @FXML
    private Label k_label;
    @FXML
    private Label nk_label;
    //monkhorst pack grid k points
    @FXML
    private TextField nk1;
    @FXML
    private TextField nk2;
    @FXML
    private TextField nk3;
    //monkhorst pack grid offsets
    @FXML
    private TextField k1;
    @FXML
    private TextField k2;
    @FXML
    private TextField k3;
    @FXML
    private Button k_button;
    private Button nonexistant_k1 = k_button;
    private Button nonexistant_k2 = k_button;
    @FXML
    private Button nk_button;
    private Button nonexistant_nk1 = nk_button;
    private Button nonexistant_nk2 = nk_button;
    
    private QEFXTextFieldInteger nmlPhnk1;
    private QEFXTextFieldInteger nmlPhnk2;
    private QEFXTextFieldInteger nmlPhnk3;
    private QEFXTextFieldInteger nmlPhk1;
    private QEFXTextFieldInteger nmlPhk2;
    private QEFXTextFieldInteger nmlPhk3;

    public QEFXPh_kController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
    }  
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlPh = this.input.getNamelist(QEInput.NAMELIST_INPUTPH);
        this.nmlPhnk1 = this.setupTextField(nmlPh,this.nk1,"nk1",this.nk_label,this.nk_button);
        this.nmlPhnk2 = this.setupTextField(nmlPh,this.nk2,"nk2",this.nk_label,this.nonexistant_nk1);
        this.nmlPhnk3 = this.setupTextField(nmlPh,this.nk3,"nk3",this.nk_label,this.nonexistant_nk2);
        this.nmlPhk1 = this.setupTextField(nmlPh,this.k1,"k1",this.k_label,this.k_button);
        this.nmlPhk2 = this.setupTextField(nmlPh,this.k2,"k2",this.k_label,this.nonexistant_k1);
        this.nmlPhk3 = this.setupTextField(nmlPh,this.k3,"k3",this.k_label,this.nonexistant_k2);
    }
    
    private QEFXTextFieldInteger setupTextField(QENamelist nmlPh, TextField controlItem, String QEVB, Label formLabel, Button defaultButton){
        if (controlItem == null) {return null;}
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer(QEVB),controlItem);
        if (formLabel != null) {item.setLabel(formLabel);}
        if (defaultButton != null) {item.setDefault(0, defaultButton);}
        item.pullAllTriggers();
        return item;
    }
    
}
