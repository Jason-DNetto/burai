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

package burai.input.card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import burai.com.math.Matrix3D;
import burai.input.QEInput;

public class QEKPoints extends QECard {

    public static final String CARD_NAME = "K_POINTS";

    private static final String OPTION_GAMMA = "gamma";
    private static final String OPTION_AUTOMATIC = "automatic";
    private static final String OPTION_TPIBA = "tpiba";
    private static final String OPTION_CRYSTAL = "crystal";
    private static final String OPTION_TPIBA_B = "tpiba_b";
    private static final String OPTION_CRYSTAL_B = "crystal_b";
    private static final String OPTION_TPIBA_C = "tpiba_c";
    private static final String OPTION_CRYSTAL_C = "crystal_c";

    private static final double ACCURATE_K_RANGE = 24.0;
    private static final double RECOMMENDED_K_RANGE = 12.0;

    private int[] kGrid;
    private int[] kOffset;

    private List<QEKPoint> kList;

    public QEKPoints() {
        super(CARD_NAME);

        this.kGrid = new int[] { 0, 0, 0 };
        this.kOffset = new int[] { 0, 0, 0 };
        this.kList = new ArrayList<QEKPoint>();

        this.setDefaultOption();
    }

    public int[] getKGrid() {
        int[] kOut = new int[3];
        kOut[0] = this.kGrid[0];
        kOut[1] = this.kGrid[1];
        kOut[2] = this.kGrid[2];

        return kOut;
    }

    public int[] getKOffset() {
        int[] kOut = new int[3];
        kOut[0] = this.kOffset[0];
        kOut[1] = this.kOffset[1];
        kOut[2] = this.kOffset[2];

        return kOut;
    }

    public void setKGrid(int[] kGrid) {
        if (kGrid == null || kGrid.length < 3) {
            return;
        }

        this.kGrid[0] = Math.max(kGrid[0], 1);
        this.kGrid[1] = Math.max(kGrid[1], 1);
        this.kGrid[2] = Math.max(kGrid[2], 1);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KGRID_CHANGED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setKOffset(int[] kOffset) {
        if (kOffset == null || kOffset.length < 3) {
            return;
        }

        this.kOffset[0] = kOffset[0] == 0 ? 0 : 1;
        this.kOffset[1] = kOffset[1] == 0 ? 0 : 1;
        this.kOffset[2] = kOffset[2] == 0 ? 0 : 1;

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KGRID_CHANGED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public int numKPoints() {
        return this.kList.size();
    }

    public QEKPoint getKPoint(int i) {
        if (i < 0 || i >= this.kList.size()) {
            throw new IllegalArgumentException("index of k-point is incorrect.");
        }

        QEKPoint k = this.kList.get(i);

        return k;
    }

    public void setKPoint(int i, QEKPoint k) {
        if (i < 0 || i >= this.kList.size()) {
            throw new IllegalArgumentException("index of k-point is incorrect.");
        }

        if (k == null) {
            return;
        }

        this.kList.set(i, k);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KPOINT_CHANGED);
            event.setKPointIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public Integer addKPoint(QEKPoint k) {
        if (k == null) {
            return null;
        }
        this.kList.add(k);
        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KPOINT_ADDED);
            event.setKPointIndex(this.kList.size() - 1);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
        return (Integer)this.numKPoints()-1;
    }
    
    public void setKPoint(int i,double x,double y,double z,double w) {
        this.kList.get(i).setX(x);
        this.kList.get(i).setY(y);
        this.kList.get(i).setZ(z);
        this.kList.get(i).setWeight(w);
    }

    public void removeKPoint(int index) {
        if (index < 0 || index >= this.kList.size()) {
            throw new ArrayIndexOutOfBoundsException("no k-point at index "+index);
        }
        this.kList.remove(index);
        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KPOINT_REMOVED);
            event.setKPointIndex(index);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setGamma() {
        this.setOption(OPTION_GAMMA);
    }

    public void setAutomatic() {
        this.setOption(OPTION_AUTOMATIC);
    }

    public void setTpiba() {
        this.setOption(OPTION_TPIBA);
    }

    public void setCrystal() {
        this.setOption(OPTION_CRYSTAL);
    }

    public void setTpibaB() {
        this.setOption(OPTION_TPIBA_B);
    }

    public void setCrystalB() {
        this.setOption(OPTION_CRYSTAL_B);
    }

    public void setTpibaC() {
        this.setOption(OPTION_TPIBA_C);
    }

    public void setCrystalC() {
        this.setOption(OPTION_CRYSTAL_C);
    }

    private void setOption(String option) {
        this.option = option;

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_UNIT_CHANGED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public boolean isGamma() {
        return OPTION_GAMMA.equals(this.option);
    }

    public boolean isAutomatic() {
        return OPTION_AUTOMATIC.equals(this.option);
    }

    public boolean isTpiba() {
        return OPTION_TPIBA.equals(this.option);
    }

    public boolean isCrystal() {
        return OPTION_CRYSTAL.equals(this.option);
    }

    public boolean isTpibaB() {
        return OPTION_TPIBA_B.equals(this.option);
    }

    public boolean isCrystalB() {
        return OPTION_CRYSTAL_B.equals(this.option);
    }

    public boolean isTpibaC() {
        return OPTION_TPIBA_C.equals(this.option);
    }

    public boolean isCrystalC() {
        return OPTION_CRYSTAL_C.equals(this.option);
    }

    private void checkOption() {
        if (this.option == null || this.option.isEmpty()) {
            this.setDefaultOption();
        }

        this.option = this.option.trim().toLowerCase();

        if (OPTION_GAMMA.equals(this.option)) {
            // NOP
        } else if (OPTION_AUTOMATIC.equals(this.option)) {
            // NOP
        } else if (OPTION_TPIBA.equals(this.option)) {
            // NOP
        } else if (OPTION_CRYSTAL.equals(this.option)) {
            // NOP
        } else if (OPTION_TPIBA_B.equals(this.option)) {
            // NOP
        } else if (OPTION_CRYSTAL_B.equals(this.option)) {
            // NOP
        } else if (OPTION_TPIBA_C.equals(this.option)) {
            // NOP
        } else if (OPTION_CRYSTAL_C.equals(this.option)) {
            // NOP
        } else {
            this.setDefaultOption();
        }
    }

    private void setDefaultOption() {
        this.option = OPTION_TPIBA;
    }

    @Override
    public boolean read(List<String> lines) throws IOException {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is null or empty.");
        }

        int startingLine = this.readUptoMyCard(lines);
        if (startingLine < 0) {
            return false;
        }

        this.checkOption();

        if (OPTION_GAMMA.equals(this.option)) {
            this.readGamma(startingLine, lines);

        } else if (OPTION_AUTOMATIC.equals(this.option)) {
            this.readAutomatic(startingLine, lines);

        } else {
            this.readTpiba(startingLine, lines);
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }

        return true;
    }

    private void readGamma(int startingLine, List<String> lines) throws IOException {
        this.kGrid[0] = 1;
        this.kGrid[1] = 1;
        this.kGrid[2] = 1;
        this.kOffset[0] = 0;
        this.kOffset[1] = 0;
        this.kOffset[2] = 0;
    }

    private void readAutomatic(int startingLine, List<String> lines) throws IOException {
        if (startingLine >= lines.size()) {
            throw new IOException("there are not enough lines in reading " + this.cardName);
        }

        String line = lines.get(startingLine);
        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < 6) {
            throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
        }

        int[] kGrid = { 1, 1, 1 };
        int[] kOffset = { 0, 0, 0 };

        try {
            kGrid[0] = Integer.parseInt(subLines[0]);
            kGrid[1] = Integer.parseInt(subLines[1]);
            kGrid[2] = Integer.parseInt(subLines[2]);
            kOffset[0] = Integer.parseInt(subLines[3]);
            kOffset[1] = Integer.parseInt(subLines[4]);
            kOffset[2] = Integer.parseInt(subLines[5]);
        } catch (NumberFormatException e) {
            throw new IOException("incorrect data in reading " + this.cardName + ": " + line);
        }

        this.kGrid[0] = kGrid[0];
        this.kGrid[1] = kGrid[1];
        this.kGrid[2] = kGrid[2];
        this.kOffset[0] = kOffset[0];
        this.kOffset[1] = kOffset[1];
        this.kOffset[2] = kOffset[2];
    }

    private void readTpiba(int startingLine, List<String> lines) throws IOException {
        if (startingLine >= lines.size()) {
            throw new IOException("there are not enough lines in reading " + this.cardName);
        }

        String line = lines.get(startingLine);
        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
        }

        int numKPoint = Integer.parseInt(subLines[0]);
        if (OPTION_TPIBA_C.equals(this.option) || OPTION_CRYSTAL_C.equals(this.option)) {
            numKPoint = 3;
        }

        for (int i = 0; i < numKPoint; i++) {
            int iLine = startingLine + i + 1;
            if (iLine >= lines.size()) {
                break;
            }

            line = lines.get(iLine);
            if (line == null || line.trim().isEmpty()) {
                break;
            }

            QEKPoint k = null;
            try {
                k = new QEKPoint(line);
            } catch (NumberFormatException e) {
                break;
            }

            if (k != null) {
                this.kList.add(k);
            }
        }

        if (OPTION_TPIBA_C.equals(this.option) || OPTION_CRYSTAL_C.equals(this.option)) {
            while (this.kList.size() > 3) {
                this.kList.remove(kList.size() - 1);
            }

            if (!this.kList.isEmpty()) {
                QEKPoint k = this.kList.get(0);
                if (k != null) {
                    if (!k.hasLetter()) {
                        this.kList.add(0, new QEKPoint(k.getX(), k.getY(), k.getZ(), k.getWeight()));
                    } else {
                        this.kList.add(0, new QEKPoint(k.getLetter(), k.getWeight()));
                    }
                }
            }
        }
    }

    @Override
    public void copyToCard(QECard card) {
        if (!(card instanceof QEKPoints)) {
            throw new IllegalArgumentException("card is incorrect.");
        }

        QEKPoints kPoints = (QEKPoints) card;

        kPoints.option = this.option;

        if (kPoints.kGrid == null) {
            kPoints.kGrid = new int[3];
        }
        kPoints.kGrid[0] = this.kGrid[0];
        kPoints.kGrid[1] = this.kGrid[1];
        kPoints.kGrid[2] = this.kGrid[2];

        if (kPoints.kOffset == null) {
            kPoints.kOffset = new int[3];
        }
        kPoints.kOffset[0] = this.kOffset[0];
        kPoints.kOffset[1] = this.kOffset[1];
        kPoints.kOffset[2] = this.kOffset[2];

        if (kPoints.kList == null) {
            kPoints.kList = new ArrayList<QEKPoint>();
        } else {
            kPoints.kList.clear();
        }
        for (QEKPoint k : this.kList) {
            kPoints.kList.add(k);
        }

        if (kPoints.listeners != null) {
            QECardEvent event = new QECardEvent(kPoints);
            for (QECardListener listener : kPoints.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public void clear() {
        this.setDefaultOption();

        if (this.kGrid == null) {
            this.kGrid = new int[3];
        }
        this.kGrid[0] = 1;
        this.kGrid[1] = 1;
        this.kGrid[2] = 1;

        if (this.kOffset == null) {
            this.kOffset = new int[3];
        }
        this.kOffset[0] = 0;
        this.kOffset[1] = 0;
        this.kOffset[2] = 0;

        if (this.kList != null) {
            this.kList.clear();
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_KPOINT_CLEARED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public String toString(boolean with_Header){
        String str="";
        if (this.isGamma()) {
            if (with_Header){
                str = CARD_NAME + " {" + this.option + "}" + System.lineSeparator();
            }
        } else if (this.isAutomatic()) {
            if (with_Header){
                str = CARD_NAME + " {" + this.option + "}" + System.lineSeparator();
            }
            str = str + String.format("%2d %2d %2d  %d %d %d%n",
                    this.kGrid[0], this.kGrid[1], this.kGrid[2], this.kOffset[0], this.kOffset[1], this.kOffset[2]);

        } else {
            //boolean asInteger = !(OPTION_TPIBA.equals(this.option) || OPTION_CRYSTAL.equals(this.option));
            //when calculating band structures, the band options set the k list option when the scf option might want the k list option to be something else
            if (this.kList.size()>0){
                if (with_Header){
                    str = CARD_NAME + " {" + this.option + "}" + System.lineSeparator();
                }
                str = str + this.kList.size() + System.lineSeparator();
                for (QEKPoint k : this.kList) {
                    if(k.hasLetter()){
                        str = str + k.toString(true) + System.lineSeparator();
                    } else {
                        str = str + k.toString(false) + System.lineSeparator();
                    }
                }
            } else {//if no k list, but k grid, print k grid, if you want nothing, pick gamma
                if (with_Header){
                    str = CARD_NAME + " {automatic}" + System.lineSeparator();
                }
                str = str + String.format("%2d %2d %2d  %d %d %d%n",
                    this.kGrid[0], this.kGrid[1], this.kGrid[2], this.kOffset[0], this.kOffset[1], this.kOffset[2]);
            }
        }

        return str;
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }

    public void setAccurateCondition(QEInput input) {
        this.setRecommendedCondition(input, ACCURATE_K_RANGE);
    }

    public void setRecommendedCondition(QEInput input) {
        this.setRecommendedCondition(input, RECOMMENDED_K_RANGE);
    }

    private void setRecommendedCondition(QEInput input, double kRange) {
        if (input == null) {
            return;
        }

        double[][] lattice = input.getLattice();
        if (lattice == null) {
            return;
        }

        double[][] lattInv = Matrix3D.trans(Matrix3D.inverse(lattice));
        if (lattInv == null) {
            return;
        }
        
        double norm1 = Matrix3D.norm(lattInv[0]);
        double norm2 = Matrix3D.norm(lattInv[1]);
        double norm3 = Matrix3D.norm(lattInv[2]);
        if(this.option==OPTION_GAMMA||this.option==OPTION_AUTOMATIC){
        int numK1 = Math.max(1, (int) (Math.rint(kRange * norm1) + 0.1));
        int numK2 = Math.max(1, (int) (Math.rint(kRange * norm2) + 0.1));
        int numK3 = Math.max(1, (int) (Math.rint(kRange * norm3) + 0.1));

        if ((numK1 * numK2 * numK3) == 0) {
            this.option = OPTION_GAMMA;
        } else {
            this.option = OPTION_AUTOMATIC;
        }

        if (this.kGrid == null || this.kGrid.length < 3) {
            this.kGrid = new int[3];
        }
        this.kGrid[0] = numK1;
        this.kGrid[1] = numK2;
        this.kGrid[2] = numK3;
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }
}
