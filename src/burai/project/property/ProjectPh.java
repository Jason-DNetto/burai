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
package burai.project.property;

import java.io.File;

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 */
public class ProjectPh {
    private String path;
    private String prefix;
    private PhData PhDatas;
    
    public ProjectPh(String path, String prefix){
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix is empty.");
        }

        this.path = path;
        this.prefix = prefix;
        this.PhDatas = null;
        
        //call reload to initialise data structure
        this.reload();
    }
    
    public String getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }
    
    //function to return list of elements in data structure
    public PhData getPhdata(){return this.PhDatas;}
    
    //reload method
    public boolean reload(){
        boolean reloaded = false;
        try {
            File dirFile = new File(this.path);
            if (!dirFile.isDirectory()) {
                return false;
            }
            
            File file = new File(dirFile, this.prefix + ".phonon.gnu");
            if (this.PhDatas == null) {
                this.PhDatas = new PhData(file);
                reloaded = true;
            } else {
                if (this.PhDatas != null) {
                    reloaded = reloaded || this.PhDatas.reload();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return reloaded;
    }
}
