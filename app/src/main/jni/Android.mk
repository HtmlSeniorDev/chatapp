LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := coder
LOCAL_SRC_FILES := coder.c


include $(BUILD_SHARED_LIBRARY)