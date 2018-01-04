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

import static burai.input.QEInput.NAMELIST_INPUT;
import burai.input.card.QEQPoints;

import burai.input.correcter.QEInputCorrecter;
import burai.input.correcter.QEQ2RInputCorrector;
import burai.input.namelist.QENamelist;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class QEQ2RInput extends QESecondaryInput {
    
    public QEQ2RInput(){
        super();
        init_common();
    }
    
    public QEQ2RInput(File file) throws IOException {
        super(file);
        init_common();
    }
    
    //place all the initial variable assignments in here
    private void init_common(){
        
    }
    
    //overrides
    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlIn = this.namelists.containsKey(NAMELIST_INPUT);
        this.setupNamelist(NAMELIST_INPUT, reader);
        if(!hasNmlIn){
            QENamelist nmlIn = this.namelists.get(NAMELIST_INPUT);
            nmlIn.addProtectedValue("fildyn");
            nmlIn.setValue("fildyn = dyn");
            nmlIn.addProtectedValue("flfrc");
            //nmlIn.addProtectedValue("flfrq");
        }
    }
    
    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        //no cards
    }
    
    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QEQ2RInputCorrector(this);
    }
    
    @Override
    public void reload() {
        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }
    }
    
    @Override
    public QEQ2RInput copy() {
        QEQ2RInput input = new QEQ2RInput();
        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }
}
