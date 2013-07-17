/*
 * sum.c
 *
 * Code generation for function 'sum'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "sum.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
real_T sum(const real_T x[257])
{
  real_T y;
  int32_T k;
  y = x[0];
  for (k = 0; k < 256; k++) {
    y += x[k + 1];
  }

  return y;
}

/* End of code generation (sum.c) */
