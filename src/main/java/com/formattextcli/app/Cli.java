package com.formattextcli.app;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cli {
    // TODO: read this from config file
    private String _version = "1.0.0";

    public Cli() { }

    public void start(String[] _args) {
        List<String> args = Arrays.asList(_args);

        if (args.isEmpty()) {
            this._printBanner();
            return;
        }

        while (!args.isEmpty()) {
            String firstCommand = args.get(0).toLowerCase();

            switch (firstCommand) {
                case "-v":
                case "-version": {
                    this._handleVersionCommand();
                    return;
                }
                default: {
                    this._handleDefaultCommand(firstCommand);
                    return;
                }
            }
        }
    }

    private void _handleVersionCommand() {
        System.out.println("Version: " + this._version);
    }

    private void _handleDefaultCommand(String filePath) {
        try {
            FileHelper fileHelper = new FileHelper(filePath);
            if (!fileHelper.exists()) {
                System.err.println("It seems that it is an incorrect file path");
                return;
            }

            ArrayList<Line> formattedLines = new ArrayList();
            BufferedReader reader = fileHelper.read();
            String lineFromFile = "";

            while (lineFromFile != null) {
                lineFromFile = reader.readLine();
                if (lineFromFile != null) {
                    // get rid of all the noise in the string
                    lineFromFile = lineFromFile.replaceAll("( +)", " ").trim();
                    this._formatLines(formattedLines, lineFromFile);
                }
            }

            formattedLines.forEach(l -> l.printLine());

        } catch (Exception e) {
            System.err.println("Something went wrong!");
            System.err.println(e);
        }
    }

    private void _formatLines(ArrayList<Line> formattedLines, String lineFromFile) {
        if (lineFromFile.isEmpty() && formattedLines.size() > 0) {
            int currentLineIndex = formattedLines.size()-1;
            // previous line has no length, then it's most likely an empty line -> skip it
            Line currentLine = formattedLines.get(currentLineIndex);
            if (currentLine.lineText.length() == 0) {
                return;
            }

            // otherwise this is a new empty line, which serves as a divider
            Line newLine = new Line(true);
            newLine.addText("");
            formattedLines.add(newLine);
            return;
        }

        if (formattedLines.size() == 0) {
            formattedLines.add(new Line());
        }

        Arrays.stream(lineFromFile.split(" ")).forEach(s -> {
            int lineIndex = formattedLines.size()-1;
            Line currentLine = formattedLines.get(lineIndex);
            if (currentLine.canAddLine(s)) {
                currentLine.addText(s);
                formattedLines.set(lineIndex, currentLine);
                return;
            }

            Line newLine = new Line();
            newLine.addText(s);
            formattedLines.add(newLine);
        });
    }

    private void _printBanner() {
        String banner = "\n" +
                "\n" +
                " ______                         _     _______        _      _____ _ _ \n" +
                "|  ____|                       | |   |__   __|      | |    / ____| (_)\n" +
                "| |__ ___  _ __ _ __ ___   __ _| |_     | | _____  _| |_  | |    | |_ \n" +
                "|  __/ _ \\| '__| '_ ` _ \\ / _` | __|    | |/ _ \\ \\/ / __| | |    | | |\n" +
                "| | | (_) | |  | | | | | | (_| | |_     | |  __/>  <| |_  | |____| | |\n" +
                "|_|  \\___/|_|  |_| |_| |_|\\__,_|\\__|    |_|\\___/_/\\_\\\\__|  \\_____|_|_|\n" +
                "                                                                      \n" +
                "                                                                      \n";

        System.out.println(banner);
    }
}