/*
 * a_melcepst_emxutil.h
 *
 * Code generation for function 'a_melcepst_emxutil'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

#ifndef __A_MELCEPST_EMXUTIL_H__
#define __A_MELCEPST_EMXUTIL_H__
/* Include files */
#include <math.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "rt_defines.h"
#include "rt_nonfinite.h"

#include "rtwtypes.h"
#include "a_melcepst_types.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
extern void b_emxInit_creal_T(emxArray_creal_T **pEmxArray, int32_T numDimensions);
extern void b_emxInit_int32_T(emxArray_int32_T **pEmxArray, int32_T numDimensions);
extern void b_emxInit_real_T(emxArray_real_T **pEmxArray, int32_T numDimensions);
extern void c_emxInit_real_T(emxArray_real_T **pEmxArray, int32_T numDimensions);
extern void emxEnsureCapacity(emxArray__common *emxArray, int32_T oldNumel, int32_T elementSize);
extern void emxFree_boolean_T(emxArray_boolean_T **pEmxArray);
extern void emxFree_creal_T(emxArray_creal_T **pEmxArray);
extern void emxFree_int32_T(emxArray_int32_T **pEmxArray);
extern void emxFree_real_T(emxArray_real_T **pEmxArray);
extern void emxInit_boolean_T(emxArray_boolean_T **pEmxArray, int32_T numDimensions);
extern void emxInit_creal_T(emxArray_creal_T **pEmxArray, int32_T numDimensions);
extern void emxInit_int32_T(emxArray_int32_T **pEmxArray, int32_T numDimensions);
extern void emxInit_real_T(emxArray_real_T **pEmxArray, int32_T numDimensions);
#endif
/* End of code generation (a_melcepst_emxutil.h) */
