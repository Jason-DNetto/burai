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

package burai.run.parser;

import java.io.File;
import java.io.IOException;

import burai.project.property.ProjectProperty;

public abstract class LogParser {

    private static final long SLEEP_TIME = 5000L;

    private boolean parsing;

    private boolean ending;

    protected ProjectProperty property;

    public LogParser(ProjectProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property is null.");
        }

        this.parsing = false;
        this.ending = false;
        this.property = property;
    }

    public abstract void parse(File file) throws IOException;
    public abstract void parse(File file, File inpFile) throws IOException;

    public void startParsing(File file) {
        if (file == null) {
            return;
        }

        synchronized (this) {
            this.parsing = true;
            this.ending = false;
        }

        Thread thread = new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }
                }

                try {
                    this.parse(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }

                    try {
                        this.wait(SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                this.parse(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (this) {
                this.ending = false;
                this.notifyAll();
            }
        });

        thread.start();
    }
    
    public void startParsing(File file, File inpFile) {
        if (file == null) {
            //System.out.println("file is null");
            return;
        }/* else {
            //System.out.println("file is "+file.toString());
        }
        if (inpFile == null) {
            //System.out.println("inpFile is null");
        } else {
            //System.out.println("inpFile is "+inpFile.toString());
        }*/
        synchronized (this) {
            this.parsing = true;
            this.ending = false;
        }

        Thread thread = new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }
                }

                try {
                    this.parse(file, inpFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }

                    try {
                        this.wait(SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                this.parse(file, inpFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (this) {
                this.ending = false;
                this.notifyAll();
            }
        });

        thread.start();
    }

    public void endParsing() {
        synchronized (this) {
            if (!this.parsing) {
                return;
            }

            this.parsing = false;
            this.ending = true;
            this.notifyAll();
        }

        synchronized (this) {
            while (this.ending) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.parsing = false;
            this.ending = false;
        }
    }
}
