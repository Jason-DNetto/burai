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

/**
 *
 * @author Jason D'Netto <j.dnetto@qut.edu.au>
 * on behalf of the Manufacturing with advanced materials enabling platform, IFE, QUT
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */
public class ProjectPhFactory {
    private String path;
    private String prefix;
    private ProjectPh phonon;
    public ProjectPhFactory(){
        this.path=null;
        this.prefix=null;
        this.phonon=null;
    }
    protected void setPath(String path, String prefix) {
        this.path = path;
        this.prefix = prefix;
    }
    public ProjectPh getProjectPh(){
        if (this.path == null || this.path.isEmpty() || this.prefix == null || this.prefix.isEmpty()) {
            this.phonon = null;

        } else if (this.phonon != null && this.path.equals(this.phonon.getPath()) && this.prefix.equals(this.phonon.getPrefix())) {
            this.phonon.reload();

        } else {
            this.phonon = new ProjectPh(this.path, this.prefix);
        }

        return this.phonon;
    }
}
