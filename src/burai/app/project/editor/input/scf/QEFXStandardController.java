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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.FXToggleBoolean;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.app.project.editor.input.items.QEFXToggleString;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QEKPoints;
import burai.input.correcter.CutoffCorrector;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class QEFXStandardController extends QEFXInputController {

    private static final double DELTA_ECUT = 1.0e-2;
    private static final double DEFAULT_ECUTWFC = 25.0; // 25Ry
    private static final double DEFAULT_ECUTRHO = 225.0; // 225Ry

    private QEFXTextFieldDouble ecutwfcItem;
    private QEFXTextFieldDouble ecutrhoItem;

    /*Edited by Jason D'Netto
    added to select options for K-Points, and to have a dynamically
    generated table for entering the list of k-poits */
    private static final String[] K_OPTIONS = {"automatic","gamma","crystal","crystal_b","crystal_c","tpiba","tpiba_b","tpiba_c"};
    //form elements for k-options combo box
    @FXML
    private Label kOptsLabel;
    @FXML
    private ComboBox<String> kOpts;
    @FXML
    private Button kOptsButton;
    //form elements for number of k-points
    @FXML
    private Label numKLabel;
    @FXML
    private TextField numK;
    @FXML
    private Button numKInc;
    @FXML
    private Button numKDec;
    //empty gridpane for dynamic k-point list
    @FXML
    private GridPane kGridPane;
    //form elements for k-point offsets when the selected k-point option is "automatic"
    @FXML
    private Label kOffLabel;
    @FXML
    private ToggleButton kOff1;
    @FXML
    private ToggleButton kOff2;
    @FXML
    private ToggleButton kOff3;
    @FXML
    private Button kOffButton;
    //size of k point offset toggle buttons
    private static final double KOFF_WIDTH = 60.0;
    //end edit
    
    /*
     * restart
     */
    @FXML
    private Label restartLabel;

    @FXML
    private ToggleButton restartToggle;

    @FXML
    private Button restartButton;

    /*
     * max time
     */
    @FXML
    private Label maxtimeLabel;

    @FXML
    private TextField maxtimeField;

    @FXML
    private Button maxtimeButton;

    @FXML
    private ComboBox<String> maxtimeUnit;

    /*
     * calc force
     */
    @FXML
    private Label forceLabel;

    @FXML
    private ToggleButton forceToggle;

    @FXML
    private Button forceButton;

    /*
     * calc stress
     */
    @FXML
    private Label stressLabel;

    @FXML
    private ToggleButton stressToggle;

    @FXML
    private Button stressButton;

    /*
     * ecutwfc
     */
    @FXML
    private Label ecutwfcLabel;

    @FXML
    private TextField ecutwfcField;

    @FXML
    private Button ecutwfcButton;

    @FXML
    private ComboBox<String> ecutwfcUnit;

    /*
     * ecutrho
     */
    @FXML
    private Label ecutrhoLabel;

    @FXML
    private TextField ecutrhoField;

    @FXML
    private Button ecutrhoButton;

    @FXML
    private ComboBox<String> ecutrhoUnit;

    /*
     * total charge
     */
    @FXML
    private Label totchargeLabel;

    @FXML
    private TextField totchargeField;

    @FXML
    private Button totchargeButton;

    /*
     * symmetry
     */
    @FXML
    private Label symmLabel;

    @FXML
    private ToggleButton symmToggle;

    @FXML
    private Button symmButton;

    /*
     * k-point
     */
    @FXML
    private Label kpointLabel;

    @FXML
    private TextField kpointField1;

    @FXML
    private TextField kpointField2;

    @FXML
    private TextField kpointField3;

    @FXML
    private Button kpointButton;

    /*Edited By Jason D'Netto for disabling occupation and smearing for use 
    cases when they should not be written at all*/
    @FXML
    private Label oToggleLabel;
    @FXML
    private ToggleButton occupToggle;
    @FXML
    private Button oToggleButton;
    
    //end edit
    
    /*
     * occupation
     */
    @FXML
    private Label occupLabel;

    @FXML
    private ComboBox<String> occupCombo;

    @FXML
    private Button occupButton;

    /*
     * smearing
     */
    @FXML
    private Label smearLabel;

    @FXML
    private ComboBox<String> smearCombo;

    @FXML
    private Button smearButton;

    /*
     * gaussian (for smearing)
     */
    @FXML
    private Label gaussLabel;

    @FXML
    private TextField gaussField;

    @FXML
    private Button gaussButton;

    @FXML
    private ComboBox<String> gaussUnit;
    
    /*Edited by Jason D'Netto
    Lists for adding dynaic k-points*/
    private List<TextField[]> kFields;
    private List<Button> invisibuttons;
    private List<Label> invisilabels;
    private List<QEFXKPoints> kPointsList;
    //end edit
    
    public QEFXStandardController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        this.ecutwfcItem = null;
        this.ecutrhoItem = null;
    }

    public void updateEcutStatus() {
        if (this.ecutwfcItem != null) {
            this.ecutwfcItem.pullAllTriggers();
        }

        if (this.ecutrhoItem != null) {
            this.ecutrhoItem.pullAllTriggers();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);

        if (nmlControl != null) {
            this.setupRestartItem(nmlControl);
            this.setupMaxTimeItem(nmlControl);
            this.setupCalcForceItem(nmlControl);
            this.setupCalcStressItem(nmlControl);
        }

        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        if (nmlSystem != null) {
            this.setupEcutwfcItem(nmlSystem);
            this.setupEcutrhoItem(nmlSystem);
            this.setupTotChargeItem(nmlSystem);
            this.setupSymmetryItem(nmlSystem);
            this.setupOccupationItem(nmlSystem);
            this.setupSmearingItem(nmlSystem);
            this.setupGaussianItem(nmlSystem);
            //edited by Jason D'Netto to disable Occupations, smearing and degauss when necessary
            this.setupOccupToggle(nmlSystem);
            //this.disableOccupations(nmlSystem);//default off
            this.enableOccupations(nmlSystem);//default oon
            //end edit
        }

        QECard card = this.input.getCard(QEKPoints.CARD_NAME);

        if (card != null && card instanceof QEKPoints) {
            this.setupKPointItem((QEKPoints) card);
            //edited by Jason D'Netto to add k-offsets and dynamic list of K points
            this.setupKOffsets((QEKPoints)card);
            this.kFields =new ArrayList<TextField[]>();
            this.invisibuttons = new ArrayList<Button>();
            this.invisilabels = new ArrayList<Label>();
            this.kPointsList = new ArrayList<QEFXKPoints>();
            this.setupKOpts((QEKPoints)card);
            this.setupNumK((QEKPoints)card);
            this.setupNumKInc();
            this.setupNumKDec();
            //end edit
        }
    }

    private void setupRestartItem(QENamelist nmlControl) {
        if (this.restartToggle == null) {
            return;
        }

        QEFXToggleString item = new QEFXToggleString(
                nmlControl.getValueBuffer("restart_mode"), this.restartToggle, false, "restart", "from_scratch");

        if (this.restartLabel != null) {
            item.setLabel(this.restartLabel);
        }

        if (this.restartButton != null) {
            item.setDefault("from_scratch", this.restartButton);
        }
    }

    private void setupMaxTimeItem(QENamelist nmlControl) {
        if (this.maxtimeField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlControl.getValueBuffer("max_seconds"), this.maxtimeField);

        if (this.maxtimeLabel != null) {
            item.setLabel(this.maxtimeLabel);
        }

        if (this.maxtimeButton != null) {
            item.setDefault(24.0 * 60.0 * 60.0, this.maxtimeButton); // 1day
        }

        if (this.maxtimeUnit != null) {
            item.setUnit(new QEFXUnit(this.maxtimeUnit, QEFXUnit.UNIT_TYPE_REAL_TIME));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("max_seconds".equalsIgnoreCase(name)) {
                if (value != null && value.getRealValue() < 60.0) { // 1min
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupCalcForceItem(QENamelist nmlControl) {
        if (this.forceToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlControl.getValueBuffer("tprnfor"), this.forceToggle, false);

        if (this.forceLabel != null) {
            item.setLabel(this.forceLabel);
        }

        if (this.forceButton != null) {
            item.setDefault(false, this.forceButton);
        }
    }

    private void setupCalcStressItem(QENamelist nmlControl) {
        if (this.stressToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlControl.getValueBuffer("tstress"), this.stressToggle, false);

        if (this.stressLabel != null) {
            item.setLabel(this.stressLabel);
        }

        if (this.stressButton != null) {
            item.setDefault(false, this.stressButton);
        }
    }

    private void setupEcutwfcItem(QENamelist nmlSystem) {
        if (this.ecutwfcField == null) {
            return;
        }

        CutoffCorrector corrector = new CutoffCorrector(this.input);

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("ecutwfc"), this.ecutwfcField);

        if (this.ecutwfcLabel != null) {
            item.setLabel(this.ecutwfcLabel);
        }

        if (this.ecutwfcButton != null) {
            item.setDefault(() -> {
                double ecut = corrector.isAvailable() ? corrector.getCutoffOfWF() : DEFAULT_ECUTWFC;
                return QEValueBase.getInstance("ecutwfc", ecut);
            }, this.ecutwfcButton);
        }

        if (this.ecutwfcUnit != null) {
            item.setUnit(new QEFXUnit(this.ecutwfcUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("ecutwfc".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }

                double ecut = corrector.isAvailable() ? corrector.getCutoffOfWF() : DEFAULT_ECUTWFC;
                if (value.getRealValue() < (ecut - DELTA_ECUT)) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.ecutwfcItem = item;
    }

    private void setupEcutrhoItem(QENamelist nmlSystem) {
        if (this.ecutrhoField == null) {
            return;
        }

        CutoffCorrector corrector = new CutoffCorrector(this.input);

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("ecutrho"), this.ecutrhoField);

        if (this.ecutrhoLabel != null) {
            item.setLabel(this.ecutrhoLabel);
        }

        if (this.ecutrhoButton != null) {
            item.setDefault(() -> {
                double ecut = corrector.isAvailable() ? corrector.getCutoffOfCharge() : DEFAULT_ECUTWFC;
                return QEValueBase.getInstance("ecutrho", ecut);
            }, this.ecutrhoButton);
        }

        if (this.ecutrhoUnit != null) {
            item.setUnit(new QEFXUnit(this.ecutrhoUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        QEValueBuffer ecutwfcValue = nmlSystem.getValueBuffer("ecutwfc");

        ecutwfcValue.addListener(value -> {
            if ((!ecutwfcValue.hasValue()) || ecutwfcValue.getRealValue() <= 0.0) {
                item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
            } else {
                item.setLowerBound(4.0 * ecutwfcValue.getRealValue(), QEFXTextFieldDouble.BOUND_TYPE_LESS_EQUAL);
            }
        });

        ecutwfcValue.runAllListeners();

        item.addWarningCondition((name, value) -> {
            if ("ecutrho".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }

                double ecut = corrector.isAvailable() ? corrector.getCutoffOfCharge() : DEFAULT_ECUTRHO;
                if (value.getRealValue() < (ecut - DELTA_ECUT)) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.ecutrhoItem = item;
    }

    private void setupTotChargeItem(QENamelist nmlSystem) {
        if (this.totchargeField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("tot_charge"), this.totchargeField);

        if (this.totchargeLabel != null) {
            item.setLabel(this.totchargeLabel);
        }

        if (this.totchargeButton != null) {
            item.setDefault((QEValue) null, this.totchargeButton);
        }

        item.addWarningCondition((name, value) -> {
            if ("tot_charge".equalsIgnoreCase(name)) {
                if (value != null && Math.abs(value.getRealValue()) >= 10.0) {
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupSymmetryItem(QENamelist nmlSystem) {
        if (this.symmToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlSystem.getValueBuffer("nosym"), this.symmToggle, true, true);

        if (this.symmLabel != null) {
            item.setLabel(this.symmLabel);
        }

        if (this.symmButton != null) {
            item.setDefault(false, this.symmButton);
        }
    }

    private void setupOccupationItem(QENamelist nmlSystem) {
        if (this.occupCombo == null) {
            return;
        }

        this.occupCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlSystem.getValueBuffer("occupations"), this.occupCombo);

        if (this.occupLabel != null) {
            item.setLabel(this.occupLabel);
        }

        if (this.occupButton != null) {
            item.setDefault("smearing", this.occupButton);
        }

        item.addItems("smearing", "fixed");

        item.addWarningCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupSmearingItem(QENamelist nmlSystem) {
        if (this.smearCombo == null) {
            return;
        }

        this.smearCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlSystem.getValueBuffer("smearing"), this.smearCombo);

        if (this.smearLabel != null) {
            item.setLabel(this.smearLabel);
        }

        if (this.smearButton != null) {
            item.setDefault("gaussian", this.smearButton);
        }

        item.addItems("gaussian", "methfessel-paxton", "marzari-vanderbilt", "fermi-dirac");

        item.addEnablingTrigger(nmlSystem.getValueBuffer("occupations"));
        item.addEnabledCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value != null && "smearing".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.pullAllTriggers();
    }

    private void setupGaussianItem(QENamelist nmlSystem) {
        if (this.gaussField == null) {
            return;
        }

        QEValueBuffer occupValue = nmlSystem.getValueBuffer("occupations");
        QEValueBuffer gaussValue = nmlSystem.getValueBuffer("degauss");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(gaussValue, this.gaussField);

        if (this.gaussLabel != null) {
            item.setLabel(this.gaussLabel);
        }

        if (this.gaussButton != null) {
            item.setDefault(0.01, this.gaussButton); // 0.01Ry
        }

        if (this.gaussUnit != null) {
            item.setUnit(new QEFXUnit(this.gaussUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_EQUAL);

        item.addEnablingTrigger(occupValue);
        item.addEnabledCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value != null && "smearing".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.addWarningTrigger(occupValue);
        item.addWarningCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name) || "degauss".equalsIgnoreCase(name)) {
                if (!item.isDisable()) {
                    if ((!gaussValue.hasValue()) || gaussValue.getRealValue() == 0.0) {
                        return WarningCondition.WARNING;
                    } else if (gaussValue.getRealValue() > 0.05) { // 0.05Ry
                        return WarningCondition.WARNING;
                    } else {
                        return WarningCondition.OK;
                    }
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }
    
    /*Edited by Jason D'Netto
    occupations need ability to be able to be switched off and not written to file at all
    smearing is for metals
    fixed is for insulators with a gap
    occupations not present in file is the valid option for certain use cases*/
    
    private void enableOccupations(QENamelist nmlSystem){
        if(!(nmlSystem.getValueBuffer("occupations").hasValue())){
            nmlSystem.addProtectedValue("occupations");
            nmlSystem.getValueBuffer("occupations").setValue(this.occupCombo.valueProperty().getValue());
        }
        this.occupCombo.setDisable(false);
        this.occupButton.setDisable(false);
        this.occupLabel.setDisable(false);
        this.occupButton.fire();
        this.enableSmearing(nmlSystem);
    }
    
    private void disableOccupations(QENamelist nmlSystem){
        if(nmlSystem.getValueBuffer("occupations").hasValue()){
            nmlSystem.removeValue("occupations");
        }
        this.occupCombo.setDisable(true);
        this.occupButton.setDisable(true);
        this.occupLabel.setDisable(true);
        this.disableSmearing(nmlSystem);
    }
    
    private void enableSmearing(QENamelist nmlSystem){
        if(nmlSystem.getValueBuffer("occupations").hasValue()&&"smearing".equals(nmlSystem.getValueBuffer("occupations").getCharacterValue())){
            if(!(nmlSystem.getValueBuffer("smearing").hasValue())){
                nmlSystem.addProtectedValue("smearing");
            }
            if(!(nmlSystem.getValueBuffer("degauss").hasValue())){
                nmlSystem.addProtectedValue("degauss");
                /*if you click the reload button on the input file,
                the input corrector will populate smearing and degauss 
                after selecting occupations or the user can actually 
                fill out the form*/
            }
            this.smearCombo.setDisable(false);
            this.smearButton.fire();
            this.gaussField.setDisable(false);
            this.gaussButton.fire();
        }
    }
    
    private void disableSmearing(QENamelist nmlSystem){
        if(nmlSystem.getValueBuffer("smearing").hasValue()){
            nmlSystem.removeValue("smearing");
        }
        this.smearCombo.setDisable(true);
        if(nmlSystem.getValueBuffer("degauss").hasValue()){
            nmlSystem.removeValue("degauss");
        }
        this.gaussField.setDisable(true);
    }
    
    private void setupOccupToggle(QENamelist nmlSystem){
        if(this.occupToggle==null){return;}
        FXToggleBoolean item = new FXToggleBoolean(occupToggle,true);
        if (this.oToggleLabel!=null){item.setLabel(this.oToggleLabel);}
        if (this.oToggleButton!=null){item.setDefault(true,this.oToggleButton);}
        this.occupToggle.setOnAction(event->{
            if(this.occupToggle.isSelected()){this.enableOccupations(nmlSystem);}
            else{this.disableOccupations(nmlSystem);}
        });
    }
    
    //also adding a handle to the automatic kPoints so they can be enabled and disabled
    private QEFXKPoints kPoints;
    /*end edit*/

    private void setupKPointItem(QEKPoints cardKPoints) {
        if (this.kpointField1 == null || this.kpointField2 == null || this.kpointField3 == null) {
            return;
        }

        if (this.kpointLabel == null || this.kpointButton == null) {
            return;
        }

        TextField[] kpointFields = { this.kpointField1, this.kpointField2, this.kpointField3 };

        this.kPoints = new QEFXKPoints(cardKPoints, kpointFields, this.kpointLabel, this.kpointButton);

        this.kPoints.setDefault(event -> {
            cardKPoints.setRecommendedCondition(this.input);
            this.kpointField1.requestFocus();
        });
    }
    
    /*Edited By Jason D'Netto
    adding form functionality for K-Point options, K-Point offsets,
    and list of K Points*/
    private QEFXKOffsets kOff;
    private void setupKOffsets(QEKPoints cardKPoints){
        if(this.kOff1==null||this.kOff2==null||this.kOff3==null){return;}
        if(this.kOffLabel==null){return;}
        ToggleButton[] kOffButtons = {this.kOff1,this.kOff2,this.kOff3};
        this.kOff = new QEFXKOffsets(cardKPoints,kOffButtons,this.kOffLabel,this.kOffButton);
        this.kOff.setDefault(event->{
            this.kOff.setOff();//default turn offsets off
        });
    }
    private void enableKGrid(){
        //switch on form elements
        this.kPoints.setDisable(false);
        this.kOff.setDisable(false);
        //if elements are empty, trigger pressing the default buttons
        if(("".equals(this.kpointField1.getText()))||("".equals(this.kpointField2.getText()))||("".equals(this.kpointField3.getText()))){
            this.kpointButton.fire();
            this.kOff.getButton().fire();
        }
    }
    private void disableKGrid(){
        //switch off form elements
        this.kPoints.setDisable(true);
        this.kOff.setDisable(true);
    }
    private void enabeKList(QEKPoints cardKPoints){
        //switch on gridpane
        this.kGridPane.setDisable(false);
        //switch on numK{,Inc,Dec}
        this.numKLabel.setDisable(false);
        this.numK.setDisable(false);
        this.numKInc.setDisable(false);
        this.numKDec.setDisable(false);
        //set numK to 1 if the text field is empty
        if("".equals(this.numK.getText())){this.numK.setText("1");}
        //row creation method is called on changing the text value
    }
    private void disableKList(){
        this.kGridPane.setDisable(true);
        this.numKLabel.setDisable(true);
        this.numK.setDisable(true);
        this.numKInc.setDisable(true);
        this.numKDec.setDisable(true);
    }
    private void disableKAll(){
        this.disableKGrid();
        this.disableKList();
    }
    private void kOptsHelper(QEKPoints cardKPoints){
        if(("automatic".equals(this.prevKOpts))||"gamma".equals(this.prevKOpts)){
            if("automatic".equals(this.prevKOpts)){
                //disable k-grid and offsets
                this.disableKGrid();
            }
            //enable dynamic list of k-points
            this.enabeKList(cardKPoints);
        }
    }
    private String prevKOpts;
    private void setupKOpts(QEKPoints cardKPoints){
        this.kOpts.setItems(FXCollections.observableArrayList(K_OPTIONS));
        this.prevKOpts = "automatic";//default value
        this.kOpts.setValue(this.prevKOpts);
        this.disableKList();
        //assign value of kOpts combobox to cardKPoints.setOption via event listener
        this.kOpts.setOnAction(event->{
            switch(kOpts.getValue()){
                case "automatic":
                    cardKPoints.setAutomatic();
                    if(!("automatic".equals(this.prevKOpts))){//if switching from another k option
                        if(!("gamma".equals(this.prevKOpts))){//if it was previously a list
                            //disable dynamic list of k-points
                            this.disableKList();
                        }
                        //enable k-grid and k-offset toggles
                        this.enableKGrid();
                    }
                    break;
                case "gamma":
                    cardKPoints.setGamma();
                    this.disableKAll();
                    break;
                case "crystal":
                    cardKPoints.setCrystal();
                    kOptsHelper(cardKPoints);
                    break;
                case "crystal_b":
                    cardKPoints.setCrystalB();
                    kOptsHelper(cardKPoints);
                    break;
                case "crystal_c":
                    cardKPoints.setCrystalC();
                    kOptsHelper(cardKPoints);
                    break;
                case "tpiba":
                    cardKPoints.setTpiba();
                    kOptsHelper(cardKPoints);
                    break;
                case "tpiba_b":
                    cardKPoints.setTpibaB();
                    kOptsHelper(cardKPoints);
                    break;
                case "tpiba_c":
                    cardKPoints.setTpibaC();
                    kOptsHelper(cardKPoints);
                    break;
                default://error
                        break;    
            }
            this.prevKOpts=this.kOpts.getValue();
        });
    }
    
    private void addKPoint(QEKPoints cardKPoints){
        //make four TextFields
        int numKPoints = this.kGridPane.getChildren().size()/4;
        this.kFields.add(new TextField[4]);
        for (int i=0;i<4;i++){
            this.kFields.get(numKPoints)[i] = new TextField();
        }
        //add to GridPane Row
        this.kGridPane.addRow(numKPoints+1,this.kFields.get(numKPoints)[0],this.kFields.get(numKPoints)[1],this.kFields.get(numKPoints)[2],this.kFields.get(numKPoints)[3]);
        //generate invisible button and label for QEFXKPoints constructor
        Button tempButton = new Button();
        this.invisibuttons.add(tempButton);
        Label tempLabel = new Label();
        this.invisilabels.add(tempLabel);
        //create QEFXKPoints, which makes the KPoint and adds it to the card
        QEFXKPoints item = new QEFXKPoints(cardKPoints,this.kFields.get(numKPoints),tempLabel,tempButton);
        this.kPointsList.add(item);
    }
    private void addExistingKPoint(QEKPoints cardKPoints, int index){
        //make four TextFields
        int numKPoints = this.kGridPane.getChildren().size()/4;
        this.kFields.add(new TextField[4]);
        for (int i=0;i<4;i++){
            kFields.get(numKPoints)[i] = new TextField();
        }
        kFields.get(numKPoints)[0].setText(Double.toString(cardKPoints.getKPoint(index).getX()));
        kFields.get(numKPoints)[1].setText(Double.toString(cardKPoints.getKPoint(index).getY()));
        kFields.get(numKPoints)[2].setText(Double.toString(cardKPoints.getKPoint(index).getZ()));
        kFields.get(numKPoints)[3].setText(Double.toString(cardKPoints.getKPoint(index).getWeight()));
        //add to GridPane Row
        this.kGridPane.addRow(numKPoints+1,kFields.get(numKPoints)[0],kFields.get(numKPoints)[1],kFields.get(numKPoints)[2],kFields.get(numKPoints)[3]);
        //generate invisible button and label for QEFXKPoints constructor
        Button tempButton = new Button();
        this.invisibuttons.add(tempButton);
        Label tempLabel = new Label();
        this.invisilabels.add(tempLabel);
        //create QEFXKPoints, which makes the KPoint and adds it to the card
        QEFXKPoints item = new QEFXKPoints(cardKPoints,kFields.get(numKPoints),tempLabel,tempButton,index);
        this.kPointsList.add(item);
    }
    
    private void removeKPoint(QEKPoints cardKPoints){
        int myPointIndex = this.kFields.size()-1;
        try{
            this.kGridPane.getChildren().removeAll(this.kFields.get(myPointIndex)[0],this.kFields.get(myPointIndex)[1],this.kFields.get(myPointIndex)[2],this.kFields.get(myPointIndex)[3]);
        }catch(ArrayIndexOutOfBoundsException e){
            throw new ArrayIndexOutOfBoundsException("number of Children in plot is " + this.kGridPane.getChildrenUnmodifiable().size());
        }
            this.invisibuttons.remove(this.invisibuttons.size()-1);
            this.invisilabels.remove(this.invisilabels.size()-1);
            this.kFields.remove(this.kFields.size()-1);
            this.kPointsList.remove(this.kPointsList.size()-1);
            cardKPoints.removeKPoint(cardKPoints.numKPoints()-1);
    }
    private void fakePressEnter(TextField item){
        KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, true, true, true, true);
        item.fireEvent(ke);
    }
    private void numKIncrement(){
        try{
        this.numK.setText(Integer.toString(Math.max(0,Integer.parseInt(this.numK.getText()))+1));
        }catch(NumberFormatException e){
            this.numK.setText("1");
        }
        this.fakePressEnter(this.numK);
    }
    private void numKDecrement(){
        try{
        this.numK.setText(Integer.toString(Math.max(1,Integer.parseInt(this.numK.getText()))-1));
        }catch(NumberFormatException e){
            this.numK.setText("0");
        }
        this.fakePressEnter(this.numK);
    }
    private void setupNumKInc(){
        this.numKInc.setOnAction(event->{this.numKIncrement();});
    }
    private void setupNumKDec(){
        this.numKDec.setOnAction(event->{this.numKDecrement();});
    }
    private void numKHelper(QEKPoints cardKPoints){
        Integer desiredNumKPoints=null;
        try{desiredNumKPoints = Integer.parseInt(this.numK.getText());}
        catch(NumberFormatException e){desiredNumKPoints=0;}
        if(desiredNumKPoints!=null){
            if(desiredNumKPoints>cardKPoints.numKPoints()){
                while(desiredNumKPoints>cardKPoints.numKPoints()){
                    this.addKPoint(cardKPoints);
                }
            }else{
                if(desiredNumKPoints<cardKPoints.numKPoints()){
                    while(desiredNumKPoints<cardKPoints.numKPoints()){
                        this.removeKPoint(cardKPoints);
                    }
                }
            }
        }
    }
    private void setupNumK(QEKPoints cardKPoints){
        //if there is a preexisting list of k-points, initialise them
        if(cardKPoints.isTpiba()||cardKPoints.isTpibaB()||cardKPoints.isTpibaC()||cardKPoints.isCrystal()||cardKPoints.isCrystalB()||cardKPoints.isCrystalC()){
                this.kOpts.setValue(cardKPoints.getOption());
                this.kOptsHelper(cardKPoints);
                this.prevKOpts=this.kOpts.getValue();
                for(int i=0;i<cardKPoints.numKPoints();i++){
                    this.addExistingKPoint(cardKPoints, i);
                }
                this.numK.setText(Integer.toString(cardKPoints.numKPoints()));
        }
        this.numK.textProperty().addListener((observable, oldValue, newValue)->{
            try{numKHelper(cardKPoints);}
            catch(NullPointerException e){
                //might occurs right after switching from disabled to enabled, ignore
            }
        });
    }
    //end edit
}
