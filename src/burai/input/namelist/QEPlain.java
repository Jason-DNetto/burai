/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public class QEPlain extends QEValuePlain {

    private String plainValue;

    public QEPlain(String name, String value) {
        super(name);
        this.plainValue = value;
    }

    @Override
    public int getIntegerValue() {//never use this
        return Integer.parseInt(this.plainValue);
    }

    @Override
    public double getRealValue() {
        return Double.parseDouble(this.plainValue);
    }

    @Override
    public boolean getLogicalValue() {
        return false;
    }

    @Override
    public String getCharacterValue() {
        return this.plainValue;
    }
}
