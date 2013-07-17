/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the COPYING file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

#include "config.h" /* needed for _GNU_SOURCE on Linux */

#include <jni.h>
#include <stdio.h>
#include <errno.h>
#include <sys/time.h>

/* For getrusage().  Note that on Linux _GNU_SOURCE needs to be defined
 * explicitly before including sys/resource.h otherwise RUSAGE_THREAD won't
 * be available.  The configure script should place _GNU_SOURCE in config.h
 * which is included above.  */
#include <sys/resource.h>
#include <unistd.h>

#include <android/log.h>
#define LOGV(TAG,...) __android_log_print(ANDROID_LOG_VERBOSE, TAG,__VA_ARGS__)
#define LOGD(TAG,...) __android_log_print(ANDROID_LOG_DEBUG  , TAG,__VA_ARGS__)
#define LOGI(TAG,...) __android_log_print(ANDROID_LOG_INFO   , TAG,__VA_ARGS__)
#define LOGW(TAG,...) __android_log_print(ANDROID_LOG_WARN   , TAG,__VA_ARGS__)
#define LOGE(TAG,...) __android_log_print(ANDROID_LOG_ERROR  , TAG,__VA_ARGS__)

#include "net_appjax_jni_ThreadCPUUsage.h"

/* Android headers don't define RUSAGE_THREAD */
#ifndef RUSAGE_THREAD
#define RUSAGE_THREAD 1
#endif

static struct rusage delay(int count) {
  int i = 0;
  struct rusage usage;
  for(i = 0; i < count; i++)
      getrusage(RUSAGE_THREAD, &usage);
  return usage;
}

static int log_clock_gettime(clockid_t id) {
  char *clkid;
  switch(id) {
      case CLOCK_REALTIME:
          clkid = "REALTIME";break;
      case CLOCK_MONOTONIC:
          clkid = "MONOTONIC";break;
      case CLOCK_PROCESS_CPUTIME_ID:
          clkid = "PROCESS";break;
      case CLOCK_THREAD_CPUTIME_ID:
          clkid = "THREAD";break;
  }
  struct timespec spec;
  int ret = clock_gettime(id, &spec);
  LOGD("clock_gettime()", "[%s] sec: %d, nsec: %d\n", clkid, spec.tv_sec, spec.tv_nsec);
  if(ret) LOGD("clock_gettime()", "failed by returning %d\n", ret);
  return ret;
}

static int log_getrusage() {
  struct rusage usage;
  int ret = getrusage(RUSAGE_THREAD, &usage);
  LOGD("getrusage()", "utime: %dus, stime: %dus\n", usage.ru_utime.tv_sec*1000000+usage.ru_utime.tv_usec,
                                                    usage.ru_stime.tv_sec*1000000+usage.ru_stime.tv_usec);
  if(ret) LOGD("getrusage()", "failed by returning %d\n", ret);
  return ret;
}

static void test_clock_gettime(clockid_t id, int count) {
  int i;
  LOGD("clock_gettime()", "===== Beginning of TEST with delay count %d =====\n", count);
  for(i = 0; i < 8; i++) {
    log_clock_gettime(id);
    delay(count);
  }
  LOGD("clock_gettime()", "===== End of TEST =====\n");
}

static void test_clock_gettime_usleep(clockid_t id, useconds_t us) {
  int i;
  LOGD("clock_gettime()", "===== Beginning of TEST with usleep(%dus) =====\n", us);
  for(i = 0; i < 8; i++) {
    log_clock_gettime(id);
    usleep(us);
  }
  LOGD("clock_gettime()", "===== End of TEST =====\n");
}

static void test_getrusage(int count) {
  int i;
  LOGD("getrusage()", "===== Beginning of TEST with delay count %d =====\n", count);
  for(i = 0; i < 8; i++) {
    log_getrusage();
    delay(count);
  }
  LOGD("getrusage()", "===== End of TEST =====\n");
}

static void test_getrusage_usleep(useconds_t us) {
  int i;
  LOGD("getrusage()", "===== Beginning of TEST with usleep(%dus) =====\n", us);
  for(i = 0; i < 8; i++) {
    log_getrusage();
    usleep(us);
  }
  LOGD("getrusage()", "===== End of TEST =====\n");
}

JNIEXPORT jobject JNICALL Java_compatibility_ThreadCPUUsage_getusage(JNIEnv *env, jclass cl, jobject cpuUsage) {
  jfieldID secondsID = (*env)->GetFieldID(env, cl, "seconds", "J");
  jfieldID nanosecondsID = (*env)->GetFieldID(env, cl, "nanoseconds", "J");
  jfieldID processSecondsID = (*env)->GetFieldID(env, cl, "processSeconds", "J");
  jfieldID processNanosecondsID = (*env)->GetFieldID(env, cl, "processNanoseconds", "J");
  jfieldID threadSecondsID = (*env)->GetFieldID(env, cl, "threadSeconds", "J");
  jfieldID threadNanosecondsID = (*env)->GetFieldID(env, cl, "threadNanoseconds", "J");
  jfieldID threadUserSecondsID = (*env)->GetFieldID(env, cl, "threadUserSeconds", "J");
  jfieldID threadUserMicrosecondsID = (*env)->GetFieldID(env, cl, "threadUserMicroseconds", "J");
  jfieldID threadSystemSecondsID = (*env)->GetFieldID(env, cl, "threadSystemSeconds", "J");
  jfieldID threadSystemMicrosecondsID = (*env)->GetFieldID(env, cl, "threadSystemMicroseconds", "J");
  
#if defined(RUSAGE_THREAD)
  int who = RUSAGE_THREAD;
#elif defined(RUSAGE_LWP)
  int who = RUSAGE_LWP;
#else
  #error Neither RUSAGE_THREAD nor RUSAGE_LWP available.  Only Linux (>2.6.26) and Solaris are known to support getrusage() on a per-thread basis.
#endif

  struct rusage usage;
  struct timespec spec;

  if(clock_gettime(CLOCK_REALTIME, &spec))
	  LOGD("clock_gettime(CLOCK_REALTIME)", "failed with errno %d\n", errno);
  else {
	  (*env)->SetLongField(env, cpuUsage, secondsID, spec.tv_sec);
	  (*env)->SetLongField(env, cpuUsage, nanosecondsID, spec.tv_nsec);
  }

  if(clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &spec))
	  LOGD("clock_gettime(CLOCK_PROCESS_CPUTIME_ID)", "failed with errno %d\n", errno);
  else {
	  (*env)->SetLongField(env, cpuUsage, processSecondsID, spec.tv_sec);
	  (*env)->SetLongField(env, cpuUsage, processNanosecondsID, spec.tv_nsec);
  }

  if(clock_gettime(CLOCK_THREAD_CPUTIME_ID, &spec))
	  LOGD("clock_gettime(CLOCK_THREAD_CPUTIME_ID)", "failed with errno %d\n", errno);
  else {
	  (*env)->SetLongField(env, cpuUsage, threadSecondsID, spec.tv_sec);
	  (*env)->SetLongField(env, cpuUsage, threadNanosecondsID, spec.tv_nsec);
  }

  if(getrusage(who, &usage))
	  LOGD("getrusage()", "failed with errno %d\n", errno);
  else {
	  (*env)->SetLongField(env, cpuUsage, threadUserSecondsID, usage.ru_utime.tv_sec);
	  (*env)->SetLongField(env, cpuUsage, threadUserMicrosecondsID, usage.ru_utime.tv_usec);
	  (*env)->SetLongField(env, cpuUsage, threadSystemSecondsID, usage.ru_stime.tv_sec);
	  (*env)->SetLongField(env, cpuUsage, threadSystemMicrosecondsID, usage.ru_stime.tv_usec);
  }

//  static int tested = 1;
//  if(!tested) {
//      test_getrusage(1000);
//      test_getrusage(10000);
//      test_getrusage(100000);
//      test_getrusage_usleep(10000);
//      test_getrusage_usleep(100000);
//      test_getrusage_usleep(1000000);
//      test_clock_gettime(CLOCK_THREAD_CPUTIME_ID, 1000);
//      test_clock_gettime(CLOCK_THREAD_CPUTIME_ID, 10000);
//      test_clock_gettime(CLOCK_THREAD_CPUTIME_ID, 100000);
//      test_clock_gettime_usleep(CLOCK_THREAD_CPUTIME_ID, 10000);
//      test_clock_gettime_usleep(CLOCK_THREAD_CPUTIME_ID, 100000);
//      test_clock_gettime_usleep(CLOCK_THREAD_CPUTIME_ID, 1000000);
//      tested = 1;
//  }

  return cpuUsage;
}
