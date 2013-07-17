/*
 * floor.c
 *
 * Code generation for function 'floor'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "floor.h"
#include "a_melcepst_emxutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void b_floor(emxArray_real_T *x)
{
  emxArray_real_T *b_x;
  int32_T i9;
  int32_T c_k;
  emxInit_real_T(&b_x, 2);
  i9 = x->size[1];
  for (c_k = 0; c_k <= i9 - 1; c_k++) {
    x->data[(int32_T)(1.0 + (real_T)c_k) - 1] = floor(x->data[(int32_T)(1.0 +
      (real_T)c_k) - 1]);
  }

  emxFree_real_T(&b_x);
}

/* End of code generation (floor.c) */
