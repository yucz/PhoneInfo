package com.luoye.phoneinfo.cpu;

/**
 * Created by zyw on 18-6-26.
 */

public class CpuBridge {

    public static native  String getCpuStructure();
    public static native  int getCpuCount();
    public static native  long getCpuFeatures();
    public static native  int getCpuId();
    public static final int ANDROID_CPU_ARM_FEATURE_ARMv7       = (1 << 0);
    public static final int ANDROID_CPU_ARM_FEATURE_VFPv3       = (1 << 1);
    public static final int ANDROID_CPU_ARM_FEATURE_NEON        = (1 << 2);
    public static final int ANDROID_CPU_ARM_FEATURE_LDREX_STREX = (1 << 3);
    public static final int ANDROID_CPU_ARM_FEATURE_VFPv2       = (1 << 4);
    public static final int ANDROID_CPU_ARM_FEATURE_VFP_D32     = (1 << 5);
    public static final int ANDROID_CPU_ARM_FEATURE_VFP_FP16    = (1 << 6);
    public static final int ANDROID_CPU_ARM_FEATURE_VFP_FMA     = (1 << 7);
    public static final int ANDROID_CPU_ARM_FEATURE_NEON_FMA    = (1 << 8);
    public static final int ANDROID_CPU_ARM_FEATURE_IDIV_ARM    = (1 << 9);
    public static final int ANDROID_CPU_ARM_FEATURE_IDIV_THUMB2 = (1 << 10);
    public static final int ANDROID_CPU_ARM_FEATURE_iWMMXt      = (1 << 11);
    public static final int ANDROID_CPU_ARM_FEATURE_AES         = (1 << 12);
    public static final int ANDROID_CPU_ARM_FEATURE_PMULL       = (1 << 13);
    public static final int ANDROID_CPU_ARM_FEATURE_SHA1        = (1 << 14);
    public static final int ANDROID_CPU_ARM_FEATURE_SHA2        = (1 << 15);
    public static final int ANDROID_CPU_ARM_FEATURE_CRC32       = (1 << 16);
    public static String getFeatures(){
        StringBuilder sb=new StringBuilder();
        long val=getCpuFeatures();
        if((val&ANDROID_CPU_ARM_FEATURE_ARMv7)==ANDROID_CPU_ARM_FEATURE_ARMv7){
            sb.append("ARMv7,");
        }

        if((val&ANDROID_CPU_ARM_FEATURE_VFPv3)==ANDROID_CPU_ARM_FEATURE_VFPv3){
            sb.append("VFPv3,");
        }

        if((val&ANDROID_CPU_ARM_FEATURE_NEON)==ANDROID_CPU_ARM_FEATURE_NEON){
            sb.append("NEON,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_LDREX_STREX)==ANDROID_CPU_ARM_FEATURE_LDREX_STREX){
            sb.append("LDREX_STREX,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_VFPv2)==ANDROID_CPU_ARM_FEATURE_VFPv2){
            sb.append("VFPv2,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_VFP_D32)==ANDROID_CPU_ARM_FEATURE_VFP_D32){
            sb.append("VFP_D32,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_VFP_FP16)==ANDROID_CPU_ARM_FEATURE_VFP_FP16){
            sb.append("VFP_FP16,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_VFP_FMA)==ANDROID_CPU_ARM_FEATURE_VFP_FMA){
            sb.append("VFP_FMA,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_NEON_FMA)==ANDROID_CPU_ARM_FEATURE_NEON_FMA){
            sb.append("NEON_FMA,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_IDIV_ARM)==ANDROID_CPU_ARM_FEATURE_IDIV_ARM){
            sb.append("IDIV_ARM,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_IDIV_THUMB2)==ANDROID_CPU_ARM_FEATURE_IDIV_THUMB2){
            sb.append("IDIV_THUMB2,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_iWMMXt)==ANDROID_CPU_ARM_FEATURE_iWMMXt){
            sb.append("iWMMXt,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_AES)==ANDROID_CPU_ARM_FEATURE_AES){
            sb.append("AES,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_PMULL)==ANDROID_CPU_ARM_FEATURE_PMULL){
            sb.append("PMULL,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_SHA1)==ANDROID_CPU_ARM_FEATURE_SHA1){
            sb.append("SHA1,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_SHA2)==ANDROID_CPU_ARM_FEATURE_SHA2){
            sb.append("SHA2,");
        }
        if((val&ANDROID_CPU_ARM_FEATURE_CRC32)==ANDROID_CPU_ARM_FEATURE_CRC32){
            sb.append("CRC32");
        }


        return sb.toString();
    }
    static {
        System.loadLibrary("cpu-info");
    }
}
