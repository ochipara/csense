LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := melcepst
LOCAL_SRC_FILES = a_melbankm.c a_melcepst.c a_melcepst_data.c a_melcepst_emxAPI.c a_melcepst_emxutil.c a_melcepst_initialize.c a_melcepst_rtwutil.c a_melcepst_terminate.c a_rdct.c a_rfft.c abs.c fft.c floor.c frq2mel.c mel2frq.c rtGetInf.c rtGetNaN.c rt_nonfinite.c sqrt.c sum.c a_melcepst_wrap.c melcepst_wrap.c 
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := getrusage
LOCAL_SRC_FILES = getrusage/libThreadCPUUsage.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

