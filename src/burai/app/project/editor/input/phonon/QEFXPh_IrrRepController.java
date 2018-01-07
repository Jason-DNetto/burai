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
import burai.app.project.editor.input.geom.AtomAnsatz;
import burai.app.project.editor.input.geom.AtomAnsatzBinder;
import burai.app.project.editor.input.items.FXToggleBoolean;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXToggleInteger;
import burai.atoms.element.ElementUtil;
import burai.com.graphic.ToggleGraphics;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QENatTodo;
import burai.input.namelist.QENamelist;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEFXPh_IrrRepController extends QEFXInputController {
    //list of FXML form elements
    @FXML
    private Label rangeLabel;
    @FXML
    private ToggleButton sel_index_by_range;
    @FXML
    private Button rangeButton;
    @FXML
    private Label selectLabel;
    @FXML
    private ToggleButton sel_index_by_sel;
    @FXML
    private Button selectButton;
    @FXML
    private Label FromLabel;
    @FXML
    private TextField index_from;
    @FXML
    private Button FromButton;
    @FXML
    private Label ToLabel;
    @FXML
    private TextField index_to;
    @FXML
    private Button ToButton;
    @FXML
    private TableView irr_rep_table;
    @FXML
    private TableColumn<AtomAnsatz, Integer> indexColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> elementColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> xColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> yColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> zColumn;
    @FXML 
    private GridPane rangeGrid;
    @FXML 
    private GridPane selectGrid;
    @FXML
    private GridPane selectionColumn;
    
    @FXML
    private TableView nat_todo_table;
    @FXML
    private TableColumn<AtomAnsatz, Integer> NTDindexColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> NTDelementColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> NTDxColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> NTDyColumn;
    @FXML
    private TableColumn<AtomAnsatz, String> NTDzColumn;
    @FXML 
    private GridPane NTDselectGrid;
    @FXML
    private GridPane NTDselectionColumn;
    
    //private variables
    private static final String CELL_STYLE_MOBILE = "-fx-text-fill: -fx-text-background-color";
    private static final String CELL_STYLE_FIXED = "-fx-text-fill: blue";
    
    //private form elements that dont have a namelist value
    private FXToggleBoolean rangeSelector;
    private FXToggleBoolean selectSelector;
    
    //private list of text fields
    private QEFXTextFieldInteger rangeFrom;
    private QEFXTextFieldInteger rangeTo;
    
    //private atomBinder
    private AtomAnsatzBinder atomBinder;
    private AtomAnsatzBinder atomBinder2;
    private final QEAtomicPositions atomicPositions;
    
    private final QEAtomicSpecies atomicSpecies;
    
    private ToggleGroup selectionMutex = new ToggleGroup();
    //private ToggleGroup selectionMutex2 = new ToggleGroup();
    
    private List<QEFXToggleInteger> ToggleList = new ArrayList<QEFXToggleInteger>();
    private List<FXToggleBoolean> ToggleList2 = new ArrayList<FXToggleBoolean>();
    
    private static final String TOGGLE_STYLE = "-fx-base: transparent";
    private static final double TOGGLE_WIDTH = 45.0;
    private static final double TOGGLE_HEIGHT = 17.0;
    private static final String TOGGLE_TEXT_YES = "yes";
    private static final String TOGGLE_TEXT_NO = "no";
    private static final String TOGGLE_STYLE_YES = "toggle-graphic-on";
    private static final String TOGGLE_STYLE_NO = "toggle-graphic-off";
    
    private QENamelist nmlPh;
    private QENatTodo ntCard;
    
    public QEFXPh_IrrRepController(QEFXMainController mainController, QEInput input, QEAtomicPositions atomicPositions, QEAtomicSpecies atomicSpecies) {
        super(mainController, input);
        this.atomBinder = null;
        this.atomicPositions = atomicPositions;
        this.atomicSpecies = atomicSpecies;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        this.nmlPh = this.input.getNamelist(QEInput.NAMELIST_INPUTPH); 
        this.ntCard = (QENatTodo)this.input.getCard(QENatTodo.CARD_NAME);
        //setup form function list
        this.initializeAtomBinder();
        
        if(this.nmlPh!=null){
            this.setupAtomicMasses();
            this.setupRangeToggle();
            this.setupSelectToggle();
            this.setupRangeFrom(this.nmlPh);
            this.setupRangeTo(this.nmlPh);
            this.disableIrrRepOpts();
            this.setupIrrRepTable();
            this.setupNTDTable();
            this.setupSelectColumn(this.nmlPh,this.selectionMutex,this.selectionColumn,"modenum",this.ToggleList);
            this.setupIndirectSelectColumn(this.nmlPh, this.NTDselectionColumn, "nat_todo", ToggleList2);
        }
    }
    //functions for form functionality
    private void initializeAtomBinder(){
        if (this.irr_rep_table == null) {
            throw new IllegalArgumentException("irr_rep_table is null.");
        }
        this.atomBinder = new AtomAnsatzBinder(this.irr_rep_table, this.atomicPositions);
        if (this.atomBinder == null){
            throw new IllegalArgumentException("atomBinder Failed.");
        }
        if (this.nat_todo_table == null){
            throw new IllegalArgumentException("nat_todo_table is null.");
        }
        this.atomBinder2 = new AtomAnsatzBinder(this.nat_todo_table, this.atomicPositions);
        if (this.atomBinder2 == null){
            throw new IllegalArgumentException("atomBinder2 Failed.");
        }
    }
    
    private void setupRangeToggle(){
        if (this.sel_index_by_range == null) {
            throw new IllegalArgumentException("sel_index_by_range is null.");
            //return;
        }
        this.rangeSelector = new FXToggleBoolean(this.sel_index_by_range, false);
        if (this.rangeSelector == null){
            throw new IllegalArgumentException("rangeSelector is null.");
            //return;
        }
        if (this.rangeLabel != null) {this.rangeSelector.setLabel(this.rangeLabel);}
        if (this.rangeButton != null) {
            this.rangeSelector.setDefault(false, this.rangeButton);
            this.sel_index_by_range.setOnAction(event->{
                if(this.rangeSelector.isSelected()||this.sel_index_by_range.isSelected()){
                    this.selectSelector.setFalse();
                    this.enableIndexByRange();
                }
                else{
                    this.disableRanges();//incase the default button switches it off
                }
            });
            
        }
    }
    
    private void setupRangeFrom(QENamelist nmlPh){
        if (this.index_from == null) {
            throw new IllegalArgumentException("index_from is null.");
            //return;
        }
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer("start_irr"),this.index_from);
        if (this.FromLabel != null) {item.setLabel(this.FromLabel);}
        if (this.FromButton != null) {item.setDefault(1, this.FromButton);}
        item.pullAllTriggers();
        this.rangeFrom = item;
    }
    
    private void setupRangeTo(QENamelist nmlPh){
        if (this.index_to == null) {
            throw new IllegalArgumentException("index_to is null.");
            //return;
        }
        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlPh.getValueBuffer("last_irr"),this.index_to);
        if (this.ToLabel != null) {item.setLabel(this.ToLabel);}
        if (this.ToButton != null) {item.setDefault(3, this.ToButton);}//Default should be 3*nat, what is nat
        item.pullAllTriggers();
        this.rangeTo = item;
    }
    
    private void setupSelectToggle(){
        if (this.sel_index_by_sel == null) {
            throw new IllegalArgumentException("sel_index_by_sel is null.");
            //return;
        }
        this.selectSelector = new FXToggleBoolean(this.sel_index_by_sel,false);
        if (this.selectSelector == null){
            throw new IllegalArgumentException("selectSelector is null.");
            //return;
        }
        if (this.selectLabel != null) {this.selectSelector.setLabel(this.selectLabel);}
        if (this.selectButton != null) {
            this.selectSelector.setDefault(false, this.selectButton);
            this.sel_index_by_sel.setOnAction(event->{
                if(this.selectSelector.isSelected()||this.sel_index_by_sel.isSelected()){
                    this.rangeSelector.setFalse();
                    this.enableIndexBySelection();
                }
                else{
                    this.disableIndex();//incase the default button switches it off
                }
            });
        }
    }
    
    private void disableIrrRepOpts(){
        disableRanges();
        disableIndex();
    }
    
    private void enableIndexByRange(){
        disableIndex();
        enableRanges();
    }
    
    private void enableIndexBySelection(){
        disableRanges();
        enableIndex();
    }
    
    private void enableRanges(){
        if(this.rangeGrid!=null){this.rangeGrid.setDisable(false);}
        rangeFrom.setDisable(false);
        rangeTo.setDisable(false);
        nmlPh.addProtectedValue("start_irr");
        nmlPh.addProtectedValue("last_irr");
    }
    
    private void disableRanges(){
        if(this.rangeGrid!=null){this.rangeGrid.setDisable(true);}
        rangeFrom.setDisable(true);
        rangeFrom.emptyField();
        rangeTo.setDisable(true);
        rangeTo.emptyField();
        nmlPh.removeValue("start_irr");
        nmlPh.removeValue("last_irr");
        
    }
    
    private void enableIndex(){
        if(this.selectGrid!=null){this.selectGrid.setDisable(false);}
        if(this.NTDselectGrid!=null){this.NTDselectGrid.setDisable(false);}
        nmlPh.addProtectedValue("modenum");
        nmlPh.addProtectedValue("nat_todo");
    }
    
    private void disableIndex(){
        if(this.selectGrid!=null){this.selectGrid.setDisable(true);}
        if(this.NTDselectGrid!=null){this.NTDselectGrid.setDisable(true);}
        nmlPh.removeValue("modenum");
        nmlPh.removeValue("nat_todo");
        for (QEFXToggleInteger item : ToggleList){
            redrawToggle(item.getControlItem());
        }
        for (FXToggleBoolean item : ToggleList2){
            redrawToggle(item.getControlItem());
        }
    }
    
    private void setupIrrRepTable(){
        this.setupIndexColumn(this.indexColumn);
        this.setupElementColumn(this.elementColumn);
        this.setupXYZColumn("x", xColumn);
        this.setupXYZColumn("y", yColumn);
        this.setupXYZColumn("z", zColumn);
        if (this.atomBinder != null) {this.atomBinder.bindTable();}
    }
    
    private void setupNTDTable(){
        this.setupIndexColumn(this.NTDindexColumn);
        this.setupElementColumn(this.NTDelementColumn);
        this.setupXYZColumn("x", NTDxColumn);
        this.setupXYZColumn("y", NTDyColumn);
        this.setupXYZColumn("z", NTDzColumn);
        if (this.atomBinder2 != null) {this.atomBinder2.bindTable();}
    }
    
    private void setupSelectColumn(QENamelist nmlPh, ToggleGroup TG, GridPane GP, String QEVB, List<QEFXToggleInteger> TIL){
        int numAtoms = this.atomicPositions.numPositions();
        for (int i=0;i<numAtoms;i++){
            //Create ToggleButton
            ToggleButton selectInstance = new ToggleButton();
            selectInstance.setToggleGroup(TG);
            setupSelectToggle(i,selectInstance,nmlPh,QEVB,TIL,TG);
            //add to table
            GP.addRow(i, selectInstance);
        }
    }
    
    private void setupIndirectSelectColumn(QENamelist nmlPh,GridPane GP, String QEVB, List<FXToggleBoolean> TBL){
        int numAtoms = this.atomicPositions.numPositions();
        for (int i=0;i<numAtoms;i++){
            //Create ToggleButton
            ToggleButton selectInstance = new ToggleButton();
            setupIndirectToggle(selectInstance,nmlPh,QEVB,TBL);
            //add to table
            GP.addRow(i, selectInstance);
        }
    }
    
    private void setupSelectToggle(int index, ToggleButton t, QENamelist nmlPh, String QEVB, List<QEFXToggleInteger> TIL, ToggleGroup TG){
        if (t==null){return;}
        t.setText("");
        t.setStyle(TOGGLE_STYLE);
        QEFXToggleInteger instance = new QEFXToggleInteger(nmlPh.getValueBuffer(QEVB),t,false,index,-1);
        if (instance==null){
            throw new IllegalArgumentException("instance is null");
        }
        TIL.add(index, instance);
        redrawToggle(t);
        t.setOnAction(event->{resetToggles(nmlPh,TIL,TG,QEVB);});
    }
    
    private void setupIndirectToggle(ToggleButton t, QENamelist nmlPh, String QEVB, List<FXToggleBoolean> TBL){
        if (t==null){return;}
        t.setText("");
        t.setStyle(TOGGLE_STYLE);
        FXToggleBoolean instance = new FXToggleBoolean(t,false);
        if (instance==null){
            throw new IllegalArgumentException("instance is null");
        }
        TBL.add(instance);
        redrawToggle(t);
        t.setOnAction(event->{resetIndirectToggles(nmlPh,TBL,QEVB);});
    }
    
    private void resetToggles(QENamelist nmlPh, List<QEFXToggleInteger> TIL, ToggleGroup TG, String QEVB){
        int selected = -1;
        int tempsel = 0;
        for (QEFXToggleInteger item : TIL){//use tempsel to find index of selected element
            if(TG.getSelectedToggle()!=item.getControlItem()){tempsel++;}
            else{selected=tempsel;}
            redrawToggle(item.getControlItem());
        }
        nmlPh.getValueBuffer(QEVB).setValue(selected);
    }
    private void resetIndirectToggles(QENamelist nmlPh, List<FXToggleBoolean> TBL, String QEVB){
        int selected = 0;
        int tempsel = 0;
        this.ntCard.clear();
        for (FXToggleBoolean item : TBL){//use selected to count selected elements, tempsel to track index
            if(item.isSelected()){selected++;this.ntCard.addIndex((Integer)tempsel);}
            redrawToggle(item.getControlItem());
            tempsel++;
        }
        nmlPh.getValueBuffer(QEVB).setValue(selected);
    }
    
    private void setupIndexColumn(TableColumn<AtomAnsatz, Integer> IC){
        if (IC == null) {return;}
        IC.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, Integer>("index"));
    }
    
    private void setupElementColumn(TableColumn<AtomAnsatz,String> EC){
        if (EC == null) {return;}
        EC.setCellFactory(TextFieldTableCell.<AtomAnsatz> forTableColumn());
        EC.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, String>("element"));
        EC.setComparator((String element1, String element2) -> {
            int atomNum1 = ElementUtil.getAtomicNumber(element1);
            int atomNum2 = ElementUtil.getAtomicNumber(element2);
            if (atomNum1 < atomNum2) {
                return -1;
            } else if (atomNum1 > atomNum2) {
                return 1;
            }
            return 0;
        });
    }
    
    private void setupXYZColumn(String name, TableColumn<AtomAnsatz, String> xyzColumn){
        if (name == null || name.isEmpty()) {
            return;
        }

        if (xyzColumn == null) {
            return;
        }

        xyzColumn.setCellFactory(new XYZCellFactory());
        xyzColumn.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, String>(name));
    }

    private static class XYZCellFactory implements Callback<TableColumn<AtomAnsatz, String>, TableCell<AtomAnsatz, String>> {
        private Callback<TableColumn<AtomAnsatz, String>, TableCell<AtomAnsatz, String>> callback;
        public XYZCellFactory() {
            this.callback = TextFieldTableCell.<AtomAnsatz> forTableColumn();
        }
        @Override
        public TableCell<AtomAnsatz, String> call(TableColumn<AtomAnsatz, String> param) {
            TableCell<AtomAnsatz, String> cell = this.callback.call(param);

            cell.setStyle(CELL_STYLE_MOBILE);

            cell.itemProperty().addListener(o -> {
                String item = cell.getItem();
                if (item == null) {
                    return;
                }

                String strPosition = AtomAnsatz.extractPosition(item);
                if (strPosition != null) {
                    cell.setItem(strPosition);
                }

                if (AtomAnsatz.containsMobile(item)) {
                    if (AtomAnsatz.extractMobile(item)) {
                        cell.setStyle(CELL_STYLE_MOBILE);
                    } else {
                        cell.setStyle(CELL_STYLE_FIXED);
                    }
                }
            });

            return cell;
        }
    }

    private void redrawToggle(ToggleButton t) {
        if (t == null) {return;}
        if (t.isSelected()) {
            t.setGraphic(ToggleGraphics.getGraphic(
                    TOGGLE_WIDTH, TOGGLE_HEIGHT, true, TOGGLE_TEXT_YES, TOGGLE_STYLE_YES));
        } else {
            t.setGraphic(ToggleGraphics.getGraphic(
                    TOGGLE_WIDTH, TOGGLE_HEIGHT, false, TOGGLE_TEXT_NO, TOGGLE_STYLE_NO));
        }
    }
    
    private void setupAtomicMasses(){
        for(int i=0;i<this.atomicSpecies.numSpecies();i++){
            String str = "amass("+Integer.toString(i+1)+")";
            if(this.nmlPh.getValueBuffer(str).hasValue()){
                this.nmlPh.removeValue(str);
            }
            this.nmlPh.addProtectedValue(str);
            this.nmlPh.getValueBuffer(str).setValue(this.atomicSpecies.getMass(i));
        }
    }
}
