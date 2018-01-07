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
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.input.QEInput;
import burai.input.card.QEQPoints;
import burai.input.namelist.QENamelist;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
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
public class QEFXPh_qController extends QEFXInputController {  
    //private list of FXML form elements
    //toggle for saving each q point seperately
    @FXML
    private Label x1Label;
    @FXML
    private ToggleButton x1;
    @FXML
    private Button x1Default;
    //if qplot is false, set numQPoints to 1 and disable, remove weights column from q_table
    //if qplot is true, enable numQPoints, add weights column to q_table
    @FXML
    private Label qIsLabel;
    @FXML
    private ToggleButton qIsVector;
    @FXML
    private Button qIsDefault;
    //q_in_band_form
    @FXML
    private Label qBandLabel;
    @FXML
    private ToggleButton qBand;
    @FXML
    private Button qBandDefault;
    //q points
    @FXML
    private Label xq1Label;
    @FXML
    private TextField xq1;
    @FXML
    private Label xq2Label;
    @FXML
    private TextField xq2;
    @FXML
    private Label xq3Label;
    @FXML
    private TextField xq3;
    @FXML
    private Button xqButton;
    @FXML
    private Label xqLabel;
    @FXML
    private Group ldispQplotFalseGroup;
    //number of q points selector
    @FXML
    private TextField numQPoints;
    @FXML
    private Button inc_number_button;
    @FXML
    private Button dec_number_button;
    //toggle for rectangular weighted system, QE namelist boolean variable q2d
    //if true, set numQPoints to 3 and disble
    @FXML
    private Label RWSLabel;
    @FXML
    private ToggleButton RWSToggle;
    @FXML
    private Button RWSDefault;
    //dynamic q point grid table
    @FXML
    private GridPane qplot;
    @FXML
    private Group qplotTrueGroup;
    //q points that might be the same as the k points from the scf calculation, have "same meaning"
    @FXML
    private Label nq1Label;
    @FXML
    private TextField nq1;
    @FXML
    private Label nq2Label;
    @FXML
    private TextField nq2;
    @FXML
    private Label nq3Label;
    @FXML
    private TextField nq3;
    @FXML
    private Button nqButton;
    @FXML
    private Group ldispTrueGroup;
    @FXML
    private Label numQLabel;
    
    private QEFXToggleBoolean X1;
    private QEFXToggleBoolean RWS;
    private QEFXToggleBoolean qBandToggle;
    private QEFXToggleBoolean qIS;
    private QEFXTextFieldInteger q1Mesh;
    private QEFXTextFieldInteger q2Mesh;
    private QEFXTextFieldInteger q3Mesh;
    private TextField[] qWVfields;
    private QEFXQPoints qWV;
    private List<TextField[]> qFields;
    private List<QEFXQPoints> qList;
    private QEFXTextFieldInteger numQ;
    private int previousNumQs;
    private List<Button> invisibuttons;
    
    private QEQPoints qCard;
    private QENamelist nmlPh;
    
     public QEFXPh_qController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.nmlPh = this.input.getNamelist(QEInput.NAMELIST_INPUTPH);
        //something to setup the lines, type of card?
        this.qCard = (QEQPoints)this.input.getCard(QEQPoints.CARD_NAME);
        //setup form function list
        if(this.nmlPh!=null){
            LDispCheck(this.nmlPh);
            this.X1 = this.setupToggleButton(this.nmlPh, x1, "lqdir", x1Label, x1Default);
            this.RWS = this.setupToggleButton(this.nmlPh, RWSToggle, "q2d", RWSLabel, RWSDefault);
            this.qBandToggle = this.setupToggleButton(nmlPh, qBand, "q_in_band_form", qBandLabel, qBandDefault);
            setupQIs(this.nmlPh);//qplot combobox
            this.q1Mesh = this.setupTextInt(this.nmlPh, nq1, "nq1", nq1Label, nqButton, 0);
            this.q2Mesh = this.setupTextInt(this.nmlPh, nq2, "nq2", nq2Label, nqButton, 0);
            this.q3Mesh = this.setupTextInt(this.nmlPh, nq3, "nq3", nq3Label, nqButton, 0);
            //phonon wave vector q point (xq) values are a line
            //if qplot is true, set the card option
            if(this.qCard==null){
                throw new IllegalArgumentException("null qCard");
            }
            if(this.nmlPh.getValueBuffer("qplot")!=null&&this.nmlPh.getValueBuffer("qplot").hasValue()){
                this.qCard.setOption(this.nmlPh.getValueBuffer("qplot").getLogicalValue());
            }
            qWVfields = new TextField[3];
            qWVfields[0] = this.xq1;
            qWVfields[1] = this.xq2;
            qWVfields[2] = this.xq3;
            //list of q points are a line
            //QEFXQPoints setupQPoints(QEQPoints qcard, TextField[] controlItems, Label formLabel, Button defaultButton)
            this.qWV = this.setupQPoints(qCard, qWVfields, xqLabel, xqButton);
            this.qFields = new ArrayList<TextField[]>();
            this.invisibuttons = new ArrayList<Button>();
            this.qList = new ArrayList<QEFXQPoints>();
            this.setupNumQInc();
            this.setupNumQDec();
            this.previousNumQs = 0;
            this.setupNumQ();
        }
    }
    
    //functions for form functionality
    private void enableLdispGroup(){
        this.ldispTrueGroup.disableProperty().setValue(Boolean.FALSE);
        if(this.nmlPh.getValueBuffer("nq1") == null){
            this.nmlPh.addProtectedValue("nq1");
        }
        if(this.nmlPh.getValueBuffer("nq2") == null){
            this.nmlPh.addProtectedValue("nq2");
        }
        if(this.nmlPh.getValueBuffer("nq3") == null){
            this.nmlPh.addProtectedValue("nq3");
        }
    }
    private void disableLdispGroup(){
        this.ldispTrueGroup.disableProperty().setValue(Boolean.TRUE);
        if(this.nmlPh.getValueBuffer("nq1") != null){
            this.nmlPh.removeValue("nq1");
        }
        if(this.nmlPh.getValueBuffer("nq2") != null){
            this.nmlPh.removeValue("nq2");
        }
        if(this.nmlPh.getValueBuffer("nq3") != null){
            this.nmlPh.removeValue("nq3");
        }
    }
    private void enableqplotTrueGroup(){
        this.qplotTrueGroup.disableProperty().setValue(Boolean.FALSE);
        if (this.nmlPh.getValueBuffer("nqs") == null) {
            this.nmlPh.addProtectedValue("nqs");
            this.nmlPh.getValueBuffer("nqs").setValue(0);
        }
        this.qCard.setOption(true);
    }
    private void disableqplotTrueGroup(){
        this.qplotTrueGroup.disableProperty().setValue(Boolean.TRUE);
        if (this.nmlPh.getValueBuffer("nqs") != null) {
            this.nmlPh.removeValue("nqs");
        }
        if(this.qCard.numQPoints()>0){
            for(int i=0;i<this.qCard.numQPoints();i++){
                this.qCard.removeQPoint(i);
            }
        }
        this.qCard.clear();
        this.qCard.setOption(false);
        //clear interface to prevent issues
        this.wipeQGrid();
        this.previousNumQs=0;
    }
    private void enableLdispQplotFalseGroup(){
        this.ldispQplotFalseGroup.disableProperty().setValue(Boolean.FALSE);
    }
    private void disableLdispQplotFalseGroup(){
        this.ldispQplotFalseGroup.disableProperty().setValue(Boolean.TRUE);
        for(int i=0;i<3;i++){
            this.qWVfields[i].textProperty().set("");
        }
        if(this.qCard.numQPoints()>0){
            for(int i=0;i<this.qCard.numQPoints();i++){
                this.qCard.removeQPoint(i);
            }
        }
        this.qCard.clear();
    }
    private QEFXToggleBoolean setupToggleButton(QENamelist nmlPh, ToggleButton controlItem, String QEVB, Label formLabel, Button defaultButton){
        if (controlItem == null) {return null;}
        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlPh.getValueBuffer(QEVB),controlItem,false);
        if (formLabel != null) {item.setLabel(formLabel);}
        if (defaultButton != null) {item.setDefault(false, defaultButton);}
        return item;
    }
    private void LDispCheck(QENamelist nmlPh){
        if(nmlPh.getValueBuffer("ldisp").hasValue()&&"ldisp = .TRUE.".equals(nmlPh.getValue("ldisp").toString())){
            enableLdispGroup();
        }else{
            disableLdispGroup();
        }
    }
    
    protected void QIsChanged(QENamelist nmlPh){
        if(!this.qIsVector.isSelected()){//qplot false
                disableqplotTrueGroup();
                if(!(nmlPh.getValueBuffer("ldisp").hasValue())||nmlPh.getValueBuffer("ldisp").getLogicalValue()==Boolean.FALSE){
                    enableLdispQplotFalseGroup();
                }
                else{disableLdispQplotFalseGroup();}
                nmlPh.getValueBuffer("qplot").setValue(Boolean.FALSE);
            }
            else{//qplot true
                disableLdispQplotFalseGroup();
                enableqplotTrueGroup();
                nmlPh.getValueBuffer("qplot").setValue(Boolean.TRUE);
            }
            LDispCheck(nmlPh);
    }
    
    protected QENamelist getNmlPh(){
        return this.nmlPh;
    }
    
    private void setupQIs(QENamelist nmlPh){
        this.qIS = setupToggleButton(nmlPh,this.qIsVector,"qplot",this.qIsLabel,this.qIsDefault);
        this.qIsVector.setOnAction(event->{
            QIsChanged(nmlPh);
        });
        //item.pullAllTriggers();
    }
    
    private QEFXTextFieldInteger setupTextInt(QENamelist nmlPh, TextField controlItem, String QEVB, Label formLabel, Button defaultButton, int defaultValue){
        if (controlItem == null) {return null;}
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer(QEVB),controlItem);
        if (formLabel != null) {item.setLabel(formLabel);}
        if (defaultButton != null) {item.setDefault(defaultValue, defaultButton);}
        return item;
    }
    private QEFXQPoints setupQPoints(QEQPoints qcard, TextField[] controlItems, Label formLabel, Button defaultButton){
        if (controlItems == null) {return null;}
        QEFXQPoints item = new QEFXQPoints(qcard,controlItems,formLabel,defaultButton);
        return item;
    }
    private void addQField(){
        //make four TextFields
        int numQFields = this.qplot.getChildren().size()/4;
        qFields.add(new TextField[4]);
        for (int i=0;i<4;i++){
            qFields.get(numQFields)[i] = new TextField();
        }
        //add to GridPane Row
        this.qplot.addRow(numQFields+1,qFields.get(numQFields)[0],qFields.get(numQFields)[1],qFields.get(numQFields)[2],qFields.get(numQFields)[3]);
        //generate invisible button for QEFXQPoints constructor
        Button tempButton = new Button();
        this.invisibuttons.add(tempButton);
        //create QEXFQPoint, which makes the QPoint and adds it to the card
        QEFXQPoints item = new QEFXQPoints(this.qCard,qFields.get(numQFields),this.numQLabel,tempButton);
        this.qList.add(item);
    }
    private void remQField(){
        //in java the garbage collector cleans up after garbage programmers, not designed to free memory yourself
        //int deleteIndex = this.qList.size()-1;
        try{
        this.qplot.getChildren().removeAll(this.qFields.get(this.qFields.size()-1)[0],this.qFields.get(this.qFields.size()-1)[1],this.qFields.get(this.qFields.size()-1)[2],this.qFields.get(this.qFields.size()-1)[3]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new ArrayIndexOutOfBoundsException("number of Children in plot is " + this.qplot.getChildrenUnmodifiable().size());
        }
        try{
        this.invisibuttons.remove(this.invisibuttons.size()-1);
        this.qFields.remove(this.qFields.size()-1);
        this.qList.remove(this.qList.size()-1);
        this.qCard.removeQPoint(this.qCard.numQPoints()-1);
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new ArrayIndexOutOfBoundsException("deleting invisibuttons["+(this.invisibuttons.size()-1)+"], qFields["+(this.qFields.size()-1)+"],qList["+(this.qList.size()-1)+"] and qCard.removePoint("+(this.qCard.numQPoints()-1)+")");
        }
    }
    
    private void wipeQGrid(){
        int numQPointsInGrid = this.qFields.size();
        for(int i=0;i<numQPointsInGrid;i++){
            remQField();
        }
    }
    
    private void numQInc(){
        if(this.nmlPh.getValueBuffer("nqs").hasValue()){
            this.nmlPh.getValueBuffer("nqs").setValue(this.nmlPh.getValue("nqs").getIntegerValue()+1);
        }
        //simulate pressing enter on the text field
        KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, true, true, true, true);
        this.numQPoints.fireEvent(ke);
    }
    private void numQDec(){
        if(this.nmlPh.getValueBuffer("nqs").hasValue()){
            this.nmlPh.getValueBuffer("nqs").setValue(this.nmlPh.getValue("nqs").getIntegerValue()-1);
        }
        //simulate pressing enter on the text field
        KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, true, true, true, true);
        this.numQPoints.fireEvent(ke);
    }
    private void setupNumQInc(){
        this.inc_number_button.setOnAction(event->{
            this.numQInc();
        });
    }
    private void setupNumQDec(){
        if(this.dec_number_button==null){
            throw new IllegalArgumentException("no dec_number_button");
        }
        this.dec_number_button.setOnAction(event->{
            this.numQDec();
        });
    }
    private void setupNumQ(){
        this.numQ = this.setupTextInt(nmlPh, numQPoints, "nqs", null, null, 0);
        this.numQ.setLowerBound(0,2);//set lower boundary to greater than or equal to 0
        this.numQPoints.setOnAction(event->numQHelper());
        this.numQPoints.textProperty().addListener((observable, oldValue, newValue)->{
            try{numQHelper();}
            catch(NullPointerException e){
                //occurs when just switched value of qplot and nqs no longer exists
            }
        });
    }
    
    private void numQHelper(){
        if(this.nmlPh.getValue("nqs").getIntegerValue()>this.previousNumQs){
            while(this.nmlPh.getValue("nqs").getIntegerValue()>this.previousNumQs){
                addQField();
                this.previousNumQs++;
            }
        }else{
            if(this.nmlPh.getValue("nqs").getIntegerValue()<this.previousNumQs){
                while(this.nmlPh.getValue("nqs").getIntegerValue()<this.previousNumQs){
                    remQField();
                    this.previousNumQs--;
                }
            }
        }
    }
}
