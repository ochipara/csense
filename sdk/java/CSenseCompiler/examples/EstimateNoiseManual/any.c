/*
 * any.c
 *
 * Code generation for function 'any'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "any.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
boolean_T any(const boolean_T x[257])
{
  boolean_T y;
  int32_T k;
  boolean_T exitg1;
  boolean_T b0;
  y = FALSE;
  k = 0;
  exitg1 = FALSE;
  while ((exitg1 == 0U) && (k < 257)) {
    if ((int32_T)x[k] == 0) {
      b0 = TRUE;
    } else {
      b0 = FALSE;
    }

    if (!b0) {
      y = TRUE;
      exitg1 = TRUE;
    } else {
      k++;
    }
  }

  return y;
}

/* End of code generation (any.c) */
