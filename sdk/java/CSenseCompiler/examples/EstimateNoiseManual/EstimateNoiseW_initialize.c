/*
 * EstimateNoiseW_initialize.c
 *
 * Code generation for function 'EstimateNoiseW_initialize'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "EstimateNoiseW_initialize.h"
#include "EstimateNoiseW_data.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void EstimateNoiseW_initialize(void)
{
  static const real_T dv0[54] = { 1.0, 2.0, 5.0, 8.0, 10.0, 15.0, 20.0, 30.0,
    40.0, 60.0, 80.0, 120.0, 140.0, 160.0, 180.0, 220.0, 260.0, 300.0, 0.0, 0.26,
    0.48, 0.58, 0.61, 0.668, 0.705, 0.762, 0.8, 0.841, 0.865, 0.89, 0.9, 0.91,
    0.92, 0.93, 0.935, 0.94, 0.0, 0.15, 0.48, 0.78, 0.98, 1.55, 2.0, 2.3, 2.52,
    3.1, 3.38, 4.15, 4.35, 4.25, 3.9, 4.1, 4.7, 5.0 };

  memcpy(&dmh[0], &dv0[0], 54U * sizeof(real_T));
  rt_InitInfAndNaN(8U);
}

/* End of code generation (EstimateNoiseW_initialize.c) */
