typedef signed char int8_T;
typedef unsigned char uint8_T;
typedef short int16_T;
typedef unsigned short uint16_T;
typedef int int32_T;
typedef unsigned int uint32_T;
typedef float real32_T;
typedef double real64_T;

typedef double real_T;
typedef double time_T;
typedef unsigned char boolean_T;
typedef int int_T;
typedef unsigned int uint_T;
typedef unsigned long ulong_T;
typedef char char_T;
typedef char_T byte_T;

%typemap(in) (double [ANY]) {
$1 = (real_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (double [ANY]) "java.nio.DoubleBuffer"
%typemap(jstype) (double [ANY]) "java.nio.DoubleBuffer"
%typemap(jni) (double [ANY]) "jobject"
%typemap(javain) (double [ANY]) "$javainput"
%typemap(javaout) (double [ANY]) {
return $jnicall;
}

%typemap(in) (float [ANY]) {
$1 = (real_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (float [ANY]) "java.nio.FloatBuffer"
%typemap(jstype) (float [ANY]) "java.nio.FloatBuffer"
%typemap(jni) (float [ANY]) "jobject"
%typemap(javain) (float [ANY]) "$javainput"
%typemap(javaout) (float [ANY]) {
return $jnicall;
}

%typemap(in) (uint8_T [ANY]) {
$1 = (uint8_t*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (uint8_T [ANY]) "java.nio.ByteBuffer"
%typemap(jstype) (uint8_T [ANY]) "java.nio.ByteBuffer"
%typemap(jni) (uint8_T [ANY]) "jobject"
%typemap(javain) (uint8_T [ANY]) "$javainput"
%typemap(javaout) (uint8_T [ANY]) {
return $jnicall;
}

%typemap(in) (int16_T [ANY]) {
$1 = (int16_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (int16_T [ANY]) "java.nio.ShortBuffer"
%typemap(jstype) (int16_T [ANY]) "java.nio.ShortBuffer"
%typemap(jni) (int16_T [ANY]) "jobject"
%typemap(javain) (int16_T [ANY]) "$javainput"
%typemap(javaout) (int16_T [ANY]) {
return $jnicall;
}

%typemap(in) (int16_t [ANY]) {
$1 = (int16_t*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (int16_t [ANY]) "java.nio.ShortBuffer"
%typemap(jstype) (int16_t [ANY]) "java.nio.ShortBuffer"
%typemap(jni) (int16_t [ANY]) "jobject"
%typemap(javain) (int16_t [ANY]) "$javainput"
%typemap(javaout) (int16_t [ANY]) {
return $jnicall;
}

%{
#include "rtwtypes.h"
%}
