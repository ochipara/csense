/*
 * abs.c
 *
 * Code generation for function 'abs'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "abs.h"
#include "a_melcepst_emxutil.h"
#include "a_melcepst_rtwutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void b_abs(const emxArray_creal_T *x, emxArray_real_T *y)
{
  uint32_T unnamed_idx_0;
  int32_T c_k;
  unnamed_idx_0 = (uint32_T)x->size[0];
  c_k = y->size[0];
  y->size[0] = (int32_T)unnamed_idx_0;
  emxEnsureCapacity((emxArray__common *)y, c_k, (int32_T)sizeof(real_T));
  for (c_k = 0; c_k <= x->size[0] - 1; c_k++) {
    y->data[c_k] = rt_hypotd_snf(fabs(x->data[c_k].re), fabs(x->data[c_k].im));
  }
}

/* End of code generation (abs.c) */
