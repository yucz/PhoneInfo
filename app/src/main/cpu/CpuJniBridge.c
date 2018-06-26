//
// Created by xiake on 18-6-26.
//
#include <jni.h>
#include "cpu-features.h"
JNIEXPORT jstring
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuStructure(JNIEnv *env, jobject thiz){
    AndroidCpuFamily family=android_getCpuFamily();
    switch (family){
        case ANDROID_CPU_FAMILY_UNKNOWN:
            return (*env)->NewStringUTF(env,"unknown");
            break;
        case ANDROID_CPU_FAMILY_ARM:
            return (*env)->NewStringUTF(env,"ARM");
            break;
        case ANDROID_CPU_FAMILY_X86:
            return (*env)->NewStringUTF(env,"X86");
            break;
        case ANDROID_CPU_FAMILY_MIPS:
            return (*env)->NewStringUTF(env,"MIPS");
            break;
        case ANDROID_CPU_FAMILY_ARM64:
            return (*env)->NewStringUTF(env,"ARM64");
            break;
        case ANDROID_CPU_FAMILY_X86_64:
            return (*env)->NewStringUTF(env,"X86_64");
            break;
        case ANDROID_CPU_FAMILY_MIPS64:
            return (*env)->NewStringUTF(env,"MIPS64");
            break;
        default:
            return (*env)->NewStringUTF(env,"MAX");
            break;
    }
}
JNIEXPORT jint
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuCount(JNIEnv *env, jobject thiz){
    return (jint)android_getCpuCount();
}
