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

import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.com.graphic.ToggleGraphics;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXPh_CalcController extends QEFXInputController {
    //private list of combo box items
    private static final String[] BASIS_VALUES = {"cartesian","modes"};
    private static final String[] ELECTRON_PHONON_WORDS = {"None","Lamda Coefficients","Interpolated"};
    private static final String[] ELECTRON_PHONON_VALUES = {"''","simple","interpolated"};
    //private object handles
    private QEFXTextFieldDouble xPsiItem;
    private QEFXTextFieldDouble NSCFItem;
    private QEFXTextFieldDouble ddkItem;
    private QEFXTextFieldDouble MaxTimeItem;
    private QEFXTextFieldDouble SCTItem;
    private QEFXTextFieldInteger MaxSCFItem;
    private QEFXTextFieldInteger NMixItem;
    private List<QEFXTextFieldDouble> alphaMixItems;
    private List<Label> alphaLabels;
    private List<TextField> alphaFields;
    private List<Button> alphaButtons;
    //private list of FXML form elements
    //recover elements
    @FXML
    private Label recoverLabel;
    @FXML
    private ToggleButton recoverToggle;
    @FXML
    private Button recoverButton;
    //Phonon Dispersion Elements
    @FXML
    private Label PhDispLabel;
    @FXML
    private ToggleButton PhDispToggle;
    @FXML
    private Button PhDispButton;
    //no gamma gamma tricks
    @FXML
    private Label NGGLabel;
    @FXML
    private ToggleButton NGGToggle;
    @FXML
    private Button NGGButton;
    //force diagonalization of dynamical matrix
    @FXML
    private Label ForceDiagLabel;
    @FXML
    private ToggleButton ForceDiagToggle;
    @FXML
    private Button ForceDiagButton;
    //dynamic polarizabilities
    @FXML
    private Label DynPolLabel;
    @FXML
    private ToggleButton DynPolToggle;
    @FXML
    private Button DynPolButton;
    //Single Q Vector Phonons
    @FXML
    private Label SingleQLabel;
    @FXML
    private ToggleButton SingleQToggle;
    @FXML
    private Button SingleQButton;
    //macroscopic dielectric constant for q=0
    @FXML
    private Label q0macroLabel;
    @FXML
    private ToggleButton q0macroToggle;
    @FXML
    private Button q0macroButton;
    //electro-optic coefficients
    @FXML
    private Label eocoeffLabel;
    @FXML
    private ToggleButton eocoeffToggle;
    @FXML
    private Button eocoeffButton;
    //mode symmetry analysis
    @FXML
    private Label modeSymmLabel;
    @FXML
    private ToggleButton modeSymmToggle;
    @FXML
    private Button modeSymmButton;
    //dV_xc dielectric constants - dV_H=0
    @FXML
    private Label dVHLabel;
    @FXML
    private ToggleButton dVHToggle;
    @FXML
    private Button dVHButton;
    //dV_xc dielectric constants - RPA level
    @FXML
    private Label RPALabel;
    @FXML
    private ToggleButton RPAToggle;
    @FXML
    private Button RPAButton;
    //effective charges from dielectric responses
    @FXML
    private Label dielecRespLabel;
    @FXML
    private ToggleButton dielecRespToggle;
    @FXML
    private Button dielecRespButton;
    //effective charges from phonon density responses
    @FXML
    private Label PhDensRespLabel;
    @FXML
    private ToggleButton PhDensRespToggle;
    @FXML
    private Button PhDensRespButton;
    //Raman coefficients - enable
    @FXML
    private Label RamanCoeffLabel;
    @FXML
    private ToggleButton RamanCoeffToggle;
    @FXML
    private Button RamanCoeffButton;
    //Raman coefficients x|Psi>
    @FXML
    private Label xPsiLabel;
    @FXML
    private TextField xPsiText;
    @FXML
    private Button xPsiButton;
    //Raman coefficients - non scf calculation
    @FXML
    private Label NSCFLabel;
    @FXML
    private TextField NSCFText;
    @FXML
    private Button NSCFButton;
    //Raman coeficients - wavefunction derivation delta k
    @FXML
    private Label ddkLabel;
    @FXML
    private TextField ddkText;
    @FXML
    private Button ddkButton;
    //potential variations, compute all q points in star
    @FXML
    private Label allQLabel;
    @FXML
    private ToggleButton allQToggle;
    @FXML
    private Button allQButton;
    //potential variations, save all q
    @FXML
    private Label saveQLabel;
    @FXML
    private ToggleButton saveQToggle;
    @FXML
    private Button saveQButton;
    //potential variations - basis
    @FXML
    private Label potVarBasisLabel;
    @FXML
    private ComboBox<String> potVarBasisBox;
    @FXML
    private Button potVarBasisButton;
    //pertubation of charge density - compute all
    @FXML
    private Label allQdensLabel;
    @FXML
    private ToggleButton allQdensToggle;
    @FXML
    private Button allQdensButton;
    //pertubation of charge density - save all q
    @FXML
    private Label saveQdensLabel;
    @FXML
    private ToggleButton saveQdensToggle;
    @FXML
    private Button saveQdensButton;
    //pertubation of charge density - basis
    @FXML
    private Label charDensPertLabel;
    @FXML
    private ComboBox<String> charDensPertBox;
    @FXML
    private Button charDensPertButton;
    //electron phonon method
    @FXML
    private Label elecPhonLabel;
    @FXML
    private ComboBox<String> elecPhonBox;
    @FXML
    private Button elecPhonButton;
    //maximum time
    @FXML
    private Label MaxTimeLabel;
    @FXML
    private TextField MaxTime;
    @FXML
    private Button MaxTimeButton;
    //self consistancy threshold
    @FXML
    private Label SCTLabel;
    @FXML
    private TextField SCT;
    @FXML
    private Button SCTButton;
    //max SCF steps
    @FXML
    private Label MaxSCFLabel;
    @FXML
    private TextField MaxSCF;
    @FXML
    private Button MaxSCFButton;
    //number of mixing iterations
    @FXML
    private Label NMixLabel;
    @FXML
    private TextField NMix;
    @FXML
    private Button NMixButton;
    //grid for dynamic mixing factors
    @FXML
    private GridPane alphaGrid;
    
    //List so that the QEFXToggleBooleans are not destroyed by garbage collector
    private List<QEFXToggleBoolean> ToggleHandles;
    
    //values for redrawing smaller togglebuttons
    private static final double TOGGLE_WIDTH = 85.0;
    
    private int previousNumAlphas;
    
     public QEFXPh_CalcController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        //this.elementBinder = null;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //namelists
        QENamelist nmlPh = this.input.getNamelist(QEInput.NAMELIST_INPUTPH);
        
        if(nmlPh!=null){
            this.ToggleHandles = new ArrayList<QEFXToggleBoolean>();
            QEFXToggleBoolean tempHandle = null;
            tempHandle = this.setupToggleButton(nmlPh,recoverToggle,"recover",recoverLabel,recoverButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,PhDispToggle,"ldisp",PhDispLabel,PhDispButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,NGGToggle,"nogg",NGGLabel,NGGButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,ForceDiagToggle,"ldiag",ForceDiagLabel,ForceDiagButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,DynPolToggle,"fpol",DynPolLabel,DynPolButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,SingleQToggle,"trans",SingleQLabel,SingleQButton,true);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,q0macroToggle,"epsil",q0macroLabel,q0macroButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,eocoeffToggle,"elop",eocoeffLabel,eocoeffButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,modeSymmToggle,"search_sym",modeSymmLabel,modeSymmButton,true);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,dVHToggle,"lnoloc",dVHLabel,dVHButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,RPAToggle,"lrpa",RPALabel,RPAButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,dielecRespToggle,"zeu",dielecRespLabel,dielecRespButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,PhDensRespToggle,"zue",PhDensRespLabel,PhDensRespButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,RamanCoeffToggle,"lraman",RamanCoeffLabel,RamanCoeffButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,allQToggle,"dvscf_star%open",allQLabel,allQButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,saveQToggle,"dvscf_star%pat",saveQLabel,saveQButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,allQdensToggle,"drho_star%open",allQdensLabel,allQdensButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            tempHandle = this.setupToggleButton(nmlPh,saveQdensToggle,"drho_star%pat",saveQdensLabel,saveQdensButton,false);
            if(tempHandle==null){System.out.println("ToggleBoolean did not create");}
            else{this.ToggleHandles.add(tempHandle);}
            this.setupPotVarBasis(nmlPh);
            this.setupcharDensPert(nmlPh);
            this.setupElecPhon(nmlPh);
            this.setupXPsi(nmlPh);
            this.setupNSCF(nmlPh);
            this.setupddk(nmlPh);
            this.setupMaxTime(nmlPh);
            this.setupSCT(nmlPh);
            this.setupMaxSteps(nmlPh);
            this.alphaButtons = new ArrayList<Button>();
            this.alphaFields = new ArrayList<TextField>();
            this.alphaLabels = new ArrayList<Label>();
            this.alphaMixItems = new ArrayList<QEFXTextFieldDouble>();
            this.previousNumAlphas = 0;
            this.setupIterations(nmlPh);
            //set default options
            this.SCTButton.fire();
            this.MaxSCFButton.fire();
            this.PhDispButton.fire();
            this.MaxTimeButton.fire();
            if(nmlPh.getValueBuffer("nmix_ph").hasValue()){
            this.numAlphaHelper(nmlPh);//populate alpha_mix table if there is a value present
            }
        }else{System.out.println("no namelist nmlPh error");}   
    }
    
    //functions for form functionality
    private QEFXToggleBoolean setupToggleButton(QENamelist nmlPh, ToggleButton controlItem, String QEVB, Label formLabel, Button defaultButton, Boolean defaultVaule){
        if (controlItem == null) {return null;}
        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlPh.getValueBuffer(QEVB),controlItem,false,TOGGLE_WIDTH);
        if (formLabel != null) {item.setLabel(formLabel);}
        if (defaultButton != null) {item.setDefault(defaultVaule, defaultButton);}
        return item;
    }
    private void setupPotVarBasis(QENamelist nmlPh){
        if (this.potVarBasisBox == null) {return;}
        QEFXComboString item = new QEFXComboString(
                nmlPh.getValueBuffer("dvscf_star%basis"),this.potVarBasisBox);
        item.addItems(BASIS_VALUES);
        if (this.potVarBasisLabel != null) {item.setLabel(this.potVarBasisLabel);}
        if (this.potVarBasisButton != null){item.setDefault("cartesian", this.potVarBasisButton);}
    }
    private void setupcharDensPert(QENamelist nmlPh){
        if (this.charDensPertBox == null) {return;}
        QEFXComboString item = new QEFXComboString(
                nmlPh.getValueBuffer("drho_star%basis"),this.charDensPertBox);
        item.addItems(BASIS_VALUES);
        if (this.charDensPertLabel != null) {item.setLabel(this.charDensPertLabel);}
        if (this.charDensPertButton != null){item.setDefault("cartesian", this.charDensPertButton);}
    }
    private void setupElecPhon(QENamelist nmlPh){
        if (this.elecPhonBox == null) {return;}
        QEFXComboString item = new QEFXComboString(
                nmlPh.getValueBuffer("electron_phonon"),this.elecPhonBox);
        item.addItems(ELECTRON_PHONON_WORDS);
        if (this.elecPhonLabel != null) {item.setLabel(this.elecPhonLabel);}
        if (this.elecPhonButton != null){item.setDefault("''", this.elecPhonButton);}
        if (ELECTRON_PHONON_WORDS.length != ELECTRON_PHONON_VALUES.length) {
            throw new RuntimeException("ELECTRON_PHONON_WORDS.length != ELECTRON_PHONON_VALUES.length");
        }
        Map<String, String> elecPhonMap = new HashMap<String, String>();
        for (int i = 0; i < ELECTRON_PHONON_WORDS.length; i++) {
            elecPhonMap.put(ELECTRON_PHONON_WORDS[i], ELECTRON_PHONON_VALUES[i]);
        }

        item.setValueFactory(text -> {
            return elecPhonMap.get(text);
        });
        item.pullAllTriggers();
    }
    
    private void setupXPsi(QENamelist nmlPh) {
        if (this.xPsiText == null) {return;}
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer("eth_rps"), this.xPsiText);
        if (this.xPsiLabel != null) {item.setLabel(this.xPsiLabel);}
        if (this.xPsiButton != null) {item.setDefault(1e-9, this.xPsiButton);}
        //dont know what the value ranges are so cant set lowerbound incase negative is allowed, or corrector
        //QEPhononInputCorrector corrector = new QEPhononInputCorrector(this.input);
        //item.setLowerBound(0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
        /*
        item.addWarningCondition((name, value) -> {
            if ("nbnd".equalsIgnoreCase(name)) {
                if (value == null) {return WarningCondition.WARNING;}
                double xPsi = corrector.isAvailable() ? corrector.getXPsi() : 0;
                if (xPsi > 0) {
                    if (xPsi != value.getDoubleValue()) {
                        return WarningCondition.WARNING;
                    } else {
                        return WarningCondition.OK;
                    }
                }
            }
            return WarningCondition.OK;
        });
        */
        item.pullAllTriggers();
        this.xPsiItem = item;
    }
    
    private void setupNSCF(QENamelist nmlPh) {
        if (this.NSCFText == null) {return;}
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer("eth_ns"), this.NSCFText);
        if (this.NSCFLabel != null) {item.setLabel(this.NSCFLabel);}
        if (this.NSCFButton != null) {item.setDefault(1e-9, this.NSCFButton);}
        //dont know what the value ranges are so cant set lowerbound incase negative is allowed, or corrector
        item.pullAllTriggers();
        this.NSCFItem = item;
    }
    
    private void setupddk(QENamelist nmlPh){
        if (this.ddkText == null) {return;}
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer("dek"), this.ddkText);
        if (this.ddkLabel != null) {item.setLabel(this.ddkLabel);}
        if (this.ddkButton != null) {item.setDefault(1e-9, this.ddkButton);}
        //dont know what the value ranges are so cant set lowerbound incase negative is allowed, or corrector
        item.pullAllTriggers();
        this.ddkItem = item;
    }
    
    private void setupMaxTime(QENamelist nmlPh){
        if (this.MaxTime == null) {return;}
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer("max_seconds"), this.MaxTime);
        if (this.MaxTimeLabel != null) {item.setLabel(this.MaxTimeLabel);}
        if (this.MaxTimeButton != null) {item.setDefault(1e7, this.MaxTimeButton);}
        //dont know what the value ranges are so cant set lowerbound incase negative is allowed, or corrector
        item.pullAllTriggers();
        this.MaxTimeItem = item;
    }
    
    private void setupSCT(QENamelist nmlPh){
        if (this.SCT == null) {return;}
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer("tr2_ph"), this.SCT);
        if (this.SCTLabel != null) {item.setLabel(this.SCTLabel);}
        if (this.SCTButton != null) {item.setDefault(1e12, this.SCTButton);}
        //dont know what the value ranges are so cant set lowerbound incase negative is allowed, or corrector
        item.pullAllTriggers();
        this.SCTItem = item;
    }
    
    private void setupMaxSteps(QENamelist nmlPh){
        if (this.MaxSCF == null) {return;}
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer("niter_ph"), this.MaxSCF);
        if (this.MaxSCFLabel != null) {item.setLabel(this.MaxSCFLabel);}
        if (this.MaxSCFButton != null) {item.setDefault(100, this.MaxSCFButton);}
        item.pullAllTriggers();
        this.MaxSCFItem = item;
    }
    
    private void setupIterations(QENamelist nmlPh){
        if (this.NMix == null) {return;}
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer("nmix_ph"), this.NMix);
        if (this.NMixLabel != null) {item.setLabel(this.NMixLabel);}
        if (this.NMixButton != null) {item.setDefault(4, this.NMixButton);}
        item.setLowerBound(0,2);
        this.NMix.setOnAction(event->numAlphaHelper(nmlPh));
        this.NMix.textProperty().addListener((observable, oldValue, newValue)->{
            try{numAlphaHelper(nmlPh);}
            catch(NullPointerException e){
                //occurs when just switched value of qplot and nqs no longer exists
            }
        });
        this.NMixItem = item;
    }
    
    private void addAlphaField(QENamelist nmlPh){
        //get index
        int numAlphaFields = this.alphaGrid.getChildren().size()/3;
        //create label, textfield, button
        this.alphaLabels.add(new Label());
        this.alphaLabels.get(numAlphaFields).setText("Mixing Factor "+(numAlphaFields+1));
        this.alphaFields.add(new TextField());
        this.alphaButtons.add(new Button());
        //add to gridpane row
        this.alphaGrid.addRow(numAlphaFields+1, this.alphaLabels.get(numAlphaFields),this.alphaFields.get(numAlphaFields), this.alphaButtons.get(numAlphaFields));
        //create namelist value alpha_mix(index)
        String str = "alpha_mix("+Integer.toString(numAlphaFields+1)+")";
        if(!(nmlPh.getValueBuffer(str).hasValue())){
            nmlPh.addProtectedValue(str);
        }
        //create QEXFTextFieldInteger with value alpha_mix(index)
        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlPh.getValueBuffer(str),this.alphaFields.get(numAlphaFields));
        item.setDefault(0.7,this.alphaButtons.get(numAlphaFields));
        item.setLabel(this.alphaLabels.get(numAlphaFields));
        //put QEFXTextFieldInteger in List so it is not destroyed by garbage collector
        this.alphaMixItems.add(item);
    }
    
    private void remAlphaField(QENamelist nmlPh){
        int numAlphaFields = this.alphaFields.size();
        for(int i=0;i<3;i++){
            this.alphaGrid.getChildren().removeAll(this.alphaGrid.getChildrenUnmodifiable().get(this.alphaGrid.getChildrenUnmodifiable().size()-1));
        }
        String str = "alpha_mix("+Integer.toString(numAlphaFields)+")";
        nmlPh.removeValue(str);
        this.alphaMixItems.remove(this.alphaMixItems.size()-1);
        this.alphaLabels.remove(this.alphaLabels.size()-1);
        this.alphaFields.remove(this.alphaFields.size()-1);
        this.alphaButtons.remove(this.alphaButtons.size()-1);
    }
    
    private void numQInc(QENamelist nmlPh){
        if(nmlPh.getValueBuffer("nmix_ph").hasValue()){
            nmlPh.getValueBuffer("nmix_ph").setValue(nmlPh.getValue("nmix_ph").getIntegerValue()+1);
        }
        //simulate pressing enter on the text field
        KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, true, true, true, true);
        this.NMix.fireEvent(ke);
    }
    private void numQDec(QENamelist nmlPh){
        if(nmlPh.getValueBuffer("nmix_ph").hasValue()){
            nmlPh.getValueBuffer("nmix_ph").setValue(nmlPh.getValue("nmix_ph").getIntegerValue()-1);
        }
        //simulate pressing enter on the text field
        KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, true, true, true, true);
        this.NMix.fireEvent(ke);
    }
    private void numAlphaHelper(QENamelist nmlPh){
        if(nmlPh.getValue("nmix_ph").getIntegerValue()>this.previousNumAlphas){
            while(nmlPh.getValue("nmix_ph").getIntegerValue()>this.previousNumAlphas){
                addAlphaField(nmlPh);
                this.previousNumAlphas++;
            }
        }else{
            if(nmlPh.getValue("nmix_ph").getIntegerValue()<this.previousNumAlphas){
                while(nmlPh.getValue("nmix_ph").getIntegerValue()<this.previousNumAlphas){
                    remAlphaField(nmlPh);
                    this.previousNumAlphas--;
                }
            }
        }
    }
    
}
