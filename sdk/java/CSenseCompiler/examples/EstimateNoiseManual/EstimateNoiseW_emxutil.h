/*
 * EstimateNoiseW_emxutil.h
 *
 * Code generation for function 'EstimateNoiseW_emxutil'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

#ifndef __ESTIMATENOISEW_EMXUTIL_H__
#define __ESTIMATENOISEW_EMXUTIL_H__
/* Include files */
#include <float.h>
#include <math.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "rt_nonfinite.h"

#include "rtwtypes.h"
#include "EstimateNoiseW_types.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
extern void emxEnsureCapacity(emxArray__common *emxArray, int32_T oldNumel, int32_T elementSize);
extern void emxFree_real_T(emxArray_real_T **pEmxArray);
extern void emxInit_real_T(emxArray_real_T **pEmxArray, int32_T numDimensions);
#endif
/* End of code generation (EstimateNoiseW_emxutil.h) */
