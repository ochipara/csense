/*
 * EstimateNoiseW.c
 *
 * Code generation for function 'EstimateNoiseW'
 *
 * C source code generated on: Thu Aug 30 12:36:12 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "EstimateNoiseW.h"
#include "EstimateNoiseW_emxutil.h"
#include "power.h"
#include "rdivide.h"
#include "any.h"
#include "sum.h"
#include "EstimateNoiseW_rtwutil.h"
#include "EstimateNoiseW_data.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
static void eml_li_find(const boolean_T x[257], int32_T y_data[257], int32_T
  y_size[2]);
static void mhvals(real_T d, real_T *m, real_T *h);
static real_T rt_remd_snf(real_T u0, real_T u1);
static real_T rt_roundd_snf(real_T u);

/* Function Definitions */
static void eml_li_find(const boolean_T x[257], int32_T y_data[257], int32_T
  y_size[2])
{
  int32_T k;
  int32_T i;
  k = 0;
  for (i = 0; i < 257; i++) {
    if (x[i]) {
      k++;
    }
  }

  y_size[0] = 1;
  y_size[1] = k;
  k = 0;
  for (i = 0; i < 257; i++) {
    if (x[i]) {
      y_data[k] = i + 1;
      k++;
    }
  }
}

static void mhvals(real_T d, real_T *m, real_T *h)
{
  int32_T idx;
  int8_T ii_data[18];
  int32_T i;
  boolean_T exitg1;
  boolean_T guard1 = FALSE;
  int8_T b_ii_data[18];
  int32_T j;
  int8_T i_data[18];
  real_T qj;
  real_T qi;
  real_T q;

  /* x = x'; */
  /*  end */
  /*  Values are taken from Table 5 in [2] */
  /* [2] R. Martin,"Bias compensation methods for minimum statistics noise power */
  /*                spectral density estimation", Signal Processing Vol 86, pp1215-1229, 2006. */
  /*  approx: plot(d.^(-0.5),[m 1-d.^(-0.5)],'x-'), plot(d.^0.5,h,'x-') */
  idx = 0;
  i = 1;
  exitg1 = FALSE;
  while ((exitg1 == 0U) && (i < 19)) {
    guard1 = FALSE;
    if (d <= dmh[i - 1]) {
      idx++;
      ii_data[idx - 1] = (int8_T)i;
      if (idx >= 18) {
        exitg1 = TRUE;
      } else {
        guard1 = TRUE;
      }
    } else {
      guard1 = TRUE;
    }

    if (guard1 == TRUE) {
      i++;
    }
  }

  if (1 > idx) {
    idx = 0;
  }

  i = idx - 1;
  for (j = 0; j <= i; j++) {
    b_ii_data[j] = ii_data[j];
  }

  i = idx - 1;
  for (j = 0; j <= i; j++) {
    ii_data[j] = b_ii_data[j];
  }

  i = idx - 1;
  for (j = 0; j <= i; j++) {
    i_data[j] = ii_data[j];
  }

  if (idx == 0) {
    i = 17;
    j = 17;
  } else {
    i = i_data[0] - 1;
    j = i_data[0] - 2;
  }

  if (d == dmh[i]) {
    *m = dmh[18 + i];
    *h = dmh[36 + i];
  } else {
    qj = sqrt(dmh[i - 1]);

    /*  interpolate using sqrt(d) */
    qi = sqrt(dmh[i]);
    q = sqrt(d);
    *h = dmh[36 + i] + (q - qi) * (dmh[36 + j] - dmh[36 + i]) / (qj - qi);
    *m = dmh[18 + i] + (qi * qj / q - qj) * (dmh[18 + j] - dmh[18 + i]) / (qi -
      qj);
  }
}

static real_T rt_remd_snf(real_T u0, real_T u1)
{
  real_T y;
  boolean_T b_y;
  boolean_T c_y;
  real_T tr;
  if (u1 < 0.0) {
    y = ceil(u1);
  } else {
    y = floor(u1);
  }

  b_y = ((!rtIsNaN(u0)) && (!rtIsInf(u0)));
  c_y = ((!rtIsNaN(u1)) && (!rtIsInf(u1)));
  if ((u1 != 0.0) && (u1 != y) && (b_y && c_y)) {
    tr = u0 / u1;
    if (fabs(tr - rt_roundd_snf(tr)) <= DBL_EPSILON * fabs(tr)) {
      y = 0.0;
    } else {
      y = fmod(u0, u1);
    }
  } else {
    y = fmod(u0, u1);
  }

  return y;
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

void EstimateNoiseW(const real_T yf[257], LocalState *local, real_T x[257])
{
  static real_T qith[4] = { 0.03, 0.05, 0.06, 0.0 };

  real_T nu;
  real_T aca;
  real_T bmax;
  real_T nv;
  real_T nd;
  real_T bc;
  real_T md;
  real_T mv;
  real_T nrcum;
  real_T ac;
  real_T pb2[257];
  real_T p[257];
  real_T pb[257];
  real_T pminu[257];
  real_T actmin[257];
  real_T actminsub[257];
  int32_T iy;
  real_T subwc;
  real_T actbuf[2056];
  real_T ibuf;
  boolean_T lminflag[257];
  real_T qiav;
  real_T y;
  real_T ah[257];
  real_T b_ah[257];
  real_T b[257];
  int32_T ix;
  real_T qeqi[257];
  boolean_T kmod[257];
  int32_T tmp_size[2];
  int32_T tmp_data[257];
  int32_T b_size[2];
  int32_T ia;
  real_T b_data[257];
  real_T b_tmp_data[257];
  emxArray_real_T *r0;
  int32_T idx;
  int32_T ixstop;
  int32_T tmp_size_idx_0;
  boolean_T exitg3;
  int8_T ii_data[4];
  int8_T ii_size[2];
  boolean_T exitg2;
  boolean_T guard1 = FALSE;
  int32_T c_tmp_data[4];
  int8_T b_ii_data[4];
  int8_T i_data[4];
  real_T a[4];
  static const real_T b_a[4] = { 47.0, 31.4, 15.7, 4.1 };

  real_T dv1[4];
  boolean_T b_y;
  boolean_T exitg1;
  boolean_T b1;
  int32_T d_tmp_data[257];
  int32_T e_tmp_data[257];
  int32_T b_mv[2];
  qith[3U] = rtInf;

  /* !codegen */
  /* ESTNOISEM - estimate noise spectrum using minimum statistics */
  /*  Inputs: */
  /*    yf      input power spectra (one row per frame) */
  /*    tz      frame increment in seconds */
  /*            Alternatively, the input state from a previous call (see below) */
  /*    pp      algorithm parameters [optional] */
  /*  */
  /*  Outputs: */
  /*    x       estimated noise power spectra (one row per frame) */
  /*    zo      output state */
  /*    xs      estimated std error of x (one row per frame) */
  /*            xs seems often to be an underestimate by a factor of 2 or 3 */
  /*  */
  /*  The algorithm parameters are defined in reference [1] from which equation */
  /*  numbers are given in parentheses. They are as follows: */
  /*  */
  /*         pp.taca      % (11): smoothing time constant for alpha_c [0.0449 seconds] */
  /*         pp.tamax     % (3): max smoothing time constant [0.392 seconds] */
  /*         pp.taminh    % (3): min smoothing time constant (upper limit) [0.0133 seconds] */
  /*         pp.tpfall    % (12): time constant for P to fall [0.064 seconds] */
  /*         pp.tbmax     % (20): max smoothing time constant [0.0717 seconds] */
  /*         pp.qeqmin    % (23): minimum value of Qeq [2] */
  /*         pp.qeqmax    % max value of Qeq per frame [14] */
  /*         pp.av        % (23)+13 lines: fudge factor for bc calculation  [2.12] */
  /*         pp.td        % time to take minimum over [1.536 seconds] */
  /*         pp.nu        % number of subwindows to use [3] */
  /*         pp.qith      % Q-inverse thresholds to select maximum noise slope [0.03 0.05 0.06 Inf ] */
  /*         pp.nsmdb     % corresponding noise slope thresholds in dB/second   [47 31.4 15.7 4.1] */
  /*  */
  /*  Example use:      y=enframe(s,w,ni);                  % divide speech signal s(n) into */
  /*                                                        % overlapping frames using window w(n) */
  /*                    yf=rfft(y,nf,2);                    % take fourier transform */
  /*                    dp=estnoisem(yf.*conj(yf),tinc);    % estimate the noise */
  /*  */
  /*  If convenient, you can call estnoisem in chunks of arbitrary size. Thus the following are equivalent: */
  /*  */
  /*                    (a) dp=estnoisem(yp(1:300),tinc); */
  /*  */
  /*                    (b) [dp(1:100),z]=estnoisem(yp(1:100),tinc); */
  /*                        [dp(101:200),z]=estnoisem(yp(101:200),z); */
  /*                        [dp(201:300),z]=estnoisem(yp(201:300),z); */
  /*  This is intended to be a precise implementation of [1] with Table III */
  /*  replaced by the updated table 5 from [2]. The only deliberate algorithm */
  /*  change is the introduction of a minimum value for 1/Qeq in equation (23). */
  /*  This change only affects the first few frames and improves the */
  /*  convergence of the algorithm. A minor improveemnt was reported in [3] but */
  /*  this has not yet been included. */
  /*  */
  /*  Refs: */
  /*     [1] Rainer Martin. */
  /*         Noise power spectral density estimation based on optimal smoothing and minimum statistics. */
  /*         IEEE Trans. Speech and Audio Processing, 9(5):504-512, July 2001. */
  /*     [2] Rainer Martin. */
  /*         Bias compensation methods for minimum statistics noise power spectral density estimation */
  /*         Signal Processing, 2006, 86, 1215-1229 */
  /*     [3] Dirk Mauler and Rainer Martin */
  /*         Noise power spectral density estimation on highly correlated data */
  /*         Proc IWAENC, 2006 */
  /* 	   Copyright (C) Mike Brookes 2008 */
  /*       Version: $Id: estnoisem.m 713 2011-10-16 14:45:43Z dmb $ */
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
  /*  number of frames and freq bins */
  /* assert(isstruct(local) == false); */
  /*  if isstruct(local) == false */
  /*      tinc = local; */
  /*      local = struct('p', zeros(1, nr), ... */
  /*          'ac', 0, ... */
  /*          'sn2', zeros(1, nr), ... */
  /*          'pb', zeros(1, nr), ... */
  /*          'pb2', zeros(1, nr), ... */
  /*          'pminu', zeros(1, nr), ... */
  /*          'actmin',  Inf(1,nr), ... */
  /*          'actminsub', Inf(1,nr), ... */
  /*          'subwc', 0, ... */
  /*          'actbuf', Inf(qq.nu,nr), ... */
  /*          'ibuf', 0, ... */
  /*          'lminflag', true(1, nr), ... */
  /*          'nrcum', 0, ... */
  /*          'tinc', tinc); */
  /*  end */
  /*  unpack parameter structure */
  /*  smoothing time constant for alpha_c = -tinc/log(0.7) in equ (11) */
  /*  max smoothing time constant in (3) = -tinc/log(0.96) */
  /*  min smoothing time constant (upper limit) in (3) = -tinc/log(0.3) */
  /*  time constant for P to fall (12) */
  /*  max smoothing time constant in (20) = -tinc/log(0.8) */
  /*  minimum value of Qeq (23) */
  /*  max value of Qeq per frame */
  /*  fudge factor for bc calculation (23 + 13 lines) */
  /*  time to take minimum over */
  nu = 8.0;

  /*  number of subwindows */
  /*  noise slope thresholds in dB/s */
  /*  maximum permitted +ve noise slope in dB/s */
  /*  derived algorithm constants */
  aca = exp(-local->tinc / 0.0449);

  /*  smoothing constant for alpha_c in equ (11) = 0.7 */
  /*  min value of alpha_c = 0.7 in equ (11) also = 0.7 */
  /*  max smoothing constant in (3) = 0.96 */
  /*  min smoothing constant (upper limit) in (3) = 0.3 */
  bmax = exp(-local->tinc / 0.0717);

  /*  max smoothing constant in (20) = 0.8 */
  nv = rt_roundd_snf(1.536 / (local->tinc * 8.0));

  /*  length of each subwindow in frames */
  if (nv < 4.0) {
    /*  algorithm doesn't work for miniscule frames */
    nv = 4.0;
    nu = rt_roundd_snf(1.536 / (local->tinc * 4.0));
    if (nu >= 1.0) {
    } else {
      nu = 1.0;
    }
  }

  nd = nu * nv;

  /*  length of total window in frames */
  mhvals(nd, &md, &bc);

  /*  calculate the constants M(D) and H(D) from Table III */
  mhvals(nv, &mv, &bc);

  /*  calculate the constants M(D) and H(D) from Table III */
  /*  [8 4 2 1.2] in paper */
  /*  maximum value of Qeq inverse (23) */
  /*  minumum value of Qeq per frame inverse */
  nrcum = local->nrcum;
  if (local->nrcum == 0.0) {
    /*  initialize values for first frame */
    /*  smoothed power spectrum */
    ac = 1.0;

    /*  smoothed noisy speech power (20) */
    b_power(yf, pb2);
    for (iy = 0; iy < 257; iy++) {
      p[iy] = yf[iy];

      /*  correction factor (9) */
      x[iy] = yf[iy];

      /*  estimated noise power */
      pb[iy] = yf[iy];
      pminu[iy] = yf[iy];
      actmin[iy] = rtInf;

      /*  Running minimum estimate */
      actminsub[iy] = rtInf;
    }

    /*  sub-window minimum estimate */
    subwc = nv;

    /*  force a buffer switch on first loop */
    for (iy = 0; iy < 2056; iy++) {
      actbuf[iy] = rtInf;
    }

    /*  buffer to store subwindow minima */
    ibuf = 0.0;
    for (iy = 0; iy < 257; iy++) {
      lminflag[iy] = FALSE;
    }

    /*  flag to remember local minimum */
  } else {
    for (iy = 0; iy < 257; iy++) {
      p[iy] = local->p[iy];
    }

    /*  smoothed power spectrum */
    ac = local->ac;

    /*  correction factor (9) */
    for (iy = 0; iy < 257; iy++) {
      x[iy] = local->sn2[iy];
    }

    /*  estimated noise power */
    for (iy = 0; iy < 257; iy++) {
      pb[iy] = local->pb[iy];
    }

    /*  smoothed noisy speech power (20) */
    for (iy = 0; iy < 257; iy++) {
      pb2[iy] = local->pb2[iy];
    }

    for (iy = 0; iy < 257; iy++) {
      pminu[iy] = local->pminu[iy];
    }

    for (iy = 0; iy < 257; iy++) {
      actmin[iy] = local->actmin[iy];
    }

    /*  Running minimum estimate */
    for (iy = 0; iy < 257; iy++) {
      actminsub[iy] = local->actminsub[iy];
    }

    /*  sub-window minimum estimate */
    subwc = local->subwc;

    /*  force a buffer switch on first loop */
    for (iy = 0; iy < 2056; iy++) {
      actbuf[iy] = local->actbuf[iy];
    }

    /*  buffer to store subwindow minima */
    ibuf = local->ibuf;
    for (iy = 0; iy < 257; iy++) {
      lminflag[iy] = local->lminflag[iy];
    }

    /*  flag to remember local minimum */
  }

  /*  loop for each frame */
  /*     for t=1:nr              % we use t instead of lambda in the paper */
  /* (t,:);        % noise speech power spectrum */
  /*  alpha_c-bar(t)  (9) */
  qiav = rt_powd_snf(1.0 + rt_powd_snf(rdivide(sum(p), sum(yf)) - 1.0, 2.0),
                     -1.0);
  if ((qiav >= aca) || rtIsNaN(aca)) {
  } else {
    qiav = aca;
  }

  ac = aca * ac + (1.0 - aca) * qiav;

  /*  alpha_c(t)  (10) */
  y = exp(-local->tinc / 0.392) * ac;

  /*  alpha_hat: smoothing factor per frequency (11) */
  qiav = exp(-local->tinc / 0.0133);
  bc = rt_powd_snf(sum(p) / sum(x), -local->tinc / 0.064);
  if ((qiav <= bc) || rtIsNaN(bc)) {
    bc = qiav;
  }

  b_rdivide(p, x, ah);
  for (iy = 0; iy < 257; iy++) {
    b_ah[iy] = ah[iy] - 1.0;
  }

  b_power(b_ah, ah);
  for (iy = 0; iy < 257; iy++) {
    b_ah[iy] = 1.0 + ah[iy];
  }

  c_power(b_ah, b);

  /*  smoothed noisy speech power (3) */
  for (iy = 0; iy < 257; iy++) {
    qiav = y * b[iy];
    if ((qiav >= bc) || rtIsNaN(bc)) {
    } else {
      qiav = bc;
    }

    /*  lower limit for alpha_hat (12) */
    b_ah[iy] = qiav;
    p[iy] = qiav * p[iy] + (1.0 - qiav) * yf[iy];
  }

  b_power(b_ah, ah);
  for (ix = 0; ix < 257; ix++) {
    qiav = ah[ix];
    if ((qiav <= bmax) || rtIsNaN(bmax)) {
    } else {
      qiav = bmax;
    }

    /*  smoothing constant for estimating periodogram variance (22 + 2 lines) */
    pb[ix] = qiav * pb[ix] + (1.0 - qiav) * p[ix];
    b[ix] = qiav;
  }

  /*  smoothed periodogram (20) */
  b_power(p, ah);
  for (iy = 0; iy < 257; iy++) {
    pb2[iy] = b[iy] * pb2[iy] + (1.0 - b[iy]) * ah[iy];
  }

  /*  smoothed periodogram squared (21) */
  b_power(pb, ah);
  b_power(x, b);
  for (iy = 0; iy < 257; iy++) {
    qeqi[iy] = pb2[iy] - ah[iy];
    b_ah[iy] = 2.0 * b[iy];
  }

  b_rdivide(qeqi, b_ah, ah);
  y = 0.071428571428571425 / (local->nrcum + 1.0);
  for (ix = 0; ix < 257; ix++) {
    qiav = ah[ix];
    if (qiav <= 0.5) {
    } else {
      qiav = 0.5;
    }

    if ((qiav >= y) || rtIsNaN(y)) {
    } else {
      qiav = y;
    }

    qeqi[ix] = qiav;
  }

  /*  Qeq inverse (23) */
  qiav = sum(qeqi) / 257.0;

  /*  Average over all frequencies (23+12 lines) (ignore non-duplication of DC and nyquist terms) */
  bc = sqrt(qiav);
  y = 2.12 * bc;
  bc = 1.0 + 2.12 * bc;

  /*  bias correction factor (23+11 lines) */
  aca = 2.0 * md;
  c_power(qeqi, ah);
  for (iy = 0; iy < 257; iy++) {
    b_ah[iy] = ah[iy] - aca;
  }

  c_rdivide(2.0 * (nd - 1.0) * (1.0 - md), b_ah, b);

  /*  we use the simplified form (17) instead of (15) */
  aca = 2.0 * mv;
  for (iy = 0; iy < 257; iy++) {
    /*  same expression but for sub windows */
    kmod[iy] = (bc * p[iy] * (1.0 + b[iy]) < actmin[iy]);
    b[iy]++;
  }

  /*  Frequency mask for new minimum */
  if (any(kmod)) {
    eml_li_find(kmod, tmp_data, tmp_size);
    b_size[0] = 1;
    b_size[1] = tmp_size[1];
    ia = tmp_size[0] * tmp_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      b_data[iy] = p[tmp_data[iy] - 1];
    }

    eml_li_find(kmod, tmp_data, tmp_size);
    ia = tmp_size[0] * tmp_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      b_tmp_data[iy] = b[tmp_data[iy] - 1];
    }

    eml_li_find(kmod, tmp_data, tmp_size);
    ia = b_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      actmin[tmp_data[iy] - 1] = (1.0 + y) * b_data[iy] * b_tmp_data[iy];
    }

    eml_li_find(kmod, tmp_data, tmp_size);
    b_size[0] = 1;
    b_size[1] = tmp_size[1];
    ia = tmp_size[0] * tmp_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      b_data[iy] = p[tmp_data[iy] - 1];
    }

    eml_li_find(kmod, tmp_data, tmp_size);
    c_power(qeqi, ah);
    for (iy = 0; iy < 257; iy++) {
      b_ah[iy] = ah[iy] - aca;
    }

    c_rdivide(2.0 * (nv - 1.0) * (1.0 - mv), b_ah, ah);
    for (iy = 0; iy < 257; iy++) {
      b_ah[iy] = 1.0 + ah[iy];
    }

    ia = tmp_size[0] * tmp_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      b_tmp_data[iy] = b_ah[tmp_data[iy] - 1];
    }

    eml_li_find(kmod, tmp_data, tmp_size);
    ia = b_size[1] - 1;
    for (iy = 0; iy <= ia; iy++) {
      actminsub[tmp_data[iy] - 1] = (1.0 + y) * b_data[iy] * b_tmp_data[iy];
    }
  }

  emxInit_real_T(&r0, 2);
  if ((subwc > 1.0) && (subwc < nv)) {
    /*  middle of buffer - allow a local minimum */
    for (iy = 0; iy < 257; iy++) {
      /*  potential local minimum frequency bins */
      qiav = actminsub[iy];
      bc = pminu[iy];
      if ((qiav <= bc) || rtIsNaN(bc)) {
        bc = qiav;
      }

      x[iy] = bc;
      pminu[iy] = bc;
      lminflag[iy] = (lminflag[iy] || kmod[iy]);
    }
  } else {
    if (subwc >= nv) {
      /*  end of buffer - do a buffer switch */
      bc = rt_remd_snf(ibuf, nu);
      ibuf = 1.0 + rt_remd_snf(ibuf, nu);

      /*  increment actbuf storage pointer */
      for (iy = 0; iy < 257; iy++) {
        actbuf[((int32_T)(1.0 + bc) + (iy << 3)) - 1] = actmin[iy];
      }

      /*  save sub-window minimum */
      ix = -7;
      iy = -1;
      for (ia = 0; ia < 257; ia++) {
        ix += 8;
        idx = ix;
        ixstop = ix + 7;
        bc = actbuf[ix - 1];
        if (rtIsNaN(actbuf[ix - 1])) {
          tmp_size_idx_0 = ix;
          exitg3 = FALSE;
          while ((exitg3 == 0U) && (tmp_size_idx_0 + 1 <= ixstop)) {
            idx = tmp_size_idx_0 + 1;
            if (!rtIsNaN(actbuf[tmp_size_idx_0])) {
              bc = actbuf[tmp_size_idx_0];
              exitg3 = TRUE;
            } else {
              tmp_size_idx_0++;
            }
          }
        }

        if (idx < ixstop) {
          while (idx + 1 <= ixstop) {
            if (actbuf[idx] < bc) {
              bc = actbuf[idx];
            }

            idx++;
          }
        }

        iy++;
        pminu[iy] = bc;
      }

      idx = 0;
      for (iy = 0; iy < 2; iy++) {
        ii_size[iy] = (int8_T)(1 + 3 * iy);
      }

      ix = 1;
      exitg2 = FALSE;
      while ((exitg2 == 0U) && (ix < 5)) {
        guard1 = FALSE;
        if (qiav < qith[ix - 1]) {
          idx++;
          ii_data[idx - 1] = (int8_T)ix;
          if (idx >= 4) {
            exitg2 = TRUE;
          } else {
            guard1 = TRUE;
          }
        } else {
          guard1 = TRUE;
        }

        if (guard1 == TRUE) {
          ix++;
        }
      }

      if (1 > idx) {
        idx = 0;
      }

      ia = idx - 1;
      for (iy = 0; iy <= ia; iy++) {
        c_tmp_data[iy] = 1 + iy;
      }

      ia = idx - 1;
      for (iy = 0; iy <= ia; iy++) {
        ix = 0;
        while (ix <= 0) {
          b_ii_data[iy] = ii_data[c_tmp_data[iy] - 1];
          ix = 1;
        }
      }

      ii_size[0] = 1;
      ii_size[1] = (int8_T)idx;
      ia = idx - 1;
      for (iy = 0; iy <= ia; iy++) {
        ii_data[iy] = b_ii_data[iy];
      }

      ia = ii_size[1] - 1;
      for (iy = 0; iy <= ia; iy++) {
        i_data[iy] = ii_data[iy];
      }

      for (iy = 0; iy < 4; iy++) {
        a[iy] = b_a[iy] * nv * local->tinc / 10.0;
      }

      power(a, dv1);

      /*  noise slope max */
      for (iy = 0; iy < 257; iy++) {
        lminflag[iy] = (lminflag[iy] && (!kmod[iy]) && (actminsub[iy] <
          dv1[i_data[0] - 1] * pminu[iy]) && (actminsub[iy] > pminu[iy]));
      }

      b_y = FALSE;
      ix = 0;
      exitg1 = FALSE;
      while ((exitg1 == 0U) && (ix < 257)) {
        if ((int32_T)lminflag[ix] == 0) {
          b1 = TRUE;
        } else {
          b1 = FALSE;
        }

        if (!b1) {
          b_y = TRUE;
          exitg1 = TRUE;
        } else {
          ix++;
        }
      }

      if (b_y) {
        eml_li_find(lminflag, tmp_data, tmp_size);
        eml_li_find(lminflag, d_tmp_data, tmp_size);
        ia = tmp_size[0] * tmp_size[1] - 1;
        for (iy = 0; iy <= ia; iy++) {
          b_tmp_data[iy] = actminsub[d_tmp_data[iy] - 1];
        }

        ia = tmp_size[1] - 1;
        for (iy = 0; iy <= ia; iy++) {
          pminu[tmp_data[iy] - 1] = b_tmp_data[iy];
        }

        eml_li_find(lminflag, tmp_data, tmp_size);
        tmp_size_idx_0 = tmp_size[1];
        ia = tmp_size[1] - 1;
        for (iy = 0; iy <= ia; iy++) {
          e_tmp_data[iy] = tmp_data[iy] - 1;
        }

        eml_li_find(lminflag, tmp_data, tmp_size);
        b_size[0] = 1;
        b_size[1] = tmp_size[1];
        ia = tmp_size[0] * tmp_size[1] - 1;
        for (iy = 0; iy <= ia; iy++) {
          b_data[iy] = pminu[tmp_data[iy] - 1];
        }

        bc = rt_roundd_snf(nu);
        if (bc < 2.147483648E+9) {
          iy = (int32_T)bc;
        } else if (bc >= 2.147483648E+9) {
          iy = MAX_int32_T;
        } else {
          iy = 0;
        }

        b_mv[0] = iy;
        b_mv[1] = 1;
        for (iy = 0; iy < 2; iy++) {
          tmp_size[iy] = b_size[iy] * b_mv[iy];
        }

        iy = r0->size[0] * r0->size[1];
        r0->size[0] = tmp_size[0];
        r0->size[1] = tmp_size[1];
        emxEnsureCapacity((emxArray__common *)r0, iy, (int32_T)sizeof(real_T));
        if ((r0->size[0] == 0) || (r0->size[1] == 0)) {
        } else {
          ia = 1;
          idx = 0;
          ix = 1;
          for (iy = 1; iy <= b_size[1]; iy++) {
            for (ixstop = 1; ixstop <= b_mv[0]; ixstop++) {
              r0->data[idx] = b_data[ix - 1];
              ia = ix + 1;
              idx++;
            }

            ix = ia;
          }
        }

        ia = tmp_size_idx_0 - 1;
        for (iy = 0; iy <= ia; iy++) {
          for (ix = 0; ix < 8; ix++) {
            actbuf[ix + (e_tmp_data[iy] << 3)] = r0->data[ix + (iy << 3)];
          }
        }
      }

      for (iy = 0; iy < 257; iy++) {
        lminflag[iy] = FALSE;
        actmin[iy] = rtInf;
      }

      subwc = 0.0;
    }
  }

  emxFree_real_T(&r0);
  subwc++;

  /*  qisq=sqrt(qeqi); */
  /*  empirical formula for standard error based on Fig 15 of [2] */
  /* xs=sn2.*sqrt(0.266*(nd+100*qisq).*qisq/(1+0.005*nd+6/nd)./(0.5*qeqi.^(-1)+nd-1)); */
  /*     end */
  for (iy = 0; iy < 257; iy++) {
    local->p[iy] = p[iy];
  }

  /*  smoothed power spectrum */
  local->ac = ac;

  /*  correction factor (9) */
  for (iy = 0; iy < 257; iy++) {
    local->sn2[iy] = x[iy];
  }

  /*  estimated noise power */
  for (iy = 0; iy < 257; iy++) {
    local->pb[iy] = pb[iy];
  }

  /*  smoothed noisy speech power (20) */
  for (iy = 0; iy < 257; iy++) {
    local->pb2[iy] = pb2[iy];
  }

  for (iy = 0; iy < 257; iy++) {
    local->pminu[iy] = pminu[iy];
  }

  for (iy = 0; iy < 257; iy++) {
    local->actmin[iy] = actmin[iy];
  }

  /*  Running minimum estimate */
  for (iy = 0; iy < 257; iy++) {
    local->actminsub[iy] = actminsub[iy];
  }

  /*  sub-window minimum estimate */
  local->subwc = subwc;

  /*  force a buffer switch on first loop */
  for (iy = 0; iy < 2056; iy++) {
    local->actbuf[iy] = actbuf[iy];
  }

  /*  buffer to store subwindow minima */
  local->ibuf = ibuf;
  for (iy = 0; iy < 257; iy++) {
    local->lminflag[iy] = lminflag[iy];
  }

  /*  flag to remember local minimum */
  local->nrcum = nrcum + 1.0;
}

/* End of code generation (EstimateNoiseW.c) */
