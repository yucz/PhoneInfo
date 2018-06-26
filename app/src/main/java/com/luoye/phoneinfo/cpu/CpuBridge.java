package com.luoye.phoneinfo.cpu;

/**
 * Created by xiake on 18-6-26.
 */

public class CpuBridge {

    public static native  String getCpuStructure();
    public static native  int getCpuCount();
    static {
        System.loadLibrary("cpu-info");
    }
}
