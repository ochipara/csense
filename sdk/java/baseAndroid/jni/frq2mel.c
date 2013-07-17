/*
 * frq2mel.c
 *
 * Code generation for function 'frq2mel'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "frq2mel.h"
#include "a_melcepst_emxutil.h"
#include "a_melcepst_data.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void b_frq2mel(const emxArray_real_T *frq, emxArray_real_T *mel)
{
  uint32_T uv0[2];
  int32_T c_k;
  emxArray_real_T *af;
  int32_T loop_ub;
  real_T u;
  emxArray_real_T *r7;

  /* FRQ2ERB  Convert Hertz to Mel frequency scale MEL=(FRQ) */
  /* 	[mel,mr] = frq2mel(frq) converts a vector of frequencies (in Hz) */
  /* 	to the corresponding values on the Mel scale which corresponds */
  /* 	to the perceived pitch of a tone. */
  /*    mr gives the corresponding gradients in Hz/mel. */
  /* 	The relationship between mel and frq is given by: */
  /*  */
  /* 	m = ln(1 + f/700) * 1000 / ln(1+1000/700) */
  /*  */
  /*   	This means that m(1000) = 1000 */
  /*  */
  /* 	References: */
  /*  */
  /* 	  [1] S. S. Stevens & J. Volkman "The relation of pitch to */
  /* 		frequency", American J of Psychology, V 53, p329 1940 */
  /* 	  [2] C. G. M. Fant, "Acoustic description & classification */
  /* 		of phonetic units", Ericsson Tchnics, No 1 1959 */
  /* 		(reprinted in "Speech Sounds & Features", MIT Press 1973) */
  /* 	  [3] S. B. Davis & P. Mermelstein, "Comparison of parametric */
  /* 		representations for monosyllabic word recognition in */
  /* 		continuously spoken sentences", IEEE ASSP, V 28, */
  /* 		pp 357-366 Aug 1980 */
  /* 	  [4] J. R. Deller Jr, J. G. Proakis, J. H. L. Hansen, */
  /* 		"Discrete-Time Processing of Speech Signals", p380, */
  /* 		Macmillan 1993 */
  /* 	  [5] HTK Reference Manual p73 */
  /* 	 */
  /*       Copyright (C) Mike Brookes 1998 */
  /*       Version: $Id: frq2mel.m 713 2011-10-16 14:45:43Z dmb $ */
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
  for (c_k = 0; c_k < 2; c_k++) {
    uv0[c_k] = (uint32_T)frq->size[c_k];
  }

  emxInit_real_T(&af, 2);
  c_k = af->size[0] * af->size[1];
  af->size[0] = 1;
  af->size[1] = (int32_T)uv0[1];
  emxEnsureCapacity((emxArray__common *)af, c_k, (int32_T)sizeof(real_T));
  for (c_k = 0; c_k <= frq->size[1] - 1; c_k++) {
    af->data[c_k] = fabs(frq->data[c_k]);
  }

  c_k = mel->size[0] * mel->size[1];
  mel->size[0] = 1;
  mel->size[1] = frq->size[1];
  emxEnsureCapacity((emxArray__common *)mel, c_k, (int32_T)sizeof(real_T));
  loop_ub = frq->size[0] * frq->size[1] - 1;
  for (c_k = 0; c_k <= loop_ub; c_k++) {
    mel->data[c_k] = frq->data[c_k];
  }

  for (c_k = 0; c_k <= frq->size[1] - 1; c_k++) {
    u = mel->data[c_k];
    if (u < 0.0) {
      u = -1.0;
    } else if (u > 0.0) {
      u = 1.0;
    } else {
      if (u == 0.0) {
        u = 0.0;
      }
    }

    mel->data[c_k] = u;
  }

  c_k = af->size[0] * af->size[1];
  af->size[0] = 1;
  af->size[1] = af->size[1];
  emxEnsureCapacity((emxArray__common *)af, c_k, (int32_T)sizeof(real_T));
  c_k = af->size[0];
  loop_ub = af->size[1];
  loop_ub = c_k * loop_ub - 1;
  for (c_k = 0; c_k <= loop_ub; c_k++) {
    af->data[c_k] = 1.0 + af->data[c_k] / 700.0;
  }

  emxInit_real_T(&r7, 2);
  c_k = r7->size[0] * r7->size[1];
  r7->size[0] = 1;
  r7->size[1] = af->size[1];
  emxEnsureCapacity((emxArray__common *)r7, c_k, (int32_T)sizeof(real_T));
  loop_ub = af->size[0] * af->size[1] - 1;
  for (c_k = 0; c_k <= loop_ub; c_k++) {
    r7->data[c_k] = af->data[c_k];
  }

  for (c_k = 0; c_k <= af->size[1] - 1; c_k++) {
    r7->data[c_k] = log(r7->data[c_k]);
  }

  emxFree_real_T(&af);
  c_k = mel->size[0] * mel->size[1];
  mel->size[0] = 1;
  mel->size[1] = mel->size[1];
  emxEnsureCapacity((emxArray__common *)mel, c_k, (int32_T)sizeof(real_T));
  c_k = mel->size[0];
  loop_ub = mel->size[1];
  loop_ub = c_k * loop_ub - 1;
  for (c_k = 0; c_k <= loop_ub; c_k++) {
    mel->data[c_k] = mel->data[c_k] * r7->data[c_k] * k;
  }

  emxFree_real_T(&r7);
}

void frq2mel(const real_T frq[2], real_T mel[2])
{
  int32_T c_k;
  real_T u;

  /* FRQ2ERB  Convert Hertz to Mel frequency scale MEL=(FRQ) */
  /* 	[mel,mr] = frq2mel(frq) converts a vector of frequencies (in Hz) */
  /* 	to the corresponding values on the Mel scale which corresponds */
  /* 	to the perceived pitch of a tone. */
  /*    mr gives the corresponding gradients in Hz/mel. */
  /* 	The relationship between mel and frq is given by: */
  /*  */
  /* 	m = ln(1 + f/700) * 1000 / ln(1+1000/700) */
  /*  */
  /*   	This means that m(1000) = 1000 */
  /*  */
  /* 	References: */
  /*  */
  /* 	  [1] S. S. Stevens & J. Volkman "The relation of pitch to */
  /* 		frequency", American J of Psychology, V 53, p329 1940 */
  /* 	  [2] C. G. M. Fant, "Acoustic description & classification */
  /* 		of phonetic units", Ericsson Tchnics, No 1 1959 */
  /* 		(reprinted in "Speech Sounds & Features", MIT Press 1973) */
  /* 	  [3] S. B. Davis & P. Mermelstein, "Comparison of parametric */
  /* 		representations for monosyllabic word recognition in */
  /* 		continuously spoken sentences", IEEE ASSP, V 28, */
  /* 		pp 357-366 Aug 1980 */
  /* 	  [4] J. R. Deller Jr, J. G. Proakis, J. H. L. Hansen, */
  /* 		"Discrete-Time Processing of Speech Signals", p380, */
  /* 		Macmillan 1993 */
  /* 	  [5] HTK Reference Manual p73 */
  /* 	 */
  /*       Copyright (C) Mike Brookes 1998 */
  /*       Version: $Id: frq2mel.m 713 2011-10-16 14:45:43Z dmb $ */
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
  for (c_k = 0; c_k < 2; c_k++) {
    u = frq[c_k];
    if (u < 0.0) {
      u = -1.0;
    } else if (u > 0.0) {
      u = 1.0;
    } else {
      if (u == 0.0) {
        u = 0.0;
      }
    }

    mel[c_k] = u * log(1.0 + fabs(frq[c_k]) / 700.0) * k;
  }
}

/* End of code generation (frq2mel.c) */
