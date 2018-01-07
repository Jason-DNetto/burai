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
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class QENatTodo extends QECard{
    public static final String CARD_NAME = "NAT_TODO";
    private List<Integer> indexList;
    
    public QENatTodo(){
        super(CARD_NAME);
        this.indexList=new ArrayList<Integer>();
    }
    public int numIndicies() {
        return this.indexList.size();
    }
    public Integer getIndex(int i){
        return this.indexList.get(i);
    }
    public void setIndex(int i, Integer value){
        this.indexList.add(i, value);
    }
    public void addIndex(Integer value){
        this.indexList.add(value);
    }
    public void removeIndex(int i){
        this.indexList.remove(i);
    }
    @Override
    public void clear(){
        this.indexList=null;
        this.indexList=new ArrayList<Integer>();
    }
    @Override
    public String toString(){
        String str = "";
        boolean first = true;
        for(Integer value : this.indexList){
            if(first){str = str + value.toString().trim();first=false;}
            else{str = str + " " + value.toString().trim();}
        }
        str = str + System.lineSeparator();
        return str;
    }
    @Override
    public void copyToCard(QECard card) {
        if(!(card instanceof QENatTodo)){
            throw new IllegalArgumentException("card is incorrect.");
        }
        QENatTodo natTodo = (QENatTodo) card;
        //clear thenew card, then copy the old card to the new card
        natTodo.indexList = null;
        if(natTodo.indexList==null){
            natTodo.indexList = new ArrayList<Integer>();
        }
        for(Integer value : this.indexList){
            natTodo.indexList.add(value);
        }
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
        this.readNatTodo(startingLine, lines);

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
    private void readNatTodo(int startingLine, List<String> lines) throws IOException {
        if (startingLine >= lines.size()) {
            throw new IOException("there are not enough lines in reading " + this.cardName);
        }

        String line = lines.get(startingLine);
        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
        }
        for(int i=0;i<subLines.length;i++){
            int iLine = startingLine + i + 1;
            if (iLine >= lines.size()) {
                break;
            }

            line = lines.get(iLine);
            if (line == null || line.trim().isEmpty()) {
                break;
            }
            Integer value;
            try{
                value = Integer.parseInt(subLines[i]);
            }
            catch(NumberFormatException e){
                break;
            }
            if(value!=null){
                this.indexList.add(value);
            }
        }
    }
}
