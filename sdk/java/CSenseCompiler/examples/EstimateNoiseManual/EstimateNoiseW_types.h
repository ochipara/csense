/*
 * EstimateNoiseW_types.h
 *
 * Code generation for function 'EstimateNoiseW'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

#ifndef __ESTIMATENOISEW_TYPES_H__
#define __ESTIMATENOISEW_TYPES_H__

/* Type Definitions */
typedef struct
{
    real_T p[257];
    real_T ac;
    real_T sn2[257];
    real_T pb[257];
    real_T pb2[257];
    real_T pminu[257];
    real_T actmin[257];
    real_T actminsub[257];
    real_T subwc;
    real_T actbuf[2056];
    real_T ibuf;
    boolean_T lminflag[257];
    real_T nrcum;
    real_T tinc;
} LocalState;
#ifndef struct_emxArray__common
#define struct_emxArray__common
typedef struct emxArray__common
{
    void *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray__common;
#endif
#ifndef struct_emxArray_int32_T_18
#define struct_emxArray_int32_T_18
typedef struct emxArray_int32_T_18
{
    int32_T data[18];
    int32_T size[1];
} emxArray_int32_T_18;
#endif
#ifndef struct_emxArray_int32_T_1x257
#define struct_emxArray_int32_T_1x257
typedef struct emxArray_int32_T_1x257
{
    int32_T data[257];
    int32_T size[2];
} emxArray_int32_T_1x257;
#endif
#ifndef struct_emxArray_int32_T_1x4
#define struct_emxArray_int32_T_1x4
typedef struct emxArray_int32_T_1x4
{
    int32_T data[4];
    int32_T size[2];
} emxArray_int32_T_1x4;
#endif
#ifndef struct_emxArray_int32_T_4
#define struct_emxArray_int32_T_4
typedef struct emxArray_int32_T_4
{
    int32_T data[4];
    int32_T size[1];
} emxArray_int32_T_4;
#endif
#ifndef struct_emxArray_real_T
#define struct_emxArray_real_T
typedef struct emxArray_real_T
{
    real_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
} emxArray_real_T;
#endif
typedef struct
{
    real_T taca;
    real_T tamax;
    real_T taminh;
    real_T tpfall;
    real_T tbmax;
    real_T qeqmin;
    real_T qeqmax;
    real_T av;
    real_T td;
    real_T nu;
    real_T qith[4];
    real_T nsmdb[4];
} struct_T;

#endif
/* End of code generation (EstimateNoiseW_types.h) */
