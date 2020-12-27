/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_rtp_SpeexUtil */

#ifndef _Included_com_rtp_SpeexUtil
#define _Included_com_rtp_SpeexUtil
#ifdef __cplusplus
extern "C" {
#endif
#undef com_rtp_SpeexUtil_DEFAULT_COMPRESSION
#define com_rtp_SpeexUtil_DEFAULT_COMPRESSION 4L
/*
 * Class:     com_rtp_SpeexUtil
 * Method:    open
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_rtp_SpeexUtil_open
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_rtp_SpeexUtil
 * Method:    getFrameSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rtp_SpeexUtil_getFrameSize
  (JNIEnv *, jobject);

/*
 * Class:     com_rtp_SpeexUtil
 * Method:    decode
 * Signature: ([B[SI)I
 */
JNIEXPORT jint JNICALL Java_com_rtp_SpeexUtil_decode
  (JNIEnv *, jobject, jbyteArray, jshortArray, jint);

/*
 * Class:     com_rtp_SpeexUtil
 * Method:    encode
 * Signature: ([SI[BI)I
 */
JNIEXPORT jint JNICALL Java_com_rtp_SpeexUtil_encode
  (JNIEnv *, jobject, jshortArray, jint, jbyteArray, jint);

/*
 * Class:     com_rtp_SpeexUtil
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_rtp_SpeexUtil_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif