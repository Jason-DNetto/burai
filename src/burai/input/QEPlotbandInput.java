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
package burai.input;
import static burai.input.QEInput.NAMELIST_PLOTBANDS;

import burai.input.correcter.QEInputCorrecter;
import burai.input.correcter.QEPlotbandInputCorrector;
import burai.input.namelist.QENamelist;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class QEPlotbandInput extends QESecondaryInput {
    public QEPlotbandInput(){
        super();
        init_common();
    }
    
    public QEPlotbandInput(File file) throws IOException {
        super(file);
        init_common();
    }
    
    //place all the initial variable assignments in here
    private void init_common(){
        
    }
    
    //overrides
    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlPB = this.namelists.containsKey(NAMELIST_PLOTBANDS);
        this.setupNamelist(NAMELIST_PLOTBANDS, reader);
        if(!hasNmlPB){
            QENamelist nmlPB = this.namelists.get(NAMELIST_PLOTBANDS);
            //nmlPB.addPlainValue("inpfilename");
            nmlPB.setValue("inpfilename = matdyn.out"+System.lineSeparator()+"0 1000"+System.lineSeparator()+
                    "espresso.phonon.gnu"+System.lineSeparator()+"espresso.phonon.ps"
                    +System.lineSeparator()+"1000"+System.lineSeparator()+"1 1000");//input file name
            //nmlPB.addPlainValue("ylim");//y axis limits
            //nmlPB.setValue("ylim = 0 1000");//formats postscript
            //nmlPB.addPlainValue("outfilename");
            //nmlPB.setValue("outfilename = espresso.phonon.gnu");//output file name
            //nmlPB.addPlainValue("outfileps");
            //nmlPB.setValue("outfileps = espresso.phonon.ps");//output postscript
            //nmlPB.addPlainValue("xlim");//x limit
            //nmlPB.setValue("xlim = 1000");//formats postscript
            //nmlPB.addPlainValue("xticks");//xticks
            //nmlPB.setValue("xticks = 1 " + nmlPB.getValue("xlim").getCharacterValue());//formats postscript
        }
    }
    
    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        //no Cards
    }
    
    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QEPlotbandInputCorrector(this);
    }
    
    @Override
    public void reload() {
        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }
    }
    
    @Override
    public QEPlotbandInput copy() {
        QEPlotbandInput input = new QEPlotbandInput();
        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }
}
