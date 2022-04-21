LOCAL_PATH := $(call my-dir)
define walk
  $(wildcard $(1)) $(foreach e, $(wildcard $(1)/*), $(call walk, $(e)))
endef

include $(CLEAR_VARS)
LOCAL_MODULE           := nativeRootCheck
FILE_LIST              := $(filter %.c %.cpp, $(call walk, $(LOCAL_PATH)/src))
LOCAL_SRC_FILES        := $(FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)