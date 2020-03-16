package com.gangweedganggang.cs4240;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunAll {

    public static void main(String[] args) {

        File f = new File("test_program");
        File[] files = f.listFiles();
        if (files == null) {
            System.err.println("listfiles() returned null");
            System.exit(1);
        }

        Map<String, String> fails = new HashMap<>();

        for (File file : files) {
            if (!file.getName().endsWith(".tiger")) {
                continue;
            }

            System.out.println("Executing " + file.getName());
            Main.runResult = null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream oldOut = System.out;
            PrintStream oldErr = System.err;
            int code = -1;
            try {
                PrintStream ps = new PrintStream(baos);
                System.setOut(ps);
                System.setErr(ps);

                code = Main.internalMain(new String[]{"--run", file.getAbsolutePath()});
            } finally {
                System.setOut(oldOut);
                System.setErr(oldErr);
            }

            {
                String pathName = file.getAbsolutePath() + ".log";
                try (FileOutputStream fos = new FileOutputStream(pathName)) {
                    fos.write(baos.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String result = Main.runResult;
            if (code != 0) {
                fails.put(file.getName(), "failed to compile");
            } else if (result == null) {
                fails.put(file.getName(), "failed to execute");
            } else if (result.contains("[Breakpoint]")) {
                fails.put(file.getName(), "Assertion failed");
            } else if (result.contains("(parser) syntax error")) {
                fails.put(file.getName(), "spim syntax error");
            }
        }

        System.out.println("\n\n");
        System.out.println("Failed Test Cases:");

        int maxKeyLen = 0;
        for (Map.Entry<String, String> entry : fails.entrySet()) {
            if (entry.getKey().length() > maxKeyLen) {
                maxKeyLen = entry.getKey().length();
            }
        }

        for (Map.Entry<String, String> entry : fails.entrySet()) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < (maxKeyLen - entry.getKey().length()); i++) {
                s.append(" ");
            }
            System.out.println("  " + entry.getKey() + s.toString() + ": " + entry.getValue());
        }


    }

}
