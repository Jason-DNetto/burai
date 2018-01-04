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
package burai.input.card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class QEQPoints extends QECard{
    public static final String CARD_NAME = "Q_POINTS";
    private List<QEQPoint> qList;
    protected Boolean option;
    
    public QEQPoints() {
        super(CARD_NAME);
        this.qList = new ArrayList<QEQPoint>();
        this.option=new Boolean(false);
        this.setDefaultOption();
    }
    public int numQPoints() {
        return this.qList.size();
    }

    public QEQPoint getQPoint(int i) {
        if (i < 0 || i >= this.qList.size()) {
            throw new IllegalArgumentException("index of q-point is incorrect.");
        }
        QEQPoint q = this.qList.get(i);
        return q;
    }
    
    public void setQPoint(int i, QEQPoint q) {
        if (i < 0) {
            throw new IllegalArgumentException("index of q-point is incorrect.");
        }
        if(i >= this.qList.size()){addQPoint(q);return;}
        if (q == null) {return;}
        this.qList.set(i, q);
        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_QPOINT_CHANGED);
            event.setQPointIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public Integer addQPoint(QEQPoint q) {
        if (q == null) {return null;}
        this.qList.add(q);
        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_QPOINT_ADDED);
            event.setQPointIndex(this.qList.size() - 1);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
        return (Integer)this.qList.size();
    }
    
    public void makeQPoint(double x, double y, double z){
        QEQPoint item = new QEQPoint(x,y,z);
        this.qList.add(item);
    }
    
    public void makeQPoint(double x, double y, double z, double weight){
        QEQPoint item = new QEQPoint(x,y,z,weight);
        this.qList.add(item);
    }

    public void removeQPoint(int index) {
        if (index < 0 || index >= this.qList.size()) {
            return;
        }

        this.qList.remove(index);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_QPOINT_REMOVED);
            event.setQPointIndex(index);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        /*String str = CARD_NAME + " {" + this.option + "}" + System.lineSeparator();

        if (this.isGamma()) {
            // NOP

        } else if (this.isAutomatic()) {
            str = str + String.format("%2d %2d %2d  %d %d %d%n",
                    this.kGrid[0], this.kGrid[1], this.kGrid[2], this.kOffset[0], this.kOffset[1], this.kOffset[2]);

        } else {
            boolean asInteger = !(OPTION_TPIBA.equals(this.option) || OPTION_CRYSTAL.equals(this.option));
            str = str + this.kList.size() + System.lineSeparator();
            for (QEKPoint k : this.kList) {
                str = str + k.toString(asInteger) + System.lineSeparator();
            }
        }

        return str;*/
        //return Q point card as the number of points on the first line, then the point.toString on each new line
        String str = "";
        if(this.option==(Boolean)true){//if qplot, print number of qpoints
            str = Integer.toString(this.qList.size()) + System.lineSeparator();
        }
        for (QEQPoint q : this.qList) {
            str = str + q.toString() + System.lineSeparator();
        }
        return str;
    }
    
    @Override
    public void copyToCard(QECard card) {
        if (!(card instanceof QEQPoints)) {
            throw new IllegalArgumentException("card is incorrect.");
        }

        QEQPoints qPoints = (QEQPoints) card;

        qPoints.option = this.option;

        if (qPoints.qList == null) {
            qPoints.qList = new ArrayList<QEQPoint>();
        } else {
            qPoints.qList.clear();
        }
        for (QEQPoint q : this.qList) {
            qPoints.qList.add(q);
        }

        if (qPoints.listeners != null) {
            QECardEvent event = new QECardEvent(qPoints);
            for (QECardListener listener : qPoints.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public void clear() {
        //this.setDefaultOption();

        /*if (this.kGrid == null) {
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
        this.kOffset[2] = 0;*/

        if (this.qList != null) {
            this.qList=null;
            this.qList = new ArrayList<QEQPoint>();
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_QPOINT_CLEARED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }
    
    private void setDefaultOption() {
        this.option = false;
    }
    
    public void setOption(boolean qplot){
        this.option=qplot;
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

        /*this.checkOption();

        if (OPTION_GAMMA.equals(this.option)) {
            this.readGamma(startingLine, lines);

        } else if (OPTION_AUTOMATIC.equals(this.option)) {
            this.readAutomatic(startingLine, lines);

        } else {
            this.readTpiba(startingLine, lines);
        }*/
        this.readQPlot(startingLine, lines);

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
    
    private void readQPlot(int startingLine, List<String> lines) throws IOException {
        if (startingLine >= lines.size()) {
            throw new IOException("there are not enough lines in reading " + this.cardName);
        }

        String line = lines.get(startingLine);
        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
        }

        int numQPoint = Integer.parseInt(subLines[0]);
        if(this.option){
            numQPoint = numQPoint-1;
            startingLine = startingLine+1;
        }

        for (int i = 0; i < numQPoint; i++) {
            int iLine = startingLine + i + 1;
            if (iLine >= lines.size()) {
                break;
            }

            line = lines.get(iLine);
            if (line == null || line.trim().isEmpty()) {
                break;
            }

            QEQPoint q = null;
            try {
                q = new QEQPoint(line, this.option);
            } catch (NumberFormatException e) {
                break;
            }

            if (q != null) {
                this.qList.add(q);
            }
        }

        /*if (OPTION_TPIBA_C.equals(this.option) || OPTION_CRYSTAL_C.equals(this.option)) {
            while (this.kList.size() > 3) {
                this.kList.remove(kList.size() - 1);
            }

            if (!this.qList.isEmpty()) {
                QEQPoint q = this.qList.get(0);
                if (q != null) {
                    if (!q.getOption()) {
                        this.qList.add(0, new QEKPoint(q.getX(), q.getY(), q.getZ()));
                    } else {
                        this.qList.add(0, new QEKPoint(q.getX(), q.getY(), q.getZ(), q.getWeight));
                    }
                }
            }
        }*/
    }
}
