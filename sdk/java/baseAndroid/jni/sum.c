/*
 * sum.c
 *
 * Code generation for function 'sum'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "sum.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
real_T sum(const emxArray_real_T *x)
{
  real_T y;
  int32_T vlen;
  int32_T c_k;
  if (x->size[1] == 0) {
    y = 0.0;
  } else {
    vlen = x->size[1];
    y = x->data[0];
    for (c_k = 2; c_k <= vlen; c_k++) {
      y += x->data[c_k - 1];
    }
  }

  return y;
}

/* End of code generation (sum.c) */
