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

import static burai.input.QEInput.NAMELIST_INPUTPH;
import burai.input.card.QENatTodo;
import burai.input.card.QEQPoints;

import burai.input.correcter.QEInputCorrecter;
import burai.input.correcter.QEPhononInputCorrector;
import burai.input.namelist.QENamelist;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEPhononInput extends QESecondaryInput {
    //constructors
    public QEPhononInput(){
        super();
        init_common();
    }
    
    public QEPhononInput(String fileName) throws IOException {
        super(fileName);
        init_common();
    }
    
    public QEPhononInput(File file) throws IOException {
        super(file);
        init_common();
    }
    
    //place all the initial variable assignments in here
    private void init_common(){
        
    }
    
    //construct objects
    
    //functions to write values to namelist
    
    //overrides
    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlPh = this.namelists.containsKey(NAMELIST_INPUTPH);
        this.setupNamelist(NAMELIST_INPUTPH, reader);
        if(!hasNmlPh){
            QENamelist nmlPh = this.namelists.get(NAMELIST_INPUTPH);
            //add namelist variables with either addProtectedValue, addDeletingValue, or addBindingValue
            //output directory and prefix must be the same as in SCF
            nmlPh.addProtectedValue("outdir");
            nmlPh.addProtectedValue("prefix");
            //control options
            nmlPh.addProtectedValue("recover");
            nmlPh.addProtectedValue("low_directory_check");
            nmlPh.addProtectedValue("only_init");//used for GRID parallel, set to false
            nmlPh.addProtectedValue("lqdir");
            //booleans
            nmlPh.addProtectedValue("ldisp");
            nmlPh.addProtectedValue("nogg");
            nmlPh.addProtectedValue("ldiag");
            nmlPh.addProtectedValue("trans");
            nmlPh.addProtectedValue("epsil");
            nmlPh.addProtectedValue("lrpa");
            nmlPh.addProtectedValue("lnoloc");
            nmlPh.addProtectedValue("fpol");
            nmlPh.addProtectedValue("zeu");
            nmlPh.addProtectedValue("zue");
            nmlPh.addProtectedValue("elop");
            nmlPh.addProtectedValue("lraman");
            nmlPh.addProtectedValue("search_sym");
            nmlPh.addProtectedValue("qplot");
            nmlPh.addProtectedValue("q2d");
            nmlPh.addProtectedValue("q_in_band_form");
            nmlPh.addProtectedValue("reduce_io");
            
            //string values
            nmlPh.addProtectedValue("electron_phonon");
            
            //double values
            nmlPh.addProtectedValue("max_seconds");
            nmlPh.addProtectedValue("tr2_ph");
            nmlPh.addProtectedValue("alpha_mix(1)");
            
            //integer values
            nmlPh.addProtectedValue("niter_ph");
            nmlPh.addProtectedValue("nmix_ph");
            
        }
    }
    
    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        this.setupCard(new QEQPoints(), reader);
        this.setupCard(new QENatTodo(), reader);
    }
    
    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QEPhononInputCorrector(this);
    }
    
    @Override
    public void reload() {
        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }
    }
    
    @Override
    public QEPhononInput copy() {
        QEPhononInput input = new QEPhononInput();
        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }
    
}
