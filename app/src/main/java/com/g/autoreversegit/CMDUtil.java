package com.g.autoreversegit;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class CMDUtil {
    public static String TAG = CMDUtil.class.getSimpleName();

    public static void processCommand(String... cmd) {
        processCommand(0, cmd);
    }

    public static void processCommand(long sleep, String... cmds) {
        try {
            Command command = new Command(0, cmds);
            try {
                RootTools.getShell(true).add(command);
            } catch (IOException | RootDeniedException | TimeoutException ex) {
                ex.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (sleep != 0) {
            sleep(sleep);
        }
    }

    public static void endShell() {
        try {
            RootTools.getShell(true).close();
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }

    public static void sleeps(long seconds) {
        sleep(seconds * 1000);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (IllegalArgumentException | InterruptedException e) {
            System.out.println(TAG + e.toString());
            e.printStackTrace();
        }
    }
}
