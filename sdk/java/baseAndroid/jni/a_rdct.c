/*
 * a_rdct.c
 *
 * Code generation for function 'a_rdct'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "a_rdct.h"
#include "a_melcepst_emxutil.h"
#include "fft.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void a_rdct(emxArray_creal_T *x, emxArray_real_T *y)
{
  int32_T m;
  real_T a;
  int32_T i4;
  int32_T cdiff;
  real_T r;
  int32_T absb;
  int32_T c_k;
  int32_T ndbl;
  emxArray_creal_T *b_x;
  int32_T apnd;
  int32_T loop_ub;
  emxArray_real_T *b_y;
  emxArray_creal_T *c_y;
  emxArray_creal_T *c_x;
  real_T x_im;
  real_T b_x_im;
  emxArray_creal_T *r8;
  emxArray_creal_T *z;
  emxArray_creal_T *r9;

  /* RDCT     Discrete cosine transform of real data Y=(X,N,A,B) */
  /*  Data is truncated/padded to length N. */
  /*  */
  /*  This routine is equivalent to multiplying by the matrix */
  /*  */
  /*    rdct(eye(n)) = diag([sqrt(2)*B/A repmat(2/A,1,n-1)]) * cos((0:n-1)'*(0.5:n)*pi/n) */
  /*  */
  /*  Default values of the scaling factors are A=sqrt(2N) and B=1 which */
  /*  results in an orthogonal matrix. Other common values are A=1 or N and/or B=1 or sqrt(2). */
  /*  If b~=1 then the columns are no longer orthogonal. */
  /*  */
  /*  see IRDCT for the inverse transform */
  /*  BUG: in line 51 we should do chopping after transform and not before */
  /*       Copyright (C) Mike Brookes 1998 */
  /*       Version: $Id: rdct.m,v 1.6 2007/05/04 07:01:39 dmb Exp $ */
  /*  */
  /*    VOICEBOX is a MATLAB toolbox for speech processing. */
  /*    Home page: http://www.ee.ic.ac.uk/hp/staff/dmb/voicebox/voicebox.html */
  /*  */
  /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */
  /*    This program is free software; you can redistribute it and/or modify */
  /*    it under the terms of the GNU General Public License as published by */
  /*    the Free Software Foundation; either version 2 of the License, or */
  /*    (at your option) any later version. */
  /*  */
  /*    This program is distributed in the hope that it will be useful, */
  /*    but WITHOUT ANY WARRANTY; without even the implied warranty of */
  /*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the */
  /*    GNU General Public License for more details. */
  /*  */
  /*    You can obtain a copy of the GNU General Public License from */
  /*    http://www.gnu.org/copyleft/gpl.html or by writing to */
  /*    Free Software Foundation, Inc.,675 Mass Ave, Cambridge, MA 02139, USA. */
  /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */
  m = x->size[0];
  a = sqrt(2.0 * (real_T)x->size[0]);
  if (1 > x->size[0]) {
    i4 = 1;
    cdiff = -1;
  } else {
    i4 = 2;
    cdiff = x->size[0] - 1;
  }

  r = (real_T)x->size[0] / 2.0;
  if (r < 0.0) {
    r = ceil(r);
  } else {
    r = floor(r);
  }

  absb = (int32_T)r << 1;
  if (2 > absb) {
    absb = 1;
    c_k = 1;
    ndbl = 0;
  } else {
    c_k = -2;
    ndbl = 2;
  }

  emxInit_creal_T(&b_x, 1);
  apnd = b_x->size[0];
  b_x->size[0] = (cdiff / i4 + (ndbl - absb) / c_k) + 2;
  emxEnsureCapacity((emxArray__common *)b_x, apnd, (int32_T)sizeof(creal_T));
  loop_ub = cdiff / i4;
  for (apnd = 0; apnd <= loop_ub; apnd++) {
    b_x->data[apnd] = x->data[i4 * apnd];
  }

  loop_ub = (ndbl - absb) / c_k;
  for (ndbl = 0; ndbl <= loop_ub; ndbl++) {
    b_x->data[(ndbl + cdiff / i4) + 1] = x->data[(absb + c_k * ndbl) - 1];
  }

  i4 = x->size[0];
  x->size[0] = b_x->size[0];
  emxEnsureCapacity((emxArray__common *)x, i4, (int32_T)sizeof(creal_T));
  loop_ub = b_x->size[0] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    x->data[i4] = b_x->data[i4];
  }

  emxFree_creal_T(&b_x);
  r = -1.5707963267948966 / (real_T)m;
  if (m - 1 < 1) {
    ndbl = 0;
    apnd = m - 1;
  } else {
    ndbl = (int32_T)floor((((real_T)m - 1.0) - 1.0) + 0.5);
    apnd = ndbl + 1;
    cdiff = (ndbl - m) + 2;
    absb = (int32_T)fabs((real_T)m - 1.0);
    if (1 > absb) {
      absb = 1;
    }

    if (fabs((real_T)cdiff) < 4.4408920985006262E-16 * (real_T)absb) {
      ndbl++;
      apnd = m - 1;
    } else if (cdiff > 0) {
      apnd = ndbl;
    } else {
      ndbl++;
    }

    if (ndbl >= 0) {
    } else {
      ndbl = 0;
    }
  }

  emxInit_real_T(&b_y, 2);
  i4 = b_y->size[0] * b_y->size[1];
  b_y->size[0] = 1;
  b_y->size[1] = ndbl;
  emxEnsureCapacity((emxArray__common *)b_y, i4, (int32_T)sizeof(real_T));
  if (ndbl > 0) {
    b_y->data[0] = 1.0;
    if (ndbl > 1) {
      b_y->data[ndbl - 1] = (real_T)apnd;
      cdiff = ndbl - 1;
      absb = cdiff >> 1;
      for (c_k = 1; c_k <= absb - 1; c_k++) {
        b_y->data[c_k] = 1.0 + (real_T)c_k;
        b_y->data[(ndbl - c_k) - 1] = (real_T)(apnd - c_k);
      }

      if (absb << 1 == cdiff) {
        b_y->data[absb] = (1.0 + (real_T)apnd) / 2.0;
      } else {
        b_y->data[absb] = 1.0 + (real_T)absb;
        b_y->data[absb + 1] = (real_T)(apnd - absb);
      }
    }
  }

  b_emxInit_creal_T(&c_y, 2);
  i4 = c_y->size[0] * c_y->size[1];
  c_y->size[0] = 1;
  c_y->size[1] = b_y->size[1];
  emxEnsureCapacity((emxArray__common *)c_y, i4, (int32_T)sizeof(creal_T));
  loop_ub = b_y->size[0] * b_y->size[1] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    c_y->data[i4].re = b_y->data[i4] * 0.0;
    c_y->data[i4].im = b_y->data[i4] * r;
  }

  emxFree_real_T(&b_y);
  b_emxInit_creal_T(&c_x, 2);
  i4 = c_x->size[0] * c_x->size[1];
  c_x->size[0] = 1;
  c_x->size[1] = c_y->size[1];
  emxEnsureCapacity((emxArray__common *)c_x, i4, (int32_T)sizeof(creal_T));
  loop_ub = c_y->size[0] * c_y->size[1] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    c_x->data[i4] = c_y->data[i4];
  }

  for (c_k = 0; c_k <= c_y->size[1] - 1; c_k++) {
    r = exp(c_x->data[c_k].re / 2.0);
    x_im = c_x->data[c_k].im;
    b_x_im = c_x->data[c_k].im;
    c_x->data[c_k].re = r * (r * cos(x_im));
    c_x->data[c_k].im = r * (r * sin(b_x_im));
  }

  emxFree_creal_T(&c_y);
  b_emxInit_creal_T(&r8, 2);
  i4 = r8->size[0] * r8->size[1];
  r8->size[0] = 1;
  r8->size[1] = 1 + c_x->size[1];
  emxEnsureCapacity((emxArray__common *)r8, i4, (int32_T)sizeof(creal_T));
  r8->data[0].re = 1.4142135623730951;
  r8->data[0].im = 0.0;
  loop_ub = c_x->size[1] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    r8->data[r8->size[0] * (i4 + 1)].re = 2.0 * c_x->data[c_x->size[0] * i4].re;
    r8->data[r8->size[0] * (i4 + 1)].im = 2.0 * c_x->data[c_x->size[0] * i4].im;
  }

  emxFree_creal_T(&c_x);
  emxInit_creal_T(&z, 1);
  i4 = z->size[0];
  z->size[0] = r8->size[1];
  emxEnsureCapacity((emxArray__common *)z, i4, (int32_T)sizeof(creal_T));
  loop_ub = r8->size[1] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    z->data[i4] = r8->data[i4];
  }

  emxFree_creal_T(&r8);
  emxInit_creal_T(&r9, 1);
  fft(x, r9);
  i4 = y->size[0];
  y->size[0] = r9->size[0];
  emxEnsureCapacity((emxArray__common *)y, i4, (int32_T)sizeof(real_T));
  loop_ub = r9->size[0] - 1;
  for (i4 = 0; i4 <= loop_ub; i4++) {
    r = r9->data[i4].re * z->data[i4].re - r9->data[i4].im * z->data[i4].im;
    y->data[i4] = r / a;
  }

  emxFree_creal_T(&r9);
  emxFree_creal_T(&z);
}

/* End of code generation (a_rdct.c) */
