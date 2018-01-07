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
package burai.input.correcter;

import burai.input.QEInput;
import burai.input.namelist.QEValue;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QEPhononInputCorrector extends QEInputCorrecter {
    public QEPhononInputCorrector(QEInput input) {
        super(input);
    }
    
    @Override
    public void correctInput() {
        correctNamelistInputPh();
    }
    
    private void correctNamelistInputPh(){
        if(this.nmlPh==null){return;}
        QEValue value = null;
        value = this.nmlPh.getValue("ldisp");
        if(value==null){this.nmlPh.getValueBuffer("ldisp").setValue(false);}
        value = this.nmlPh.getValue("qplot");
        if(value==null){this.nmlPh.getValueBuffer("qplot").setValue(Boolean.FALSE);}
        value = this.nmlPh.getValue("qplot");
        if("Wave Vector".equals(value.toString())){this.nmlPh.getValueBuffer("qplot").setValue(Boolean.FALSE);}
        if("List of Points".equals(value.toString())){this.nmlPh.getValueBuffer("qplot").setValue(Boolean.TRUE);}
        value = this.nmlPh.getValue("search_sym");
        if(value==null){this.nmlPh.getValueBuffer("search_sym").setValue(true);}
        value = this.nmlPh.getValue("trans");
        if(value==null){this.nmlPh.getValueBuffer("trans").setValue(true);}
    }
}
