/*
 * power.c
 *
 * Code generation for function 'power'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "power.h"
#include "EstimateNoiseW_rtwutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void b_power(const real_T a[257], real_T y[257])
{
  int32_T k;
  for (k = 0; k < 257; k++) {
    y[k] = rt_powd_snf(a[k], 2.0);
  }
}

void c_power(const real_T a[257], real_T y[257])
{
  int32_T k;
  for (k = 0; k < 257; k++) {
    y[k] = rt_powd_snf(a[k], -1.0);
  }
}

void power(const real_T b[4], real_T y[4])
{
  int32_T k;
  for (k = 0; k < 4; k++) {
    y[k] = rt_powd_snf(10.0, b[k]);
  }
}

/* End of code generation (power.c) */
