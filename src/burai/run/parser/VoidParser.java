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
 * modified from code developed by Satomichi Nishihara <nisihara.burai@gmail.com>
 * original code available from https://github.com/nisihara1/burai
 */

package burai.run.parser;

import java.io.File;
import java.io.IOException;

import burai.project.property.ProjectProperty;

public class VoidParser extends LogParser {

    public VoidParser(ProjectProperty property) {
        super(property);
    }

    @Override
    public void parse(File file, File inpfile) throws IOException {
        parse(file);//inpFile not used
    }
    @Override
    public void parse(File file) throws IOException {
        // NOP
    }

    @Override
    public void startParsing(File file) {
        // NOP
    }

    @Override
    public void endParsing() {
        // NOP
    }
}
