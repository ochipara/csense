/*
 * frq2mel.h
 *
 * Code generation for function 'frq2mel'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

#ifndef __FRQ2MEL_H__
#define __FRQ2MEL_H__
/* Include files */
#include <math.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "rt_defines.h"
#include "rt_nonfinite.h"

#include "rtwtypes.h"
#include "a_melcepst_types.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
extern void b_frq2mel(const emxArray_real_T *frq, emxArray_real_T *mel);
extern void frq2mel(const real_T frq[2], real_T mel[2]);
#endif
/* End of code generation (frq2mel.h) */
