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

//typedef uint8_t         jboolean;       /* unsigned 8 bits */
//typedef int8_t          jbyte;          /* signed 8 bits */
//typedef uint16_t        jchar;          /* unsigned 16 bits */
//typedef int16_t         jshort;         /* signed 16 bits */
//typedef int32_t         jint;           /* signed 32 bits */
//typedef int64_t         jlong;          /* signed 64 bits */
//typedef float           jfloat;         /* 32-bit IEEE 754 */
//typedef double          jdouble;        /* 64-bit IEEE 754 */
//typedef unsigned char   jboolean;       /* unsigned 8 bits */
//typedef signed char     jbyte;          /* signed 8 bits */
//typedef unsigned short  jchar;          /* unsigned 16 bits */
//typedef short           jshort;         /* signed 16 bits */
//typedef int             jint;           /* signed 32 bits */
//typedef long long       jlong;          /* signed 64 bits */
//typedef float           jfloat;         /* 32-bit IEEE 754 */
//typedef double          jdouble;        /* 64-bit IEEE 754 */

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
JNIEXPORT jint
JNICALL
Java_com_luoye_phoneinfo_cpu_CpuBridge_getCpuFeatures(JNIEnv *env, jobject thiz){
    uint64_t  val=android_getCpuFeatures();
    int value=(int)val;
    return  value;

}