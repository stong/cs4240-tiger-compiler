package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.ToolsIntegration;

import java.io.*;

class MIPS32ToolsIntegration implements ToolsIntegration {

    private void copyStream(OutputStream dst, InputStream src) {
        try {
            byte[] arr = new byte[1024];
            int length;

            while ((length = src.read(arr)) != -1) {
                dst.write(arr, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String executeAssemblyFile(File file) {
        try {
            Process p = new ProcessBuilder()
                    .command("spim", "-file", file.getAbsolutePath())
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .start();

            long startTime = System.currentTimeMillis();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();

            Thread copier = new Thread(() -> {
                synchronized (baos) {
                    copyStream(baos, p.getInputStream());
                }
            });
            copier.setDaemon(true);
            copier.start();

            Thread errCopier = new Thread(() -> {
                synchronized (errBaos) {
                    copyStream(errBaos, p.getErrorStream());
                }
            });
            errCopier.setDaemon(true);
            errCopier.start();

            while (System.currentTimeMillis() - startTime < 2000 && p.isAlive()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }

            if (p.isAlive()) {
                p.destroyForcibly();
            }

            while (true) {
                try {
                    copier.join();
                    errCopier.join();
                    break;
                } catch (InterruptedException ignored) {
                }
            }

            synchronized (baos) {
                synchronized (errBaos) {
                    errBaos.write(baos.toByteArray());
                    return errBaos.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
