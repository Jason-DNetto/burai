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
 * and Samantha Adnett, formerly QUT
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.input.correcter;

import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.card.QEKPoints;
import burai.input.namelist.QENamelist;

public abstract class QEInputCorrecter {

    protected QEInput input;

    protected QENamelist nmlControl;
    protected QENamelist nmlSystem;
    protected QENamelist nmlElectrons;
    protected QENamelist nmlIons;
    protected QENamelist nmlCell;
    protected QENamelist nmlDos;
    protected QENamelist nmlProjwfc;
    protected QENamelist nmlBands;
    protected QENamelist nmlPh;
    protected QENamelist nmlIn;
    protected QENamelist nmlPB;

    protected QEAtomicSpecies cardSpecies;
    protected QEAtomicPositions cardPositions;
    protected QEKPoints cardKPoints;
    protected QECellParameters cardCell;

    protected QEInputCorrecter(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);
        this.nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        this.nmlElectrons = this.input.getNamelist(QEInput.NAMELIST_ELECTRONS);
        this.nmlIons = this.input.getNamelist(QEInput.NAMELIST_IONS);
        this.nmlCell = this.input.getNamelist(QEInput.NAMELIST_CELL);
        this.nmlDos = this.input.getNamelist(QEInput.NAMELIST_DOS);
        this.nmlProjwfc = this.input.getNamelist(QEInput.NAMELIST_PROJWFC);
        this.nmlBands = this.input.getNamelist(QEInput.NAMELIST_BANDS);
        this.nmlPh = this.input.getNamelist(QEInput.NAMELIST_INPUTPH);
        this.nmlIn = this.input.getNamelist(QEInput.NAMELIST_INPUT);
        this.nmlPB = this.input.getNamelist(QEInput.NAMELIST_PLOTBANDS);

        QECard card = null;
        card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        this.cardSpecies = (card != null && card instanceof QEAtomicSpecies) ? ((QEAtomicSpecies) card) : null;
        card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        this.cardPositions = (card != null && card instanceof QEAtomicPositions) ? ((QEAtomicPositions) card) : null;
        card = this.input.getCard(QEKPoints.CARD_NAME);
        this.cardKPoints = (card != null && card instanceof QEKPoints) ? ((QEKPoints) card) : null;
        card = this.input.getCard(QECellParameters.CARD_NAME);
        this.cardCell = (card != null && card instanceof QECellParameters) ? ((QECellParameters) card) : null;
    }

    public abstract void correctInput();

}
