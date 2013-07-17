/*
 * fft.c
 *
 * Code generation for function 'fft'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "fft.h"
#include "a_melcepst_emxutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void fft(const emxArray_creal_T *x, emxArray_creal_T *y)
{
  uint32_T n1;
  int32_T iDelta2;
  int32_T nd2;
  emxArray_real_T *costab1q;
  int32_T nRowsM1;
  int32_T ixDelta;
  int32_T nRowsD2;
  int32_T nRowsD4;
  int32_T lastChan;
  real_T e;
  int32_T c_k;
  emxArray_real_T *costab;
  emxArray_real_T *sintab;
  int32_T n;
  int32_T n2;
  int32_T ix;
  int32_T chanStart;
  uint32_T ju;
  int32_T i;
  uint32_T b_n;
  boolean_T tst;
  real_T temp_re;
  real_T temp_im;
  int32_T iheight;
  int32_T ihi;
  real_T twid_im;
  n1 = (uint32_T)x->size[0];
  iDelta2 = y->size[0];
  y->size[0] = (int32_T)n1;
  emxEnsureCapacity((emxArray__common *)y, iDelta2, (int32_T)sizeof(creal_T));
  if ((int32_T)n1 > x->size[0]) {
    nd2 = y->size[0];
    iDelta2 = y->size[0];
    y->size[0] = nd2;
    emxEnsureCapacity((emxArray__common *)y, iDelta2, (int32_T)sizeof(creal_T));
    nd2--;
    for (iDelta2 = 0; iDelta2 <= nd2; iDelta2++) {
      y->data[iDelta2].re = 0.0;
      y->data[iDelta2].im = 0.0;
    }
  }

  if (x->size[0] == 0) {
  } else {
    if (x->size[0] > (int32_T)n1) {
      nd2 = (int32_T)n1;
    } else {
      nd2 = x->size[0];
    }

    emxInit_real_T(&costab1q, 2);
    nRowsM1 = nd2 - 1;
    ixDelta = x->size[0] - nRowsM1;
    if (1 >= ixDelta) {
      ixDelta = 1;
    }

    iDelta2 = (int32_T)n1;
    nRowsD2 = (iDelta2 + (iDelta2 < 0)) >> 1;
    nRowsD4 = (nRowsD2 + (nRowsD2 < 0)) >> 1;
    lastChan = (int32_T)n1 * (x->size[0] / x->size[0] - 1);
    e = 6.2831853071795862 / (real_T)(int32_T)n1;
    iDelta2 = costab1q->size[0] * costab1q->size[1];
    costab1q->size[0] = 1;
    costab1q->size[1] = nRowsD4 + 1;
    emxEnsureCapacity((emxArray__common *)costab1q, iDelta2, (int32_T)sizeof
                      (real_T));
    costab1q->data[0] = 1.0;
    nd2 = (nRowsD4 + (nRowsD4 < 0)) >> 1;
    for (c_k = 1; c_k <= nd2; c_k++) {
      costab1q->data[c_k] = cos(e * (real_T)c_k);
    }

    for (c_k = nd2 + 1; c_k <= nRowsD4 - 1; c_k++) {
      costab1q->data[c_k] = sin(e * (real_T)(nRowsD4 - c_k));
    }

    emxInit_real_T(&costab, 2);
    emxInit_real_T(&sintab, 2);
    costab1q->data[nRowsD4] = 0.0;
    n = costab1q->size[1] - 1;
    n2 = n << 1;
    nd2 = n2 + 1;
    iDelta2 = costab->size[0] * costab->size[1];
    costab->size[0] = 1;
    costab->size[1] = nd2;
    emxEnsureCapacity((emxArray__common *)costab, iDelta2, (int32_T)sizeof
                      (real_T));
    iDelta2 = sintab->size[0] * sintab->size[1];
    sintab->size[0] = 1;
    sintab->size[1] = nd2;
    emxEnsureCapacity((emxArray__common *)sintab, iDelta2, (int32_T)sizeof
                      (real_T));
    costab->data[0] = 1.0;
    sintab->data[0] = 0.0;
    for (c_k = 1; c_k <= n; c_k++) {
      costab->data[c_k] = costab1q->data[c_k];
      sintab->data[c_k] = -costab1q->data[n - c_k];
    }

    for (c_k = n + 1; c_k <= n2; c_k++) {
      costab->data[c_k] = -costab1q->data[n2 - c_k];
      sintab->data[c_k] = -costab1q->data[c_k - n];
    }

    emxFree_real_T(&costab1q);
    ix = 0;
    chanStart = 0;
    while (((int32_T)n1 > 0) && (chanStart <= lastChan)) {
      ju = 0U;
      nd2 = chanStart;
      for (i = 1; i <= nRowsM1; i++) {
        y->data[nd2] = x->data[ix];
        b_n = n1;
        tst = TRUE;
        while (tst) {
          b_n >>= 1U;
          ju ^= b_n;
          tst = ((int32_T)(ju & b_n) == 0);
        }

        nd2 = chanStart + (int32_T)ju;
        ix++;
      }

      y->data[nd2] = x->data[ix];
      ix += ixDelta;
      nd2 = (chanStart + (int32_T)n1) - 2;
      if ((int32_T)n1 > 1) {
        for (i = chanStart; i <= nd2; i += 2) {
          temp_re = y->data[i + 1].re;
          temp_im = y->data[i + 1].im;
          y->data[i + 1].re = y->data[i].re - y->data[i + 1].re;
          y->data[i + 1].im = y->data[i].im - y->data[i + 1].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
        }
      }

      n2 = 2;
      iDelta2 = 4;
      c_k = nRowsD4;
      iheight = 1 + ((nRowsD4 - 1) << 2);
      while (c_k > 0) {
        i = chanStart;
        ihi = chanStart + iheight;
        while (i < ihi) {
          nd2 = i + n2;
          temp_re = y->data[nd2].re;
          temp_im = y->data[nd2].im;
          y->data[i + n2].re = y->data[i].re - y->data[nd2].re;
          y->data[i + n2].im = y->data[i].im - y->data[nd2].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
          i += iDelta2;
        }

        nd2 = chanStart + 1;
        for (n = c_k; n < nRowsD2; n += c_k) {
          e = costab->data[n];
          twid_im = sintab->data[n];
          i = nd2;
          ihi = nd2 + iheight;
          while (i < ihi) {
            temp_re = e * y->data[i + n2].re - twid_im * y->data[i + n2].im;
            temp_im = e * y->data[i + n2].im + twid_im * y->data[i + n2].re;
            y->data[i + n2].re = y->data[i].re - temp_re;
            y->data[i + n2].im = y->data[i].im - temp_im;
            y->data[i].re += temp_re;
            y->data[i].im += temp_im;
            i += iDelta2;
          }

          nd2++;
        }

        c_k >>= 1;
        n2 = iDelta2;
        iDelta2 <<= 1;
        iheight -= n2;
      }

      chanStart += (int32_T)n1;
    }

    emxFree_real_T(&sintab);
    emxFree_real_T(&costab);
  }
}

/* End of code generation (fft.c) */
