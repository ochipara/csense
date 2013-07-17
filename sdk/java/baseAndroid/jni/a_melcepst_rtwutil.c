/*
 * a_melcepst_rtwutil.c
 *
 * Code generation for function 'a_melcepst_rtwutil'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "a_melcepst_rtwutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
real_T rt_hypotd_snf(real_T u0, real_T u1)
{
  real_T y;
  real_T a;
  a = fabs(u0);
  y = fabs(u1);
  if (a < y) {
    a /= y;
    y *= sqrt(a * a + 1.0);
  } else if (a > y) {
    y /= a;
    y = a * sqrt(y * y + 1.0);
  } else if (rtIsNaN(y)) {
  } else {
    y = a * 1.4142135623730951;
  }

  return y;
}

/* End of code generation (a_melcepst_rtwutil.c) */
