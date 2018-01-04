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

package burai.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import burai.com.file.FileTools;
import burai.input.QEInput;
import burai.project.Project;
import burai.run.parser.LogParser;
import burai.run.parser.PhParser;
import burai.run.parser.PhPlotbandParser;

public class RunningNode implements Runnable {

    private static final RunningType DEFAULT_TYPE = RunningType.SCF;

    private boolean alive;

    private Project project;

    private RunningStatus status;

    private List<RunningStatusChanged> onStatusChangedList;

    private RunningType type;

    private int numProcesses;

    private int numThreads;

    private Process objProcess;

    public RunningNode(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.alive = true;

        this.project = project;

        this.status = RunningStatus.IDLE;
        this.onStatusChangedList = null;

        this.type = null;
        this.numProcesses = 1;
        this.numThreads = 1;

        this.objProcess = null;
    }

    public Project getProject() {
        return this.project;
    }

    public synchronized RunningStatus getStatus() {
        return this.status;
    }

    protected synchronized void setStatus(RunningStatus status) {
        if (status == null) {
            return;
        }

        this.status = status;

        if (this.onStatusChangedList != null) {
            for (RunningStatusChanged onStatusChanged : this.onStatusChangedList) {
                if (onStatusChanged != null) {
                    onStatusChanged.onRunningStatusChanged(this.status);
                }
            }
        }
    }

    public synchronized void addOnStatusChanged(RunningStatusChanged onStatusChanged) {
        if (onStatusChanged != null) {
            if (this.onStatusChangedList == null) {
                this.onStatusChangedList = new ArrayList<RunningStatusChanged>();
            }

            this.onStatusChangedList.add(onStatusChanged);
        }
    }

    public synchronized void removeOnStatusChanged(RunningStatusChanged onStatusChanged) {
        if (onStatusChanged != null) {
            if (this.onStatusChangedList != null) {
                this.onStatusChangedList.remove(onStatusChanged);
            }
        }
    }

    public synchronized RunningType getType() {
        return this.type;
    }

    public synchronized void setType(RunningType type) {
        this.type = type;
    }

    public synchronized int getNumProcesses() {
        return this.numProcesses;
    }

    public synchronized void setNumProcesses(int numProcesses) {
        this.numProcesses = numProcesses;
    }

    public synchronized int getNumThreads() {
        return this.numThreads;
    }

    public synchronized void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public synchronized void stop() {
        this.alive = false;

        if (this.objProcess != null) {
            this.objProcess.destroy();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!this.alive) {
                return;
            }
        }

        File directory = this.getDirectory();
        if (directory == null) {
            return;
        }

        RunningType type2 = null;
        int numProcesses2 = -1;
        int numThreads2 = -1;

        synchronized (this) {
            type2 = this.type;
            numProcesses2 = this.numProcesses;
            numThreads2 = this.numThreads;
        }

        if (type2 == null) {
            type2 = DEFAULT_TYPE;
        }
        if (numProcesses2 < 1) {
            numProcesses2 = 1;
        }
        if (numThreads2 < 1) {
            numThreads2 = 1;
        }

        QEInput input = new FXQEInputFactory(type2).getQEInput(this.project);
        if (input == null) {
            return;
        }

        String inpName = this.project.getInpFileName();
        inpName = inpName == null ? null : inpName.trim();
        File inpFile = (inpName == null || inpName.isEmpty()) ? null : new File(directory, inpName);
        if (inpFile == null) {
            return;
        }
        //DEBUG
        ////System.out.println("input file name is "+inpName);
        
        List<String[]> commandList = type2.getCommandList(inpName, numProcesses2);
        if (commandList == null || commandList.isEmpty()) {
            return;
        }
        //DEBUG
        for(int loopcount=0;loopcount<commandList.size();loopcount++){
            for(int loopcount2=0;loopcount2<commandList.get(loopcount).length;loopcount2++){
                //System.out.print(commandList.get(loopcount)[loopcount2]+" ");
            }
            //System.out.println();
        }
        //end DEBUG
        List<RunningCondition> conditionList = type2.getConditionList();
        if (conditionList == null || conditionList.size() < commandList.size()) {
            //debug
            if (conditionList == null) {
                //System.out.println("condition list is null");//DEBUG
            } else {
                //System.out.println("condition list size id "+Integer.toString(conditionList.size())+" while command list size is "+Integer.toString(commandList.size()));//DEBUG
            }
            //end debug
            return;
        }

        List<InputEditor> inputEditorList = type2.getInputEditorList(this.project);
        if (inputEditorList == null || inputEditorList.size() < commandList.size()) {
            //debug
            if (conditionList == null) {
                //System.out.println("input editor list is null");//DEBUG
            } else {
                //System.out.println("input editor list size id "+Integer.toString(inputEditorList.size())+" while command list size is "+Integer.toString(commandList.size()));//DEBUG
            }
            //end debug
            return;
        }

        List<LogParser> parserList = type2.getParserList(this.project);
        if (parserList == null || parserList.size() < commandList.size()) {
            //debug
            if (conditionList == null) {
                //System.out.println("parser list is null");//DEBUG
            } else {
                //System.out.println("parser list size id "+Integer.toString(parserList.size())+" while command list size is "+Integer.toString(commandList.size()));//DEBUG
            }
            //end debug
            return;
        }

        List<PostOperation> postList = type2.getPostList();
        if (postList == null || postList.size() < commandList.size()) {
            //debug
            if (conditionList == null) {
                //System.out.println("post list is null");//DEBUG
            } else {
                //System.out.println("post list size id "+Integer.toString(postList.size())+" while command list size is "+Integer.toString(commandList.size()));//DEBUG
            }
            //end debug
            return;
        }

        this.deleteLogFiles(directory);

        int iCommand = 0;
        boolean errOccurred = false;

        for (int i = 0; i < commandList.size(); i++) {
            synchronized (this) {
                if (!this.alive) {
                    //System.out.println("this is not alive");
                    return;
                }
            }

            String[] command = commandList.get(i);
            if (command == null || command.length < 1) {
                //System.out.println("no commands");
                continue;
            }

            RunningCondition condition = conditionList.get(i);
            if (condition == null) {
                //System.out.println("no conditions");
                continue;
            }

            InputEditor inputEditor = inputEditorList.get(i);
            if (inputEditor == null) {
                //System.out.println("no input editor lists");
                continue;
            }

            LogParser parser = parserList.get(i);
            if (parser == null) {
                //System.out.println("no parsers");
                continue;
            }

            PostOperation post = postList.get(i);
            if (post == null) {
                //System.out.println("no post");
                continue;
            }

            QEInput input2 = inputEditor.editInput(input);
            if (input2 == null) {
                //System.out.println("no input editors");
                continue;
            }
            //DEBUG
            //System.out.print("running ");
            for(int loopcount2=0;loopcount2<commandList.get(i).length;loopcount2++){
                //System.out.print(commandList.get(i)[loopcount2]+" ");
            }
            //System.out.println();
            //System.out.println(input2.toString());
            
            if (!condition.toRun(this.project, input2)) {
                //System.out.println("condition to run is false");
                continue;
            }

            boolean inpStatus = this.writeQEInput(input2, inpFile);
            if (!inpStatus) {
                //System.out.println("input status is false");
                continue;
            }

            String logName = this.project.getLogFileName(iCommand);
            logName = logName == null ? null : logName.trim();
            File logFile = (logName == null || logName.isEmpty()) ? null : new File(directory, logName);
            if (logFile == null) {
                //System.out.println("no logfile");
                continue;
            }

            String errName = this.project.getErrFileName(iCommand);
            errName = errName == null ? null : errName.trim();
            File errFile = (errName == null || errName.isEmpty()) ? null : new File(directory, errName);
            if (errFile == null) {
                //System.out.println("no errfile");
                continue;
            }

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(directory);
            //debug
            //System.out.println("command.length = "+Integer.toString(command.length));
            for (int loopcount=0;loopcount<command.length;loopcount++) {
                //System.out.println("command["+Integer.toString(loopcount)+"] = "+command[loopcount]);
            }
            //end debug
            builder.command(command);
            if (!command[0].contains("plotband.")) {
                builder.environment().put("OMP_NUM_THREADS", Integer.toString(numThreads2));
            } else {
                builder.redirectInput(inpFile);
            }
            builder.redirectOutput(logFile);
            builder.redirectError(errFile);

            try {
                synchronized (this) {
                    this.objProcess = builder.start();
                }

                if (!(parser instanceof PhPlotbandParser)){
                    parser.startParsing(logFile,inpFile);
                } else {//if PhParser, take k-point specification from either espresso.matdyn.in or espresso.band.in
                    parser.startParsing(logFile, new File(this.getDirectory(),"espresso.band.in"));
                }
                
                if (this.objProcess != null) {
                    if (this.objProcess.waitFor() != 0) {
                        errOccurred = true;
                        //System.out.println("parsing error occurred");
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                errOccurred = true;
                break;

            } finally {
                synchronized (this) {
                    this.objProcess = null;
                }

                parser.endParsing();
            }

            if (!errOccurred) {
                post.operate(this.project);
            }

            iCommand++;
        }
        //System.out.println("reached end of process loop");
        if (!errOccurred) {
            type2.setProjectStatus(this.project);
        }
    }

    private File getDirectory() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null) {
            return null;
        }

        File dirFile = new File(dirPath);
        try {
            if (!dirFile.isDirectory()) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dirFile;
    }

    private boolean writeQEInput(QEInput input, File file) {
        if (input == null) {
            return false;
        }

        if (file == null) {
            return false;
        }

        String strInput = input.toString();
        if (strInput == null) {
            return false;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(strInput);

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return true;
    }

    private void deleteLogFiles(File directory) {
        if (directory == null) {
            return;
        }

        final int maxIndex = 9;

        for (int i = 0; true; i++) {
            String logName = this.project.getLogFileName(i);
            logName = logName == null ? null : logName.trim();
            if (logName == null || logName.isEmpty()) {
                continue;
            }

            boolean status = false;
            try {
                File logFile = new File(directory, logName);
                if (logFile.exists()) {
                    status = FileTools.deleteAllFiles(logFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!status) && (i > maxIndex)) {
                break;
            }
        }

        for (int i = 0; true; i++) {
            String errName = this.project.getErrFileName(i);
            errName = errName == null ? null : errName.trim();
            if (errName == null || errName.isEmpty()) {
                continue;
            }

            boolean status = false;
            try {
                File errFile = new File(directory, errName);
                if (errFile.exists()) {
                    status = FileTools.deleteAllFiles(errFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!status) && (i > maxIndex)) {
                break;
            }
        }

        String exitName = this.project.getExitFileName();
        exitName = exitName == null ? null : exitName.trim();
        if (exitName != null && (!exitName.isEmpty())) {
            try {
                File exitFile = new File(directory, exitName);
                if (exitFile.exists()) {
                    FileTools.deleteAllFiles(exitFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
