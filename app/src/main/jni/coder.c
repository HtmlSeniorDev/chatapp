#include <jni.h>


JNIEXPORT jbyteArray JNICALL Java_ru_readme_chatapp_util_Coder_code(JNIEnv *env,
                                                                       jobject javaThis,
                                                                       jbyteArray in,
                                                                       jbyteArray ps) {

    int strlen = (*env)->GetArrayLength(env, in);
    int passlen = (*env)->GetArrayLength(env, ps);
    int i;

    jbyte *src = (*env)->GetByteArrayElements(env, in, JNI_FALSE);
    jbyte *pas = (*env)->GetByteArrayElements(env, ps, JNI_FALSE);
    (*env)->ReleaseByteArrayElements(env, in, src, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, ps,pas, JNI_ABORT);
    jbyte res[strlen];
    for (i = 0; i < strlen; i++) {
        res[i] = (jbyte) (src[i] ^ (pas[i % passlen] + 8));
    }
    jbyteArray data = (*env)->NewByteArray(env, strlen);
    (*env)->SetByteArrayRegion(env, data, 0, strlen, res);
   // (*env)->ReleaseByteArrayElements(data, src, 0, JNI_ABORT);

    return data;
}
