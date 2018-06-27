//
// Created by xiake on 18-6-26.
//
#include <jni.h>
#include "cpu-features.h"
#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

const char* TAG="cpu-info";


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
    }
}
JNIEXPORT jint
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuCount(JNIEnv *env, jobject thiz){
    return (jint)android_getCpuCount();
}

JNIEXPORT jint
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuFeatures(JNIEnv *env, jobject thiz){
    uint64_t  val=android_getCpuFeatures();
    int value=val;
    return  value;
}

JNIEXPORT jint
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuId(JNIEnv *env, jobject thiz){
#ifdef __arm__
    int cpuid=android_getCpuIdArm();
#else
    int cpuid=0;
#endif

    LOGD("%d",cpuid);
    return  cpuid;
}