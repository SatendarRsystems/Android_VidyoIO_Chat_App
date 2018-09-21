package com.vidyo.io.demo.logger;
/**
 * Summary: To show the Logs
 * Description: By using this Logger, Logs will be shown on Logcat
 * @author RSI
 * @date 15.09.2018
 */
public class Logger {
    private static Logger mInstance = new Logger();

    public static Logger getInstance() {
        return mInstance;
    }

    private Logger() {
    }

    public void Log(String msg) {
        System.out.println("Vidyo Connector App: " + msg);
    }
}
