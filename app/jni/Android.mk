LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := daemon
LOCAL_SRC_FILES := daemon.c
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
