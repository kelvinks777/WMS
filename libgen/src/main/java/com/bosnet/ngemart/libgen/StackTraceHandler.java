package com.bosnet.ngemart.libgen;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by luis on 8/26/2016.
 * Purpose :
 */
public class StackTraceHandler {

    public static String getStacktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
