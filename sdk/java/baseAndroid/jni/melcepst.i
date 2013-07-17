/*
 * a_melcepst.h
 *
 * Code generation for function 'a_melcepst'
 *
 * C source code generated on: Tue Mar  6 15:50:27 2012
 *
 */

%module a_melcepst
%include "matlab.i"

%typemap(in) (const real_T s[512]) { 
    $1 = (real_T*)(*jenv)->GetDirectBufferAddress(jenv, $input);  
    //$1 = jenv->GetDirectBufferAddress($input); 
    //$2 = (long)(jenv->GetDirectBufferCapacity($input)); 
} 

%typemap(jni) (const real_T s[512]) "jobject" 
%typemap(jtype) (const real_T s[512]) "java.nio.ByteBuffer" 
%typemap(jstype) (const real_T s[512]) "java.nio.ByteBuffer" 
%typemap(javain) (const real_T s[512]) "$javainput" 
%typemap(javaout) (const real_T s[512]) { 
    return $jnicall; 
}

%typemap(in) (real_T r[32]) { 
    $1 = (real_T*)(*jenv)->GetDirectBufferAddress(jenv, $input); 
} 

%typemap(jni) (real_T r[32]) "jobject" 
%typemap(jtype) (real_T r[32]) "java.nio.ByteBuffer" 
%typemap(jstype) (real_T r[32]) "java.nio.ByteBuffer" 
%typemap(javain) (real_T r[32]) "$javainput" 
%typemap(javaout) (real_T r[32]) { 
    return $jnicall; 
}

%include "carrays.i"
%array_functions(int, intArray);
%array_functions(double, doubleArray);
/*
%include "arrays_java.i"
//%include "cpointer.i"
//%pointer_class(int,intp)
*/
%{

#include "a_melcepst_types.h"
#include "rtwtypes.h"
#include "a_melcepst_initialize.h"
#include "a_melcepst_terminate.h"

void a_melcepst_wrap(const real_T s[512], int32_T offset1, 
                     real_T fs, int32_T nc, emxArray_real_T *c, 
                     real_T r[32], int32_T offset2);
//void a_melcepst(const real_T s[512], real_T fs, int32_T nc, emxArray_real_T *c);
extern void a_melcepst_initialize(void);
extern void a_melcepst_terminate(void);
//
//
%}

typedef struct emxArray__common
{
    void *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray__common;
typedef struct emxArray_boolean_T
{
    boolean_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray_boolean_T;
typedef struct emxArray_creal_T
{
    creal_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray_creal_T;
typedef struct emxArray_int32_T
{
    int32_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray_int32_T;
typedef struct emxArray_real_T
{
    real_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray_real_T;

// basic definitiosn from rtwtypes.h
typedef double real_T;
typedef double time_T;
typedef unsigned char boolean_T;
typedef int int_T;
typedef unsigned uint_T;
typedef unsigned long ulong_T;
typedef char char_T;
typedef char_T byte_T;
typedef int int32_T;

extern void a_melcepst_wrap(const real_T s[512], int32_T offset1, 
                            real_T fs, int32_T nc, emxArray_real_T *c, 
                            real_T r[32], int32_T offset2);
//extern void a_melcepst(const real_T s[512], real_T fs, int32_T nc, emxArray_real_T *c);
extern void a_melcepst_initialize(void);
extern void a_melcepst_terminate(void);
extern emxArray_real_T *emxCreate_real_T(int32_T rows, int32_T cols);
