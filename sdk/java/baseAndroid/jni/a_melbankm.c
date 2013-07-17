/*
 * a_melbankm.c
 *
 * Code generation for function 'a_melbankm'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "a_melbankm.h"
#include "a_melcepst_emxutil.h"
#include "sum.h"
#include "floor.h"
#include "frq2mel.h"
#include "mel2frq.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
static void b_eml_null_assignment(emxArray_real_T *x, real_T idx);
static void eml_li_find(const emxArray_boolean_T *x, emxArray_int32_T *y);
static void eml_null_assignment(emxArray_real_T *x);
static real_T rt_roundd_snf(real_T u);

/* Function Definitions */
static void b_eml_null_assignment(emxArray_real_T *x, real_T idx)
{
  int32_T nxin;
  real_T d0;
  int32_T c_k;
  int32_T i8;
  emxArray_int32_T *r11;
  emxArray_real_T *b_x;
  nxin = x->size[1];
  d0 = rt_roundd_snf(idx);
  if (d0 < 2.147483648E+9) {
    if (d0 >= -2.147483648E+9) {
      c_k = (int32_T)d0;
    } else {
      c_k = MIN_int32_T;
    }
  } else if (d0 >= 2.147483648E+9) {
    c_k = MAX_int32_T;
  } else {
    c_k = 0;
  }

  while (c_k <= nxin - 1) {
    x->data[c_k - 1] = x->data[c_k];
    c_k++;
  }

  if (1 > nxin - 1) {
    i8 = 0;
  } else {
    i8 = nxin - 1;
  }

  b_emxInit_int32_T(&r11, 1);
  nxin = r11->size[0];
  r11->size[0] = i8;
  emxEnsureCapacity((emxArray__common *)r11, nxin, (int32_T)sizeof(int32_T));
  c_k = i8 - 1;
  for (i8 = 0; i8 <= c_k; i8++) {
    r11->data[i8] = 1 + i8;
  }

  emxInit_real_T(&b_x, 2);
  nxin = r11->size[0];
  i8 = b_x->size[0] * b_x->size[1];
  b_x->size[0] = 1;
  b_x->size[1] = nxin;
  emxEnsureCapacity((emxArray__common *)b_x, i8, (int32_T)sizeof(real_T));
  c_k = nxin - 1;
  for (i8 = 0; i8 <= c_k; i8++) {
    nxin = 0;
    while (nxin <= 0) {
      b_x->data[b_x->size[0] * i8] = x->data[r11->data[i8] - 1];
      nxin = 1;
    }
  }

  emxFree_int32_T(&r11);
  i8 = x->size[0] * x->size[1];
  x->size[0] = 1;
  x->size[1] = b_x->size[1];
  emxEnsureCapacity((emxArray__common *)x, i8, (int32_T)sizeof(real_T));
  c_k = b_x->size[1] - 1;
  for (i8 = 0; i8 <= c_k; i8++) {
    x->data[x->size[0] * i8] = b_x->data[b_x->size[0] * i8];
  }

  emxFree_real_T(&b_x);
}

static void eml_li_find(const emxArray_boolean_T *x, emxArray_int32_T *y)
{
  int32_T n;
  int32_T c_k;
  int32_T i;
  int32_T j;
  n = x->size[1];
  c_k = 0;
  for (i = 1; i <= n; i++) {
    if (x->data[i - 1]) {
      c_k++;
    }
  }

  j = y->size[0] * y->size[1];
  y->size[0] = 1;
  y->size[1] = c_k;
  emxEnsureCapacity((emxArray__common *)y, j, (int32_T)sizeof(int32_T));
  j = 0;
  for (i = 1; i <= n; i++) {
    if (x->data[i - 1]) {
      y->data[j] = i;
      j++;
    }
  }
}

static void eml_null_assignment(emxArray_real_T *x)
{
  int32_T nxin;
  int32_T c_k;
  int32_T i7;
  emxArray_int32_T *r10;
  emxArray_real_T *b_x;
  nxin = x->size[1];
  for (c_k = 1; c_k <= nxin - 1; c_k++) {
    x->data[c_k - 1] = x->data[c_k];
  }

  if (1 > nxin - 1) {
    i7 = 0;
  } else {
    i7 = nxin - 1;
  }

  b_emxInit_int32_T(&r10, 1);
  nxin = r10->size[0];
  r10->size[0] = i7;
  emxEnsureCapacity((emxArray__common *)r10, nxin, (int32_T)sizeof(int32_T));
  c_k = i7 - 1;
  for (i7 = 0; i7 <= c_k; i7++) {
    r10->data[i7] = 1 + i7;
  }

  emxInit_real_T(&b_x, 2);
  nxin = r10->size[0];
  i7 = b_x->size[0] * b_x->size[1];
  b_x->size[0] = 1;
  b_x->size[1] = nxin;
  emxEnsureCapacity((emxArray__common *)b_x, i7, (int32_T)sizeof(real_T));
  c_k = nxin - 1;
  for (i7 = 0; i7 <= c_k; i7++) {
    nxin = 0;
    while (nxin <= 0) {
      b_x->data[b_x->size[0] * i7] = x->data[r10->data[i7] - 1];
      nxin = 1;
    }
  }

  emxFree_int32_T(&r10);
  i7 = x->size[0] * x->size[1];
  x->size[0] = 1;
  x->size[1] = b_x->size[1];
  emxEnsureCapacity((emxArray__common *)x, i7, (int32_T)sizeof(real_T));
  c_k = b_x->size[1] - 1;
  for (i7 = 0; i7 <= c_k; i7++) {
    x->data[x->size[0] * i7] = b_x->data[b_x->size[0] * i7];
  }

  emxFree_real_T(&b_x);
}

static real_T rt_roundd_snf(real_T u)
{
  real_T y;
  if (fabs(u) < 4.503599627370496E+15) {
    if (u >= 0.5) {
      y = floor(u + 0.5);
    } else if (u > -0.5) {
      y = -0.0;
    } else {
      y = ceil(u - 0.5);
    }
  } else {
    y = u;
  }

  return y;
}

void a_melbankm(emxArray_real_T *x, int32_T *mc, int32_T *mn)
{
  real_T dv3[2];
  int32_T i3;
  real_T mflh[2];
  real_T melrng;
  int32_T c_k;
  real_T melinc;
  real_T b_mflh[4];
  static const int8_T a[4] = { 0, 1, 32, 33 };

  real_T blim[4];
  int32_T b1;
  int32_T b4;
  int32_T n;
  uint32_T span;
  emxArray_int32_T *ii;
  int32_T yk;
  emxArray_real_T *b_ii;
  int32_T loop_ub;
  emxArray_real_T *pf;
  int32_T nd2;
  emxArray_real_T *fp;
  emxArray_boolean_T *msk;
  boolean_T exitg3;
  emxArray_int32_T *r1;
  emxArray_int32_T *c_ii;
  emxArray_real_T *d_ii;
  int32_T k3;
  int32_T k4;
  boolean_T exitg2;
  emxArray_int32_T *r2;
  emxArray_int32_T *e_ii;
  emxArray_real_T *f_ii;
  int32_T k2;
  emxArray_int32_T *r3;
  emxArray_int32_T *r4;
  emxArray_real_T *r;
  emxArray_int32_T *y;
  emxArray_int32_T *c;
  emxArray_int32_T *r5;
  emxArray_int32_T *r6;
  emxArray_real_T *v;
  boolean_T exitg1;

  /* MELBANKM determine matrix for a mel/erb/bark-spaced filterbank [X,MN,MX]=(P,N,FS,FL,FH,W) */
  /*  */
  /*  Inputs: */
  /*        p   number of filters in filterbank or the filter spacing in k-mel/bark/erb [ceil(4.6*log10(fs))] */
  /* 		n   length of fft */
  /* 		fs  sample rate in Hz */
  /* 		fl  low end of the lowest filter as a fraction of fs [default = 0] */
  /* 		fh  high end of highest filter as a fraction of fs [default = 0.5] */
  /* 		w   any sensible combination of the following: */
  /*              'b' = bark scale instead of mel */
  /*              'e' = erb-rate scale */
  /*              'l' = log10 Hz frequency scale */
  /*              'f' = linear frequency scale */
  /*  */
  /*              'c' = fl/fh specify centre of low and high filters */
  /*              'h' = fl/fh are in Hz instead of fractions of fs */
  /*              'H' = fl/fh are in mel/erb/bark/log10 */
  /*  */
  /* 		      't' = triangular shaped filters in mel/erb/bark domain (default) */
  /* 		      'n' = hanning shaped filters in mel/erb/bark domain */
  /* 		      'm' = hamming shaped filters in mel/erb/bark domain */
  /*  */
  /* 		      'z' = highest and lowest filters taper down to zero [default] */
  /* 		      'y' = lowest filter remains at 1 down to 0 frequency and */
  /* 			        highest filter remains at 1 up to nyquist freqency */
  /*  */
  /*              'u' = scale filters to sum to unity */
  /*  */
  /*              's' = single-sided: do not double filters to account for negative frequencies */
  /*  */
  /*              'g' = plot idealized filters [default if no output arguments present] */
  /*  */
  /*  Note that the filter shape (triangular, hamming etc) is defined in the mel (or erb etc) domain. */
  /*  Some people instead define an asymmetric triangular filter in the frequency domain. */
  /*  */
  /* 		       If 'ty' or 'ny' is specified, the total power in the fft is preserved. */
  /*  */
  /*  Outputs:	x     a sparse matrix containing the filterbank amplitudes */
  /* 		          If the mn and mx outputs are given then size(x)=[p,mx-mn+1] */
  /*                  otherwise size(x)=[p,1+floor(n/2)] */
  /*                  Note that the peak filter values equal 2 to account for the power */
  /*                  in the negative FFT frequencies. */
  /*            mc    the filterbank centre frequencies in mel/erb/bark */
  /* 		    mn    the lowest fft bin with a non-zero coefficient */
  /* 		    mx    the highest fft bin with a non-zero coefficient */
  /*                  Note: you must specify both or neither of mn and mx. */
  /*  */
  /*  Examples of use: */
  /*  */
  /*  (a) Calcuate the Mel-frequency Cepstral Coefficients */
  /*  */
  /*        f=rfft(s);			        % rfft() returns only 1+floor(n/2) coefficients */
  /* 		x=melbankm(p,n,fs);	        % n is the fft length, p is the number of filters wanted */
  /* 		z=log(x*abs(f).^2);         % multiply x by the power spectrum */
  /* 		c=dct(z);                   % take the DCT */
  /*  */
  /*  (b) Calcuate the Mel-frequency Cepstral Coefficients efficiently */
  /*  */
  /*        f=fft(s);                        % n is the fft length, p is the number of filters wanted */
  /*        [x,mc,na,nb]=melbankm(p,n,fs);   % na:nb gives the fft bins that are needed */
  /*        z=log(x*(f(na:nb)).*conj(f(na:nb))); */
  /*  */
  /*  (c) Plot the calculated filterbanks */
  /*  */
  /*       plot((0:floor(n/2))*fs/n,melbankm(p,n,fs)')   % fs=sample frequency */
  /*  */
  /*  (d) Plot the idealized filterbanks (without output sampling) */
  /*  */
  /*       melbankm(p,n,fs); */
  /*  */
  /*  References: */
  /*  */
  /*  [1] S. S. Stevens, J. Volkman, and E. B. Newman. A scale for the measurement */
  /*      of the psychological magnitude of pitch. J. Acoust Soc Amer, 8: 185–19, 1937. */
  /*  [2] S. Davis and P. Mermelstein. Comparison of parametric representations for */
  /*      monosyllabic word recognition in continuously spoken sentences. */
  /*      IEEE Trans Acoustics Speech and Signal Processing, 28 (4): 357–366, Aug. 1980. */
  /*       Copyright (C) Mike Brookes 1997-2009 */
  /*       Version: $Id: melbankm.m,v 1.11 2010/01/02 20:02:22 dmb Exp $ */
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
  /*  Note "FFT bin_0" assumes DC = bin 0 whereas "FFT bin_1" means DC = bin 1 */
  /*  1 if single sided else 2 */
  for (i3 = 0; i3 < 2; i3++) {
    dv3[i3] = 8000.0 * (real_T)i3;
  }

  frq2mel(dv3, mflh);

  /*  convert frequency limits into mel */
  melrng = 0.0;
  for (c_k = 0; c_k < 2; c_k++) {
    melrng += mflh[c_k] * ((real_T)(c_k << 1) - 1.0);
  }

  /*  mel range */
  /*  bin index of highest positive frequency (Nyquist if n is even) */
  melinc = melrng / 33.0;

  /*  */
  /*  Calculate the FFT bins corresponding to [filter#1-low filter#1-mid filter#p-mid filter#p-high] */
  /*  */
  for (i3 = 0; i3 < 4; i3++) {
    b_mflh[i3] = mflh[0] + (real_T)a[i3] * melinc;
  }

  mel2frq(b_mflh, blim);
  for (i3 = 0; i3 < 4; i3++) {
    blim[i3] = blim[i3] * 512.0 / 16000.0;
  }

  /* mc=mflh(1)+(1:p)*melinc;    % mel centre frequencies */
  melrng = rt_roundd_snf(floor(blim[0]) + 1.0);
  if (melrng < 2.147483648E+9) {
    if (melrng >= -2.147483648E+9) {
      b1 = (int32_T)melrng;
    } else {
      b1 = MIN_int32_T;
    }
  } else if (melrng >= 2.147483648E+9) {
    b1 = MAX_int32_T;
  } else {
    b1 = 0;
  }

  /*  lowest FFT bin_0 required might be negative) */
  melrng = ceil(blim[3]) - 1.0;
  if ((256.0 <= melrng) || rtIsNaN(melrng)) {
    melrng = 256.0;
  }

  melrng = rt_roundd_snf(melrng);
  if (melrng < 2.147483648E+9) {
    if (melrng >= -2.147483648E+9) {
      b4 = (int32_T)melrng;
    } else {
      b4 = MIN_int32_T;
    }
  } else {
    b4 = 0;
  }

  /*  highest FFT bin_0 required */
  /*  */
  /*  now map all the useful FFT bins_0 to filter1 centres */
  /*  */
  if (b4 < b1) {
    n = 0;
  } else {
    if ((b1 < 0) && (b4 >= 0)) {
      span = (uint32_T)b4 - (uint32_T)b1;
    } else {
      span = (uint32_T)(b4 - b1);
    }

    n = (int32_T)span + 1;
  }

  emxInit_int32_T(&ii, 2);
  i3 = ii->size[0] * ii->size[1];
  ii->size[0] = 1;
  ii->size[1] = n;
  emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
  if (n > 0) {
    ii->data[0] = b1;
    yk = b1;
    for (c_k = 2; c_k <= n; c_k++) {
      yk++;
      ii->data[c_k - 1] = yk;
    }
  }

  emxInit_real_T(&b_ii, 2);
  i3 = b_ii->size[0] * b_ii->size[1];
  b_ii->size[0] = 1;
  b_ii->size[1] = ii->size[1];
  emxEnsureCapacity((emxArray__common *)b_ii, i3, (int32_T)sizeof(real_T));
  loop_ub = ii->size[0] * ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    b_ii->data[i3] = (real_T)ii->data[i3] * 16000.0 / 512.0;
  }

  emxInit_real_T(&pf, 2);
  b_frq2mel(b_ii, pf);
  i3 = pf->size[0] * pf->size[1];
  pf->size[0] = 1;
  pf->size[1] = pf->size[1];
  emxEnsureCapacity((emxArray__common *)pf, i3, (int32_T)sizeof(real_T));
  nd2 = pf->size[0];
  yk = pf->size[1];
  emxFree_real_T(&b_ii);
  loop_ub = nd2 * yk - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    pf->data[i3] -= mflh[0];
  }

  i3 = pf->size[0] * pf->size[1];
  pf->size[0] = 1;
  pf->size[1] = pf->size[1];
  emxEnsureCapacity((emxArray__common *)pf, i3, (int32_T)sizeof(real_T));
  nd2 = pf->size[0];
  yk = pf->size[1];
  loop_ub = nd2 * yk - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    pf->data[i3] /= melinc;
  }

  /*  */
  /*   remove any incorrect entries in pf due to rounding errors */
  /*  */
  if (pf->data[0] < 0.0) {
    eml_null_assignment(pf);
    nd2 = b1 + 1;
    if ((b1 > 0) && (nd2 <= 0)) {
      nd2 = MAX_int32_T;
    }

    b1 = nd2;
  }

  if (pf->data[pf->size[1] - 1] >= 33.0) {
    i3 = pf->size[1];
    b_eml_null_assignment(pf, (real_T)i3);
    nd2 = b4 - 1;
    if ((b4 < 0) && (nd2 >= 0)) {
      nd2 = MIN_int32_T;
    }

    b4 = nd2;
  }

  emxInit_real_T(&fp, 2);
  i3 = fp->size[0] * fp->size[1];
  fp->size[0] = 1;
  fp->size[1] = pf->size[1];
  emxEnsureCapacity((emxArray__common *)fp, i3, (int32_T)sizeof(real_T));
  loop_ub = pf->size[0] * pf->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    fp->data[i3] = pf->data[i3];
  }

  b_floor(fp);

  /*  FFT bin_0 i contributes to filters_1 fp(1+i-b1)+[0 1] */
  i3 = pf->size[0] * pf->size[1];
  pf->size[0] = 1;
  pf->size[1] = pf->size[1];
  emxEnsureCapacity((emxArray__common *)pf, i3, (int32_T)sizeof(real_T));
  nd2 = pf->size[0];
  yk = pf->size[1];
  loop_ub = nd2 * yk - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    pf->data[i3] -= fp->data[i3];
  }

  emxInit_boolean_T(&msk, 2);

  /*  multiplier for upper filter */
  i3 = msk->size[0] * msk->size[1];
  msk->size[0] = 1;
  msk->size[1] = fp->size[1];
  emxEnsureCapacity((emxArray__common *)msk, i3, (int32_T)sizeof(boolean_T));
  loop_ub = fp->size[0] * fp->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    msk->data[i3] = (fp->data[i3] < 32.0);
  }

  yk = msk->size[1];
  if (1 <= yk) {
    c_k = 1;
  } else {
    c_k = yk;
  }

  n = 0;
  i3 = ii->size[0] * ii->size[1];
  ii->size[0] = 1;
  ii->size[1] = c_k;
  emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
  exitg3 = FALSE;
  while ((exitg3 == 0U) && (yk > 0)) {
    if (msk->data[yk - 1]) {
      n = 1;
      ii->data[0] = yk;
      exitg3 = TRUE;
    } else {
      yk--;
    }
  }

  if (c_k == 1) {
    if (n == 0) {
      i3 = ii->size[0] * ii->size[1];
      ii->size[0] = 1;
      ii->size[1] = 0;
      emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
    }
  } else {
    if (1 > n) {
      i3 = -1;
    } else {
      i3 = 0;
    }

    b_emxInit_int32_T(&r1, 1);
    c_k = r1->size[0];
    r1->size[0] = i3 + 1;
    emxEnsureCapacity((emxArray__common *)r1, c_k, (int32_T)sizeof(int32_T));
    c_k = 0;
    while (c_k <= i3) {
      r1->data[0] = 1;
      c_k = 1;
    }

    emxInit_int32_T(&c_ii, 2);
    nd2 = r1->size[0];
    i3 = c_ii->size[0] * c_ii->size[1];
    c_ii->size[0] = 1;
    c_ii->size[1] = nd2;
    emxEnsureCapacity((emxArray__common *)c_ii, i3, (int32_T)sizeof(int32_T));
    loop_ub = nd2 - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      c_k = 0;
      while (c_k <= 0) {
        c_ii->data[c_ii->size[0] * i3] = ii->data[r1->data[i3] - 1];
        c_k = 1;
      }
    }

    emxFree_int32_T(&r1);
    i3 = ii->size[0] * ii->size[1];
    ii->size[0] = 1;
    ii->size[1] = c_ii->size[1];
    emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
    loop_ub = c_ii->size[1] - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      ii->data[ii->size[0] * i3] = c_ii->data[c_ii->size[0] * i3];
    }

    emxFree_int32_T(&c_ii);
    n = ii->size[1];
    nd2 = (n + (n < 0)) >> 1;
    yk = 1;
    while (yk <= nd2) {
      yk = ii->data[0];
      ii->data[0] = ii->data[n - 1];
      ii->data[n - 1] = yk;
      yk = 2;
    }
  }

  emxInit_real_T(&d_ii, 2);
  i3 = d_ii->size[0] * d_ii->size[1];
  d_ii->size[0] = 1;
  d_ii->size[1] = ii->size[1];
  emxEnsureCapacity((emxArray__common *)d_ii, i3, (int32_T)sizeof(real_T));
  loop_ub = ii->size[0] * ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    d_ii->data[i3] = (real_T)ii->data[i3];
  }

  melrng = rt_roundd_snf(sum(d_ii));
  if (melrng < 2.147483648E+9) {
    if (melrng >= -2.147483648E+9) {
      k3 = (int32_T)melrng;
    } else {
      k3 = MIN_int32_T;
    }
  } else if (melrng >= 2.147483648E+9) {
    k3 = MAX_int32_T;
  } else {
    k3 = 0;
  }

  /*  FFT bin_1 k3+b1 is the last to contribute to both upper and lower filters */
  k4 = fp->size[1];

  /*  FFT bin_1 k4+b1 is the last to contribute to any filters */
  i3 = msk->size[0] * msk->size[1];
  msk->size[0] = 1;
  msk->size[1] = fp->size[1];
  emxEnsureCapacity((emxArray__common *)msk, i3, (int32_T)sizeof(boolean_T));
  emxFree_real_T(&d_ii);
  loop_ub = fp->size[0] * fp->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    msk->data[i3] = (fp->data[i3] > 0.0);
  }

  yk = msk->size[1];
  if (1 <= yk) {
    c_k = 1;
  } else {
    c_k = yk;
  }

  n = 0;
  i3 = ii->size[0] * ii->size[1];
  ii->size[0] = 1;
  ii->size[1] = c_k;
  emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
  nd2 = 1;
  exitg2 = FALSE;
  while ((exitg2 == 0U) && (nd2 <= yk)) {
    if (msk->data[nd2 - 1]) {
      n = 1;
      ii->data[0] = nd2;
      exitg2 = TRUE;
    } else {
      nd2++;
    }
  }

  if (c_k == 1) {
    if (n == 0) {
      i3 = ii->size[0] * ii->size[1];
      ii->size[0] = 1;
      ii->size[1] = 0;
      emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
    }
  } else {
    if (1 > n) {
      i3 = -1;
    } else {
      i3 = 0;
    }

    b_emxInit_int32_T(&r2, 1);
    c_k = r2->size[0];
    r2->size[0] = i3 + 1;
    emxEnsureCapacity((emxArray__common *)r2, c_k, (int32_T)sizeof(int32_T));
    c_k = 0;
    while (c_k <= i3) {
      r2->data[0] = 1;
      c_k = 1;
    }

    emxInit_int32_T(&e_ii, 2);
    nd2 = r2->size[0];
    i3 = e_ii->size[0] * e_ii->size[1];
    e_ii->size[0] = 1;
    e_ii->size[1] = nd2;
    emxEnsureCapacity((emxArray__common *)e_ii, i3, (int32_T)sizeof(int32_T));
    loop_ub = nd2 - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      c_k = 0;
      while (c_k <= 0) {
        e_ii->data[e_ii->size[0] * i3] = ii->data[r2->data[i3] - 1];
        c_k = 1;
      }
    }

    emxFree_int32_T(&r2);
    i3 = ii->size[0] * ii->size[1];
    ii->size[0] = 1;
    ii->size[1] = e_ii->size[1];
    emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
    loop_ub = e_ii->size[1] - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      ii->data[ii->size[0] * i3] = e_ii->data[e_ii->size[0] * i3];
    }

    emxFree_int32_T(&e_ii);
  }

  emxInit_real_T(&f_ii, 2);
  i3 = f_ii->size[0] * f_ii->size[1];
  f_ii->size[0] = 1;
  f_ii->size[1] = ii->size[1];
  emxEnsureCapacity((emxArray__common *)f_ii, i3, (int32_T)sizeof(real_T));
  loop_ub = ii->size[0] * ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    f_ii->data[i3] = (real_T)ii->data[i3];
  }

  melrng = rt_roundd_snf(sum(f_ii));
  if (melrng < 2.147483648E+9) {
    if (melrng >= -2.147483648E+9) {
      k2 = (int32_T)melrng;
    } else {
      k2 = MIN_int32_T;
    }
  } else if (melrng >= 2.147483648E+9) {
    k2 = MAX_int32_T;
  } else {
    k2 = 0;
  }

  /*  FFT bin_1 k2+b1 is the first to contribute to both upper and lower filters */
  emxFree_real_T(&f_ii);
  if (k2 == 0) {
    k2 = k4 + 1;
    if ((k4 > 0) && (k2 <= 0)) {
      k2 = MAX_int32_T;
    }
  }

  if (1 > k3) {
    i3 = 0;
  } else {
    i3 = k3;
  }

  if (k2 > k4) {
    c_k = 1;
    yk = 0;
  } else {
    c_k = k2;
    yk = k4;
  }

  b_emxInit_int32_T(&r3, 1);
  nd2 = r3->size[0];
  r3->size[0] = i3;
  emxEnsureCapacity((emxArray__common *)r3, nd2, (int32_T)sizeof(int32_T));
  loop_ub = i3 - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    r3->data[i3] = 1 + i3;
  }

  b_emxInit_int32_T(&r4, 1);
  i3 = r4->size[0];
  r4->size[0] = (yk - c_k) + 1;
  emxEnsureCapacity((emxArray__common *)r4, i3, (int32_T)sizeof(int32_T));
  loop_ub = yk - c_k;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    r4->data[i3] = c_k + i3;
  }

  emxInit_real_T(&r, 2);
  nd2 = r3->size[0];
  yk = r4->size[0];
  i3 = r->size[0] * r->size[1];
  r->size[0] = 1;
  r->size[1] = nd2 + yk;
  emxEnsureCapacity((emxArray__common *)r, i3, (int32_T)sizeof(real_T));
  loop_ub = nd2 - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c_k = 0;
    while (c_k <= 0) {
      r->data[r->size[0] * i3] = 1.0 + fp->data[r3->data[i3] - 1];
      c_k = 1;
    }
  }

  emxFree_int32_T(&r3);
  loop_ub = yk - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c_k = 0;
    while (c_k <= 0) {
      r->data[r->size[0] * (i3 + nd2)] = fp->data[r4->data[i3] - 1];
      c_k = 1;
    }
  }

  emxFree_int32_T(&r4);

  /*  filter number_1 */
  if (k3 < 1) {
    n = 0;
  } else {
    n = k3;
  }

  i3 = ii->size[0] * ii->size[1];
  ii->size[0] = 1;
  ii->size[1] = n;
  emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
  if (n > 0) {
    ii->data[0] = 1;
    yk = 1;
    for (c_k = 2; c_k <= n; c_k++) {
      yk++;
      ii->data[c_k - 1] = yk;
    }
  }

  if (k4 < k2) {
    n = 0;
  } else {
    if (k2 < 0) {
      span = (uint32_T)k4 - (uint32_T)k2;
    } else {
      span = (uint32_T)(k4 - k2);
    }

    n = (int32_T)span + 1;
  }

  emxInit_int32_T(&y, 2);
  i3 = y->size[0] * y->size[1];
  y->size[0] = 1;
  y->size[1] = n;
  emxEnsureCapacity((emxArray__common *)y, i3, (int32_T)sizeof(int32_T));
  if (n > 0) {
    y->data[0] = k2;
    yk = k2;
    for (c_k = 2; c_k <= n; c_k++) {
      yk++;
      y->data[c_k - 1] = yk;
    }
  }

  emxInit_int32_T(&c, 2);
  i3 = c->size[0] * c->size[1];
  c->size[0] = 1;
  c->size[1] = ii->size[1] + y->size[1];
  emxEnsureCapacity((emxArray__common *)c, i3, (int32_T)sizeof(int32_T));
  loop_ub = ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c->data[c->size[0] * i3] = ii->data[ii->size[0] * i3];
  }

  loop_ub = y->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c->data[c->size[0] * (i3 + ii->size[1])] = y->data[y->size[0] * i3];
  }

  /*  FFT bin_1 - b1 */
  if (1 > k3) {
    k3 = 0;
  }

  if (k2 > k4) {
    k2 = 1;
    k4 = 0;
  }

  b_emxInit_int32_T(&r5, 1);
  i3 = r5->size[0];
  r5->size[0] = k3;
  emxEnsureCapacity((emxArray__common *)r5, i3, (int32_T)sizeof(int32_T));
  loop_ub = k3 - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    r5->data[i3] = 1 + i3;
  }

  b_emxInit_int32_T(&r6, 1);
  i3 = r6->size[0];
  r6->size[0] = (k4 - k2) + 1;
  emxEnsureCapacity((emxArray__common *)r6, i3, (int32_T)sizeof(int32_T));
  loop_ub = k4 - k2;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    r6->data[i3] = k2 + i3;
  }

  emxInit_real_T(&v, 2);
  nd2 = r5->size[0];
  yk = r6->size[0];
  i3 = v->size[0] * v->size[1];
  v->size[0] = 1;
  v->size[1] = nd2 + yk;
  emxEnsureCapacity((emxArray__common *)v, i3, (int32_T)sizeof(real_T));
  loop_ub = nd2 - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c_k = 0;
    while (c_k <= 0) {
      v->data[v->size[0] * i3] = pf->data[r5->data[i3] - 1];
      c_k = 1;
    }
  }

  emxFree_int32_T(&r5);
  loop_ub = yk - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    c_k = 0;
    while (c_k <= 0) {
      v->data[v->size[0] * (i3 + nd2)] = 1.0 - pf->data[r6->data[i3] - 1];
      c_k = 1;
    }
  }

  emxFree_int32_T(&r6);
  emxFree_real_T(&pf);
  nd2 = b1 + 1;
  if ((b1 > 0) && (nd2 <= 0)) {
    nd2 = MAX_int32_T;
  }

  *mn = nd2;

  /*  lowest fft bin_1 */
  /*  highest fft bin_1 */
  if (b1 < 0) {
    i3 = c->size[0] * c->size[1];
    c->size[0] = 1;
    c->size[1] = c->size[1];
    emxEnsureCapacity((emxArray__common *)c, i3, (int32_T)sizeof(int32_T));
    nd2 = c->size[0];
    yk = c->size[1];
    loop_ub = nd2 * yk - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      n = c->data[i3];
      nd2 = n + b1;
      if ((n < 0) && (nd2 >= 0)) {
        nd2 = MIN_int32_T;
      }

      c_k = nd2 - 1;
      if ((nd2 < 0) && (c_k >= 0)) {
        c_k = MIN_int32_T;
      }

      c->data[i3] = c_k;
    }

    for (i3 = 0; i3 < 2; i3++) {
      mflh[i3] = (real_T)c->size[i3];
    }

    i3 = ii->size[0] * ii->size[1];
    ii->size[0] = 1;
    ii->size[1] = (int32_T)mflh[1];
    emxEnsureCapacity((emxArray__common *)ii, i3, (int32_T)sizeof(int32_T));
    for (c_k = 0; c_k <= c->size[1] - 1; c_k++) {
      if (c->data[c_k] < 0) {
        i3 = c->data[c_k];
        if (i3 <= MIN_int32_T) {
          yk = MAX_int32_T;
        } else {
          yk = -i3;
        }
      } else {
        yk = c->data[c_k];
      }

      ii->data[c_k] = yk;
    }

    i3 = c->size[0] * c->size[1];
    c->size[0] = 1;
    c->size[1] = ii->size[1];
    emxEnsureCapacity((emxArray__common *)c, i3, (int32_T)sizeof(int32_T));
    loop_ub = ii->size[0] * ii->size[1] - 1;
    for (i3 = 0; i3 <= loop_ub; i3++) {
      n = ii->data[i3];
      nd2 = n - b1;
      if ((n >= 0) && (nd2 < 0)) {
        nd2 = MAX_int32_T;
      }

      c_k = nd2 + 1;
      if ((nd2 > 0) && (c_k <= 0)) {
        c_k = MAX_int32_T;
      }

      c->data[i3] = c_k;
    }

    /*  convert negative frequencies into positive */
  }

  /*  double all except the DC and Nyquist (if any) terms */
  i3 = msk->size[0] * msk->size[1];
  msk->size[0] = 1;
  msk->size[1] = c->size[1];
  emxEnsureCapacity((emxArray__common *)msk, i3, (int32_T)sizeof(boolean_T));
  loop_ub = c->size[0] * c->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    n = c->data[i3];
    yk = *mn;
    nd2 = n + yk;
    if ((n < 0) && ((yk < 0) && (nd2 >= 0))) {
      nd2 = MIN_int32_T;
    } else {
      if ((n > 0) && ((yk > 0) && (nd2 <= 0))) {
        nd2 = MAX_int32_T;
      }
    }

    n = c->data[i3];
    yk = *mn;
    c_k = n + yk;
    if ((n < 0) && ((yk < 0) && (c_k >= 0))) {
      c_k = MIN_int32_T;
    } else {
      if ((n > 0) && ((yk > 0) && (c_k <= 0))) {
        c_k = MAX_int32_T;
      }
    }

    msk->data[i3] = ((nd2 > 2) && (c_k < 258));
  }

  /*  there is no Nyquist term if n is odd */
  eml_li_find(msk, ii);
  i3 = y->size[0] * y->size[1];
  y->size[0] = 1;
  y->size[1] = ii->size[1];
  emxEnsureCapacity((emxArray__common *)y, i3, (int32_T)sizeof(int32_T));
  loop_ub = ii->size[0] * ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    y->data[i3] = ii->data[i3];
  }

  eml_li_find(msk, ii);
  i3 = fp->size[0] * fp->size[1];
  fp->size[0] = 1;
  fp->size[1] = ii->size[1];
  emxEnsureCapacity((emxArray__common *)fp, i3, (int32_T)sizeof(real_T));
  emxFree_boolean_T(&msk);
  loop_ub = ii->size[0] * ii->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    fp->data[i3] = v->data[ii->data[i3] - 1];
  }

  emxFree_int32_T(&ii);
  loop_ub = fp->size[0] * fp->size[1] - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    v->data[y->data[i3] - 1] = 2.0 * fp->data[i3];
  }

  emxFree_int32_T(&y);
  emxFree_real_T(&fp);

  /*  */
  /*  sort out the output argument options */
  /*  */
  /* x=sparse(r,c,v); */
  nd2 = 1;
  n = r->size[1];
  melrng = r->data[0];
  if (n > 1) {
    if (rtIsNaN(r->data[0])) {
      yk = 2;
      exitg1 = FALSE;
      while ((exitg1 == 0U) && (yk <= n)) {
        nd2 = yk;
        if (!rtIsNaN(r->data[yk - 1])) {
          melrng = r->data[yk - 1];
          exitg1 = TRUE;
        } else {
          yk++;
        }
      }
    }

    if (nd2 < n) {
      while (nd2 + 1 <= n) {
        if (r->data[nd2] > melrng) {
          melrng = r->data[nd2];
        }

        nd2++;
      }
    }
  }

  n = c->size[1];
  nd2 = c->data[0];
  if (n > 1) {
    for (yk = 1; yk + 1 <= n; yk++) {
      if (c->data[yk] > nd2) {
        nd2 = c->data[yk];
      }
    }
  }

  i3 = x->size[0] * x->size[1];
  x->size[0] = (int32_T)melrng;
  x->size[1] = nd2;
  emxEnsureCapacity((emxArray__common *)x, i3, (int32_T)sizeof(real_T));
  loop_ub = (int32_T)melrng * nd2 - 1;
  for (i3 = 0; i3 <= loop_ub; i3++) {
    x->data[i3] = 0.0;
  }

  for (c_k = 0; c_k <= (int32_T)melrng - 1; c_k++) {
    x->data[((int32_T)r->data[(int32_T)(1.0 + (real_T)c_k) - 1] + x->size[0] *
             (c->data[(int32_T)(1.0 + (real_T)c_k) - 1] - 1)) - 1] = v->data
      [(int32_T)(1.0 + (real_T)c_k) - 1];
  }

  emxFree_real_T(&v);
  emxFree_int32_T(&c);
  emxFree_real_T(&r);
  *mc = *mn;

  /*  delete mc output for legacy code compatibility */
  nd2 = b4 + 1;
  if ((b4 > 0) && (nd2 <= 0)) {
    nd2 = MAX_int32_T;
  }

  *mn = nd2;
}

/* End of code generation (a_melbankm.c) */
