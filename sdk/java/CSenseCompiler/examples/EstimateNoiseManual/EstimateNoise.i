%module EstimateNoiseMW
%include "matlab.i"

%typemap(in) (real_T [257]) {
$1 = (real_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (real_T [257]) "java.nio.DoubleBuffer"
%typemap(jstype) (real_T [257]) "java.nio.DoubleBuffer"
%typemap(jni) (real_T [257]) "jobject"
%typemap(javain) (real_T [257]) "$javainput"
%typemap(javaout) (real_T [257]) {
return $jnicall;
}

%typemap(in) (boolean_T [257]) {
$1 = (boolean_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);
}
%typemap(jtype) (boolean_T [257]) "java.nio.ByteBuffer"
%typemap(jstype) (boolean_T [257]) "java.nio.ByteBuffer"
%typemap(jni) (boolean_T [257]) "jobject"
%typemap(javain) (boolean_T [257]) "$javainput"
%typemap(javaout) (boolean_T [257]) {
return $jnicall;
}

%include "EstimateNoiseW_types.h"

%{
#include "EstimateNoiseW.h"
#include "EstimateNoiseW_terminate.h"
#include "EstimateNoiseW_initialize.h"
#include "EstimateNoiseW_types.h"
//nothings
%}

extern void EstimateNoiseW(const real_T yf[257], LocalState *local, real_T x[257]);
extern void EstimateNoiseW_initialize(void);
extern void EstimateNoiseW_terminate(void);
