/*
 * mel2frq.c
 *
 * Code generation for function 'mel2frq'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "mel2frq.h"
#include "a_melcepst_data.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */
void mel2frq(const real_T mel[4], real_T frq[4])
{
  int32_T i2;
  real_T u;

  /* MEL2FRQ  Convert Mel frequency scale to Hertz FRQ=(MEL) */
  /* 	frq = mel2frq(mel) converts a vector of Mel frequencies */
  /* 	to the corresponding real frequencies. */
  /*    mr gives the corresponding gradients in Hz/mel. */
  /* 	The Mel scale corresponds to the perceived pitch of a tone */
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
  /*       Version: $Id: mel2frq.m 713 2011-10-16 14:45:43Z dmb $ */
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
  for (i2 = 0; i2 < 4; i2++) {
    u = mel[i2];
    if (u < 0.0) {
      u = -1.0;
    } else if (u > 0.0) {
      u = 1.0;
    } else {
      if (u == 0.0) {
        u = 0.0;
      }
    }

    frq[i2] = 700.0 * u * (exp(fabs(mel[i2]) / b_k) - 1.0);
  }
}

/* End of code generation (mel2frq.c) */
