/*
 * rdivide.h
 *
 * Code generation for function 'rdivide'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

#ifndef __RDIVIDE_H__
#define __RDIVIDE_H__
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
extern void b_rdivide(const real_T x[257], const real_T y[257], real_T z[257]);
extern void c_rdivide(real_T x, const real_T y[257], real_T z[257]);
extern real_T rdivide(real_T x, real_T y);
#endif
/* End of code generation (rdivide.h) */
