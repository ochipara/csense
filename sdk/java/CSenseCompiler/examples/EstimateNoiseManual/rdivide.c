/*
 * rdivide.c
 *
 * Code generation for function 'rdivide'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "rdivide.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void b_rdivide(const real_T x[257], const real_T y[257], real_T z[257])
{
  int32_T i0;
  for (i0 = 0; i0 < 257; i0++) {
    z[i0] = x[i0] / y[i0];
  }
}

void c_rdivide(real_T x, const real_T y[257], real_T z[257])
{
  int32_T i1;
  for (i1 = 0; i1 < 257; i1++) {
    z[i1] = x / y[i1];
  }
}

real_T rdivide(real_T x, real_T y)
{
  return x / y;
}

/* End of code generation (rdivide.c) */
