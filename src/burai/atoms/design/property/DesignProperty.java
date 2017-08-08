/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.design.property;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import burai.atoms.design.AtomDesign;
import burai.atoms.design.AtomsStyle;
import burai.atoms.design.Design;
import javafx.scene.paint.Color;

public class DesignProperty {

    private int atomsStyle;

    private double[] backColor;

    private double[] fontColor;

    private double[] cellColor;

    private boolean showingLegend;

    private boolean showingAxis;

    private boolean showingCell;

    private double bondWidth;

    private double cellWidth;

    private Map<String, AtomDesignProperty> atomProperties;

    public DesignProperty(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        String path_ = path.trim();
        if (path_.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        this.readFile(path_);
    }

    public DesignProperty(Design design) {
        if (design == null) {
            throw new IllegalArgumentException("design is null.");
        }

        this.atomsStyle = this.atmsStyleToId(design.getAtomsStyle());
        this.backColor = this.colorToDoubles(design.getBackColor());
        this.fontColor = this.colorToDoubles(design.getFontColor());
        this.cellColor = this.colorToDoubles(design.getCellColor());
        this.showingLegend = design.isShowingLegend();
        this.showingAxis = design.isShowingAxis();
        this.showingCell = design.isShowingCell();
        this.bondWidth = design.getBondWidth();
        this.cellWidth = design.getCellWidth();

        String[] names = design.namesOfAtoms();
        if (names != null && names.length > 0) {
            for (String name : names) {
                if (name == null) {
                    continue;
                }
                AtomDesign atomDesign = design.getAtomDesign(name);
                if (atomDesign != null) {
                    this.putAtomProperty(name, atomDesign);
                }
            }
        }
    }

    public void storeProperty(String path) throws IOException {
        if (path == null) {
            return;
        }

        String path_ = path.trim();
        if (path_.isEmpty()) {
            return;
        }

        this.writeFile(path_);
    }

    public void restoreProperty(Design design) {
        if (design == null) {
            return;
        }

        design.setAtomsStyle(this.idToAtomsStyle(this.atomsStyle));
        design.setBackColor(this.doublesToColor(this.backColor));
        design.setFontColor(this.doublesToColor(this.fontColor));
        design.setCellColor(this.doublesToColor(this.cellColor));
        design.setShowingLegend(this.showingLegend);
        design.setShowingAxis(this.showingAxis);
        design.setShowingCell(this.showingCell);
        design.setBondWidth(this.bondWidth);
        design.setCellWidth(this.cellWidth);

        if (this.atomProperties != null) {
            Set<String> names = this.atomProperties.keySet();
            if (names != null && !names.isEmpty()) {
                for (String name : names) {
                    if (name == null) {
                        continue;
                    }
                    AtomDesign atomDesign = design.getAtomDesign(name);
                    if (atomDesign != null) {
                        this.setAtomDesign(name, atomDesign);
                    }
                }
            }
        }
    }

    private int atmsStyleToId(AtomsStyle atomsStyle) {
        return atomsStyle == null ? AtomsStyle.BALL_STICK.getId() : atomsStyle.getId();
    }

    private AtomsStyle idToAtomsStyle(int id) {
        AtomsStyle atomsStyle = AtomsStyle.getInstance(id);
        return atomsStyle == null ? AtomsStyle.BALL_STICK : atomsStyle;
    }

    private double[] colorToDoubles(Color color) {
        if (color == null) {
            return new double[] { 0.0, 0.0, 0.0, 0.0 };
        }

        return new double[] {
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getOpacity()
        };
    }

    private Color doublesToColor(double[] values) {
        Color color = null;
        if (values != null) {
            if (values.length == 3) {
                color = Color.color(values[0], values[1], values[2]);
            } else if (values.length >= 4) {
                color = Color.color(values[0], values[1], values[2], values[3]);
            }
        }

        return color == null ? Color.TRANSPARENT : color;
    }

    private void putAtomProperty(String name, AtomDesign atomDesign) {
        if (name == null || atomDesign == null) {
            return;
        }

        if (this.atomProperties == null) {
            this.atomProperties = new HashMap<>();
        }

        AtomDesignProperty atomProperty = new AtomDesignProperty();
        atomProperty.setRadius(atomDesign.getRadius());
        atomProperty.setColor(this.colorToDoubles(atomDesign.getColor()));

        this.atomProperties.put(name, atomProperty);
    }

    private void setAtomDesign(String name, AtomDesign atomDesign) {
        if (name == null || atomDesign == null) {
            return;
        }

        if (this.atomProperties == null) {
            return;
        }

        AtomDesignProperty atomProperty = this.atomProperties.get(name);
        if (atomProperty == null) {
            return;
        }

        atomDesign.setRadius(atomProperty.getRadius());
        atomDesign.setColor(this.doublesToColor(atomProperty.getColor()));
    }

    private void readFile(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        Reader reader = null;
        DesignProperty other = null;

        try {
            File file = new File(path);
            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            other = gson.fromJson(reader, DesignProperty.class);

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        this.atomsStyle = other.atomsStyle;
        this.backColor = other.backColor;
        this.fontColor = other.fontColor;
        this.cellColor = other.cellColor;
        this.showingLegend = other.showingLegend;
        this.showingAxis = other.showingAxis;
        this.showingCell = other.showingCell;
        this.bondWidth = other.bondWidth;
        this.cellWidth = other.cellWidth;
        this.atomProperties = other.atomProperties;
    }

    private void writeFile(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        Writer writer = null;

        try {
            File file = new File(path);
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(this, writer);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }
}