/*
 * a_melcepst.c
 *
 * Code generation for function 'a_melcepst'
 *
 * C source code generated on: Tue Apr  3 22:33:14 2012
 *
 */

/* Include files */
#include "rt_nonfinite.h"
#include "a_melcepst.h"
#include "a_melcepst_emxutil.h"
#include "a_rdct.h"
#include "abs.h"
#include "sqrt.h"
#include "a_melbankm.h"
#include "a_rfft.h"
#include "a_melcepst_rtwutil.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */
static real_T rt_atan2d_snf(real_T u0, real_T u1);

/* Function Definitions */
static real_T rt_atan2d_snf(real_T u0, real_T u1)
{
  real_T y;
  int32_T i5;
  int32_T i6;
  if (rtIsNaN(u0) || rtIsNaN(u1)) {
    y = rtNaN;
  } else if (rtIsInf(u0) && rtIsInf(u1)) {
    if (u1 > 0.0) {
      i5 = 1;
    } else {
      i5 = -1;
    }

    if (u0 > 0.0) {
      i6 = 1;
    } else {
      i6 = -1;
    }

    y = atan2((real_T)i6, (real_T)i5);
  } else if (u1 == 0.0) {
    if (u0 > 0.0) {
      y = RT_PI / 2.0;
    } else if (u0 < 0.0) {
      y = -(RT_PI / 2.0);
    } else {
      y = 0.0;
    }
  } else {
    y = atan2(u0, u1);
  }

  return y;
}

void a_melcepst(const real_T s[512], real_T fs, int32_T nc, emxArray_real_T *c)
{
  real_T b_s[512];
  int32_T i;
  static const real_T dv0[512] = { 0.080000000000000016, 0.080034772851092173,
    0.080139086147189731, 0.080312924117550422, 0.0805562604802531,
    0.08086905844617126, 0.081251270724534919, 0.0817028395300804,
    0.082223696591786744, 0.082813763163197218, 0.083472950034324755,
    0.084201157545139238, 0.084998275600634943, 0.085864183687475115,
    0.086798750892212118, 0.0878018359210796, 0.0888732871213544,
    0.0900129425042841, 0.091220629769577732, 0.092496166331455187,
    0.093839359346251483, 0.095250005741572386, 0.09672789224699585,
    0.09827279542631584, 0.099884481711322914, 0.10156270743711604,
    0.10330721887894206, 0.10511775229055487, 0.10699403394409035,
    0.10893578017145067, 0.11094269740719032, 0.11301448223289995,
    0.11515082142307836, 0.11735139199248851, 0.11961586124498802,
    0.12194388682382867, 0.12433511676341558, 0.12678918954252011,
    0.12930573413893637, 0.1318843700855753, 0.13452470752798562,
    0.13722634728329447, 0.13998888090055894, 0.1428118907225176,
    0.14569494994873494, 0.14863762270012759, 0.1516394640848634,
    0.15470002026562302, 0.15781882852821355, 0.16099541735152506,
    0.16422930647881784, 0.16752000699033076, 0.17086702137719906,
    0.17426984361667108, 0.177727959248612, 0.181240845453283,
    0.18480797113038444, 0.18842879697935122, 0.19210277558088723,
    0.19582935147972808, 0.19960796126861807, 0.20343803367348967,
    0.20731898963983236, 0.211250242420238, 0.21523119766310839,
    0.2192612535025138, 0.2233398006491864, 0.22746622248263659,
    0.23163989514437766, 0.23586018763224437, 0.24012646189579223,
    0.24443807293276187, 0.24879436888659412, 0.25319469114498255,
    0.25763837443944609, 0.26212474694590859, 0.26665313038626953,
    0.27122284013095055, 0.27583318530240147, 0.28048346887955239,
    0.28517298780319289, 0.2899010330822655, 0.29466688990105527,
    0.29946983772726005, 0.30430915042092521, 0.30918409634422606,
    0.31409393847208128, 0.31903793450358153, 0.32401533697421447,
    0.32902539336887182, 0.33406734623561868, 0.33914043330021065,
    0.34424388758133867, 0.34937693750658638, 0.35453880702908114,
    0.35972871574482179, 0.36494587901066489, 0.3701895080629527,
    0.37545881013676219, 0.38075298858576168, 0.38607124300265128,
    0.39141276934017522, 0.39677676003268147, 0.40216240411821519,
    0.40756888736112512, 0.41299539237516436, 0.41844109874706864,
    0.42390518316059117, 0.4293868195209769, 0.43488517907985663,
    0.44039943056054276, 0.44592874028370622, 0.45147227229341824,
    0.45702918848353491, 0.46259864872440754, 0.46817981098989864,
    0.47377183148468471, 0.47937386477182686, 0.48498506390058893,
    0.490604580534485, 0.49623156507953636, 0.50186516681271842,
    0.50750453401057793, 0.51314881407800261, 0.51879715367712187,
    0.524448698856319, 0.53010259517933778, 0.53575798785446094,
    0.54141402186374354, 0.5470698420922796, 0.55272459345748381,
    0.55837742103836852, 0.5640274702047956, 0.56967388674668551,
    0.57531581700316159, 0.58095240799161241, 0.58658280753665026,
    0.59220616439894935, 0.59782162840394082, 0.60342835057034794,
    0.60902548323854022, 0.61461218019868813, 0.62018759681869828,
    0.62575089017190988, 0.63130121916453474, 0.63683774466281806,
    0.64235962961990467, 0.64786603920238861, 0.65335614091652849,
    0.65882910473410994, 0.66428410321793319, 0.6697203116469117,
    0.67513690814075755, 0.68053307378423888, 0.68590799275098879,
    0.69126085242684687, 0.69659084353271583, 0.70189716024691284,
    0.70717900032699887, 0.7124355652310671, 0.717666060238471,
    0.72286969456997574, 0.72804568150731275, 0.73319323851212115,
    0.73831158734425673, 0.74339995417945037, 0.74845756972630173,
    0.75348366934258248, 0.75847749315084323, 0.76343828615329357,
    0.7683652983459498, 0.77325778483202323, 0.77811500593454008,
    0.78293622730816892, 0.78772072005024552, 0.7924677608109707,
    0.79717663190277332, 0.80184662140881269, 0.80647702329061177,
    0.81106713749480042, 0.8156162700589541, 0.82012373321651044,
    0.82458884550075162, 0.82901093184783137, 0.83338932369883667,
    0.83772335910086348, 0.84201238280709623, 0.84625574637587087,
    0.85045280826871128, 0.8546029339473209, 0.85870549596951617,
    0.86275987408408694, 0.86676545532457061, 0.8707216341019236,
    0.87462781229607822, 0.87848339934637087, 0.88228781234082576,
    0.88604047610428438, 0.88974082328536275, 0.89338829444222823,
    0.89698233812717909, 0.90052241097001584, 0.90400797776019148,
    0.90743851152772792, 0.91081349362288644, 0.91413241379458121,
    0.91739477026752081, 0.92060006981807141, 0.92374782784882448,
    0.92683756846186127, 0.9298688245307023, 0.93284113777093092,
    0.93575405880947859, 0.938607147252565, 0.94139997175227874,
    0.94413211007179187, 0.94680314914919594, 0.94941268515995136,
    0.95196032357793992, 0.95444567923511281, 0.95686837637972111,
    0.9592280487331255, 0.96152433954517225, 0.96375690164812866,
    0.965925397509171, 0.96802949928141335, 0.970068888853475,
    0.97204325789757351, 0.97395230791614062, 0.97579575028695,
    0.97757330630675354, 0.97928470723341743, 0.98092969432655219,
    0.98250801888663064, 0.98401944229258809, 0.98546373603789827,
    0.98684068176512052, 0.98815007129891252, 0.98939170667750365,
    0.99056540018262351, 0.99167097436788332, 0.99270826208560237,
    0.99367710651207919, 0.99457736117130091, 0.99540888995708832,
    0.9961715671536735, 0.99686527745470577, 0.99748991598068559,
    0.99804538829481926, 0.9985316104172981, 0.99894850883799369,
    0.99929602052757294, 0.99957409294702582, 0.99978268405560977,
    0.99992176231720475, 0.99999130670508207, 0.99999130670508207,
    0.99992176231720475, 0.99978268405560977, 0.99957409294702582,
    0.99929602052757294, 0.99894850883799369, 0.9985316104172981,
    0.99804538829481926, 0.99748991598068559, 0.99686527745470577,
    0.9961715671536735, 0.99540888995708832, 0.99457736117130091,
    0.99367710651207919, 0.99270826208560237, 0.99167097436788332,
    0.99056540018262351, 0.98939170667750365, 0.98815007129891264,
    0.98684068176512052, 0.98546373603789827, 0.9840194422925882,
    0.98250801888663064, 0.98092969432655219, 0.97928470723341743,
    0.97757330630675365, 0.97579575028695009, 0.97395230791614062,
    0.97204325789757351, 0.970068888853475, 0.96802949928141335,
    0.96592539750917106, 0.96375690164812866, 0.96152433954517225,
    0.9592280487331255, 0.95686837637972122, 0.95444567923511281,
    0.95196032357794014, 0.94941268515995125, 0.94680314914919594,
    0.94413211007179187, 0.94139997175227885, 0.938607147252565,
    0.93575405880947871, 0.93284113777093114, 0.92986882453070241,
    0.92683756846186127, 0.92374782784882448, 0.92060006981807163,
    0.91739477026752092, 0.91413241379458121, 0.91081349362288655,
    0.90743851152772792, 0.90400797776019148, 0.900522410970016,
    0.89698233812717909, 0.89338829444222834, 0.88974082328536275,
    0.8860404761042846, 0.88228781234082587, 0.878483399346371,
    0.87462781229607822, 0.87072163410192371, 0.86676545532457072,
    0.86275987408408716, 0.85870549596951617, 0.854602933947321,
    0.85045280826871128, 0.84625574637587087, 0.84201238280709623,
    0.83772335910086393, 0.83338932369883678, 0.82901093184783159,
    0.82458884550075162, 0.82012373321651089, 0.81561627005895421,
    0.81106713749480042, 0.80647702329061177, 0.80184662140881269,
    0.79717663190277355, 0.79246776081097081, 0.78772072005024563,
    0.78293622730816925, 0.7781150059345403, 0.77325778483202334,
    0.76836529834594991, 0.7634382861532939, 0.75847749315084312,
    0.7534836693425826, 0.7484575697263014, 0.74339995417945093,
    0.73831158734425684, 0.73319323851212148, 0.72804568150731264,
    0.72286969456997607, 0.717666060238471, 0.71243556523106721,
    0.70717900032699887, 0.701897160246913, 0.696590843532716,
    0.69126085242684687, 0.68590799275098913, 0.680533073784239,
    0.67513690814075789, 0.66972031164691181, 0.66428410321793374,
    0.65882910473411, 0.65335614091652838, 0.6478660392023885,
    0.64235962961990478, 0.63683774466281828, 0.63130121916453463,
    0.62575089017190988, 0.62018759681869828, 0.61461218019868846,
    0.60902548323854022, 0.603428350570348, 0.597821628403941,
    0.59220616439894924, 0.58658280753665026, 0.58095240799161219,
    0.57531581700316192, 0.56967388674668551, 0.56402747020479571,
    0.55837742103836829, 0.55272459345748415, 0.54706984209227971,
    0.54141402186374377, 0.53575798785446127, 0.53010259517933778,
    0.52444869885631917, 0.51879715367712176, 0.51314881407800306,
    0.50750453401057793, 0.50186516681271853, 0.49623156507953631,
    0.49060458053448541, 0.48498506390058904, 0.47937386477182709,
    0.47377183148468466, 0.46817981098989869, 0.46259864872440776,
    0.45702918848353485, 0.45147227229341824, 0.44592874028370633,
    0.440399430560543, 0.43488517907985663, 0.429386819520977,
    0.42390518316059139, 0.41844109874706892, 0.41299539237516436,
    0.40756888736112484, 0.40216240411821547, 0.39677676003268147,
    0.39141276934017533, 0.3860712430026515, 0.3807529885857619,
    0.3754588101367623, 0.37018950806295287, 0.36494587901066522,
    0.35972871574482168, 0.35453880702908119, 0.34937693750658616,
    0.34424388758133895, 0.33914043330021071, 0.33406734623561879,
    0.3290253933688716, 0.32401533697421481, 0.31903793450358164,
    0.31409393847208145, 0.30918409634422594, 0.30430915042092521,
    0.29946983772726016, 0.29466688990105516, 0.2899010330822655,
    0.285172987803193, 0.28048346887955261, 0.27583318530240147,
    0.2712228401309506, 0.2666531303862697, 0.26212474694590882,
    0.25763837443944609, 0.25319469114498266, 0.24879436888659429,
    0.24443807293276176, 0.24012646189579229, 0.23586018763224448,
    0.23163989514437777, 0.22746622248263659, 0.22333980064918652,
    0.21926125350251396, 0.21523119766310861, 0.21125024242023804,
    0.20731898963983225, 0.20343803367348989, 0.19960796126861807,
    0.19582935147972819, 0.19210277558088712, 0.18842879697935144,
    0.1848079711303845, 0.18124084545328312, 0.17772795924861196,
    0.17426984361667136, 0.17086702137719911, 0.16752000699033065,
    0.16422930647881784, 0.16099541735152512, 0.15781882852821361,
    0.15470002026562296, 0.15163946408486367, 0.14863762270012765,
    0.14569494994873505, 0.1428118907225176, 0.13998888090055922,
    0.13722634728329458, 0.13452470752798557, 0.1318843700855753,
    0.12930573413893648, 0.12678918954252016, 0.12433511676341558,
    0.12194388682382873, 0.11961586124498813, 0.11735139199248862,
    0.11515082142307836, 0.1130144822329, 0.11094269740719043,
    0.10893578017145061, 0.10699403394409041, 0.10511775229055476,
    0.10330721887894218, 0.10156270743711604, 0.09988448171132297,
    0.09827279542631584, 0.0967278922469959, 0.095250005741572386,
    0.093839359346251427, 0.092496166331455243, 0.091220629769577788,
    0.0900129425042841, 0.088873287121354339, 0.087801835921079707,
    0.086798750892212118, 0.085864183687475171, 0.084998275600634943,
    0.084201157545139349, 0.083472950034324811, 0.082813763163197218,
    0.082223696591786744, 0.081702839530080451, 0.081251270724534919,
    0.08086905844617126, 0.0805562604802531, 0.080312924117550422,
    0.080139086147189731, 0.080034772851092173, 0.080000000000000016 };

  emxArray_creal_T *f;
  emxArray_real_T *m;
  int32_T b;
  int32_T a;
  int32_T i0;
  int32_T i1;
  emxArray_creal_T *pw;
  int32_T j;
  real_T absxk;
  real_T r;
  real_T x;
  real_T y;
  int32_T ixstart;
  creal_T ath;
  boolean_T exitg1;
  int32_T ia;
  boolean_T p;
  int32_T ic;
  int32_T b_m;
  emxArray_creal_T *b_f;
  emxArray_real_T *b_b;
  emxArray_real_T *b_y;
  int32_T c_k;
  uint32_T unnamed_idx_0;
  int32_T exponent;
  int32_T b_exponent;
  int32_T c_exponent;
  emxArray_int32_T *r0;
  emxArray_int32_T *idx;
  emxArray_boolean_T *c_b;
  emxArray_real_T *b_c;
  emxArray_real_T *c_c;

  /* MELCEPST Calculate the mel cepstrum of a signal C=(S,FS,W,NC,P,N,INC,FL,FH) */
  /*  */
  /*  */
  /*  Simple use: c=melcepst(s,fs)	% calculate mel cepstrum with 12 coefs, 256 sample frames */
  /* 				  c=melcepst(s,fs,'e0dD') % include log energy, 0th cepstral coef, delta and delta-delta coefs */
  /*  */
  /*  Inputs: */
  /*      s	 speech signal */
  /*      fs  sample rate in Hz (default 11025) */
  /*      nc  number of cepstral coefficients excluding 0'th coefficient (default 12) */
  /*      n   length of frame in samples (default power of 2 < (0.03*fs)) */
  /*      p   number of filters in filterbank (default: floor(3*log(fs)) = approx 2.1 per ocatave) */
  /*      inc frame increment (default n/2) */
  /*      fl  low end of the lowest filter as a fraction of fs (default = 0) */
  /*      fh  high end of highest filter as a fraction of fs (default = 0.5) */
  /*  */
  /* 		w   any sensible combination of the following: */
  /*  */
  /* 				'R'  rectangular window in time domain */
  /* 				'N'	Hanning window in time domain */
  /* 				'M'	Hamming window in time domain (default) */
  /*  */
  /* 		      't'  triangular shaped filters in mel domain (default) */
  /* 		      'n'  hanning shaped filters in mel domain */
  /* 		      'm'  hamming shaped filters in mel domain */
  /*  */
  /* 				'p'	filters act in the power domain */
  /* 				'a'	filters act in the absolute magnitude domain (default) */
  /*  */
  /* 			   '0'  include 0'th order cepstral coefficient */
  /* 				'E'  include log energy */
  /* 				'd'	include delta coefficients (dc/dt) */
  /* 				'D'	include delta-delta coefficients (d^2c/dt^2) */
  /*  */
  /* 		      'z'  highest and lowest filters taper down to zero (default) */
  /* 		      'y'  lowest filter remains at 1 down to 0 frequency and */
  /* 			   	  highest filter remains at 1 up to nyquist freqency */
  /*  */
  /* 		       If 'ty' or 'ny' is specified, the total power in the fft is preserved. */
  /*  */
  /*  Outputs:	c     mel cepstrum output: one frame per row. Log energy, if requested, is the */
  /*                  first element of each row followed by the delta and then the delta-delta */
  /*                  coefficients. */
  /*  */
  /*  BUGS: (1) should have power limit as 1e-16 rather than 1e-6 (or possibly a better way of choosing this) */
  /*            and put into VOICEBOX */
  /*        (2) get rdct to change the data length (properly) instead of doing it explicitly (wrongly) */
  /*       Copyright (C) Mike Brookes 1997 */
  /*       Version: $Id: melcepst.m,v 1.8 2011/09/02 16:24:14 dmb Exp $ */
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
  /* floor(3*log(fs)); */
  /* 256; %20 / 1000 * fs;  % 10 ms window */
  /* nc = 20; */
  /* z=a_enframe(s,a_hamming(n),inc); */
  /*  HAMMING.M */
  /*  */
  /*  COPYRIGHT : (c) NUHAG, Dept.Math., University of Vienna, AUSTRIA */
  /*              http://nuhag.eu/ */
  /*              Permission is granted to modify and re-distribute this */
  /*              code in any manner as long as this notice is preserved. */
  /*              All standard disclaimers apply. */
  /*  */
  /*  HAMMING.M	- returns the N-point Hamming window. */
  /*  */
  /*  Input		: n = number */
  /*  */
  /*  Output	: w = vector */
  /*  */
  /*  Usage		: w = hamming (n) */
  /*  */
  /*  Comments	: allows also the call:  hamming(xx), taking only format from signal xx */
  /*  */
  /*  See also	: HAMMING2 */
  /*  modification of original MATLAB (3.5)  file */
  /*  HGFei, 1990   */
  /* z=enframe(s,hamming(n),inc); */
  for (i = 0; i < 512; i++) {
    b_s[i] = s[i] * dv0[i];
  }

  emxInit_creal_T(&f, 1);
  emxInit_real_T(&m, 2);
  a_rfft(b_s, f);
  a_melbankm(m, &a, &b);

  /* [m,a,b]=melbankm(p,n,fs,fl,fh, 'M'); */
  if (a > b) {
    i0 = 0;
    i1 = 0;
  } else {
    i0 = a - 1;
    i1 = b;
  }

  if (a > b) {
    i = 0;
  } else {
    i = a - 1;
  }

  emxInit_creal_T(&pw, 1);
  j = pw->size[0];
  pw->size[0] = i1 - i0;
  emxEnsureCapacity((emxArray__common *)pw, j, (int32_T)sizeof(creal_T));
  j = (i1 - i0) - 1;
  for (i1 = 0; i1 <= j; i1++) {
    absxk = f->data[i + i1].re;
    r = -f->data[i + i1].im;
    x = f->data[i0 + i1].re;
    y = f->data[i0 + i1].im;
    pw->data[i1].re = x * absxk - y * r;
    pw->data[i1].im = x * r + y * absxk;
  }

  ixstart = 1;
  j = pw->size[0];
  ath = pw->data[0];
  if (j > 1) {
    if (rtIsNaN(pw->data[0].re) || rtIsNaN(pw->data[0].im)) {
      i = 1;
      exitg1 = FALSE;
      while ((exitg1 == 0U) && (i + 1 <= j)) {
        ixstart = i + 1;
        if (!(rtIsNaN(pw->data[i].re) || rtIsNaN(pw->data[i].im))) {
          ath = pw->data[i];
          exitg1 = TRUE;
        } else {
          i++;
        }
      }
    }

    if (ixstart < j) {
      while (ixstart + 1 <= j) {
        if ((fabs(pw->data[ixstart].re) > 8.9884656743115785E+307) || (fabs
             (pw->data[ixstart].im) > 8.9884656743115785E+307) || (fabs(ath.re) >
             8.9884656743115785E+307) || (fabs(ath.im) > 8.9884656743115785E+307))
        {
          x = rt_hypotd_snf(fabs(pw->data[ixstart].re / 2.0), fabs(pw->
            data[ixstart].im / 2.0));
          y = rt_hypotd_snf(fabs(ath.re / 2.0), fabs(ath.im / 2.0));
        } else {
          x = rt_hypotd_snf(fabs(pw->data[ixstart].re), fabs(pw->data[ixstart].
            im));
          y = rt_hypotd_snf(fabs(ath.re), fabs(ath.im));
        }

        absxk = y / 2.0;
        if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
          if (absxk <= 2.2250738585072014E-308) {
            r = 4.94065645841247E-324;
          } else {
            frexp(absxk, &ia);
            r = ldexp(1.0, ia - 53);
          }
        } else {
          r = rtNaN;
        }

        if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) == (y >
               0.0)))) {
          p = TRUE;
        } else {
          p = FALSE;
        }

        if (p) {
          x = rt_atan2d_snf(pw->data[ixstart].im, pw->data[ixstart].re);
          y = rt_atan2d_snf(ath.im, ath.re);
          absxk = fabs(y / 2.0);
          if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
            if (absxk <= 2.2250738585072014E-308) {
              r = 4.94065645841247E-324;
            } else {
              frexp(absxk, &ic);
              r = ldexp(1.0, ic - 53);
            }
          } else {
            r = rtNaN;
          }

          if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) == (y
                 > 0.0)))) {
            p = TRUE;
          } else {
            p = FALSE;
          }

          if (p) {
            x = fabs(pw->data[ixstart].re);
            y = fabs(ath.re);
            absxk = y / 2.0;
            if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
              if (absxk <= 2.2250738585072014E-308) {
                r = 4.94065645841247E-324;
              } else {
                frexp(absxk, &b_m);
                r = ldexp(1.0, b_m - 53);
              }
            } else {
              r = rtNaN;
            }

            if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) ==
                  (y > 0.0)))) {
              p = TRUE;
            } else {
              p = FALSE;
            }

            if (p) {
              x = 0.0;
              y = 0.0;
            }
          }
        }

        if (x > y) {
          ath = pw->data[ixstart];
        }

        ixstart++;
      }
    }
  }

  ath.re *= 1.0E-20;
  ath.im *= 1.0E-20;
  b_sqrt(&ath);
  if (a > b) {
    i0 = 0;
    b = 0;
  } else {
    i0 = a - 1;
  }

  emxInit_creal_T(&b_f, 1);
  i1 = b_f->size[0];
  b_f->size[0] = b - i0;
  emxEnsureCapacity((emxArray__common *)b_f, i1, (int32_T)sizeof(creal_T));
  j = (b - i0) - 1;
  for (i1 = 0; i1 <= j; i1++) {
    b_f->data[i1] = f->data[i0 + i1];
  }

  b_emxInit_real_T(&b_b, 1);
  b_abs(b_f, b_b);
  emxFree_creal_T(&b_f);
  b_emxInit_real_T(&b_y, 1);
  if ((m->size[1] == 1) || (b_b->size[0] == 1)) {
    i0 = b_y->size[0];
    b_y->size[0] = m->size[0];
    emxEnsureCapacity((emxArray__common *)b_y, i0, (int32_T)sizeof(real_T));
    j = m->size[0] - 1;
    for (i0 = 0; i0 <= j; i0++) {
      b_y->data[i0] = 0.0;
      i = b_b->size[0] - 1;
      for (i1 = 0; i1 <= i; i1++) {
        b_y->data[i0] += m->data[i0 + m->size[0] * i1] * b_b->data[i1];
      }
    }
  } else {
    c_k = m->size[1];
    unnamed_idx_0 = (uint32_T)m->size[0];
    i0 = b_y->size[0];
    b_y->size[0] = (int32_T)unnamed_idx_0;
    emxEnsureCapacity((emxArray__common *)b_y, i0, (int32_T)sizeof(real_T));
    b_m = m->size[0];
    i = b_y->size[0];
    i0 = b_y->size[0];
    b_y->size[0] = i;
    emxEnsureCapacity((emxArray__common *)b_y, i0, (int32_T)sizeof(real_T));
    j = i - 1;
    for (i0 = 0; i0 <= j; i0++) {
      b_y->data[i0] = 0.0;
    }

    if (b_m == 0) {
    } else {
      for (j = 0; j <= 0; j += b_m) {
        i0 = j + b_m;
        for (ic = j; ic + 1 <= i0; ic++) {
          b_y->data[ic] = 0.0;
        }
      }

      i = 0;
      for (j = 0; j <= 0; j += b_m) {
        ixstart = 0;
        i0 = i + c_k;
        for (a = i; a + 1 <= i0; a++) {
          if (b_b->data[a] != 0.0) {
            ia = ixstart;
            i1 = j + b_m;
            for (ic = j; ic + 1 <= i1; ic++) {
              ia++;
              b_y->data[ic] += b_b->data[a] * m->data[ia - 1];
            }
          }

          ixstart += b_m;
        }

        i += c_k;
      }
    }
  }

  emxFree_real_T(&m);
  unnamed_idx_0 = (uint32_T)b_y->size[0];
  i0 = f->size[0];
  f->size[0] = (int32_T)unnamed_idx_0;
  emxEnsureCapacity((emxArray__common *)f, i0, (int32_T)sizeof(creal_T));
  i0 = f->size[0];
  for (c_k = 0; c_k + 1 <= i0; c_k++) {
    if ((fabs(b_y->data[c_k]) > 8.9884656743115785E+307) || (fabs(ath.re) >
         8.9884656743115785E+307) || (fabs(ath.im) > 8.9884656743115785E+307)) {
      x = fabs(b_y->data[c_k]) / 2.0;
      y = rt_hypotd_snf(fabs(ath.re / 2.0), fabs(ath.im / 2.0));
    } else {
      x = fabs(b_y->data[c_k]);
      y = rt_hypotd_snf(fabs(ath.re), fabs(ath.im));
    }

    absxk = y / 2.0;
    if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
      if (absxk <= 2.2250738585072014E-308) {
        r = 4.94065645841247E-324;
      } else {
        frexp(absxk, &exponent);
        r = ldexp(1.0, exponent - 53);
      }
    } else {
      r = rtNaN;
    }

    if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) == (y > 0.0))))
    {
      p = TRUE;
    } else {
      p = FALSE;
    }

    if (p) {
      x = rt_atan2d_snf(0.0, b_y->data[c_k]);
      y = rt_atan2d_snf(ath.im, ath.re);
      absxk = fabs(y / 2.0);
      if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
        if (absxk <= 2.2250738585072014E-308) {
          r = 4.94065645841247E-324;
        } else {
          frexp(absxk, &b_exponent);
          r = ldexp(1.0, b_exponent - 53);
        }
      } else {
        r = rtNaN;
      }

      if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) == (y >
             0.0)))) {
        p = TRUE;
      } else {
        p = FALSE;
      }

      if (p) {
        x = fabs(b_y->data[c_k]);
        y = fabs(ath.re);
        absxk = y / 2.0;
        if ((!rtIsInf(absxk)) && (!rtIsNaN(absxk))) {
          if (absxk <= 2.2250738585072014E-308) {
            r = 4.94065645841247E-324;
          } else {
            frexp(absxk, &c_exponent);
            r = ldexp(1.0, c_exponent - 53);
          }
        } else {
          r = rtNaN;
        }

        if ((fabs(y - x) < r) || (rtIsInf(x) && rtIsInf(y) && ((x > 0.0) == (y >
               0.0)))) {
          p = TRUE;
        } else {
          p = FALSE;
        }

        if (p) {
          x = 0.0;
          y = 0.0;
        }
      }
    }

    if ((x < y) || rtIsNaN(b_y->data[c_k])) {
      absxk = ath.re;
      r = ath.im;
    } else {
      absxk = b_y->data[c_k];
      r = 0.0;
    }

    f->data[c_k].re = absxk;
    f->data[c_k].im = r;
  }

  emxFree_real_T(&b_y);
  i0 = pw->size[0];
  pw->size[0] = f->size[0];
  emxEnsureCapacity((emxArray__common *)pw, i0, (int32_T)sizeof(creal_T));
  j = f->size[0] - 1;
  for (i0 = 0; i0 <= j; i0++) {
    pw->data[i0] = f->data[i0];
  }

  for (c_k = 0; c_k <= f->size[0] - 1; c_k++) {
    ath = pw->data[c_k];
    if ((pw->data[c_k].im == 0.0) && rtIsNaN(pw->data[c_k].re)) {
    } else if ((fabs(pw->data[c_k].re) > 8.9884656743115785E+307) || (fabs
                (pw->data[c_k].im) > 8.9884656743115785E+307)) {
      ath.re = log(rt_hypotd_snf(fabs(pw->data[c_k].re / 2.0), fabs(pw->data[c_k]
        .im / 2.0))) + 0.69314718055994529;
      ath.im = rt_atan2d_snf(pw->data[c_k].im, pw->data[c_k].re);
    } else {
      ath.re = log(rt_hypotd_snf(fabs(pw->data[c_k].re), fabs(pw->data[c_k].im)));
      ath.im = rt_atan2d_snf(pw->data[c_k].im, pw->data[c_k].re);
    }

    pw->data[c_k] = ath;
  }

  emxFree_creal_T(&f);
  a_rdct(pw, b_b);
  i0 = c->size[0] * c->size[1];
  c->size[0] = 1;
  emxEnsureCapacity((emxArray__common *)c, i0, (int32_T)sizeof(real_T));
  i = b_b->size[0];
  i0 = c->size[0] * c->size[1];
  c->size[1] = i;
  emxEnsureCapacity((emxArray__common *)c, i0, (int32_T)sizeof(real_T));
  emxFree_creal_T(&pw);
  j = b_b->size[0] - 1;
  for (i0 = 0; i0 <= j; i0++) {
    c->data[i0] = b_b->data[i0];
  }

  emxFree_real_T(&b_b);
  i = nc + 1;
  if ((nc > 0) && (i <= 0)) {
    i = MAX_int32_T;
  }

  nc = i;
  if (32 > nc) {
    b_emxInit_int32_T(&r0, 1);
    i0 = c->size[1];
    i1 = r0->size[0];
    r0->size[0] = i0 - nc;
    emxEnsureCapacity((emxArray__common *)r0, i1, (int32_T)sizeof(int32_T));
    j = (i0 - nc) - 1;
    for (i0 = 0; i0 <= j; i0++) {
      r0->data[i0] = (nc + i0) + 1;
    }

    emxInit_int32_T(&idx, 2);
    i0 = idx->size[0] * idx->size[1];
    idx->size[0] = 1;
    emxEnsureCapacity((emxArray__common *)idx, i0, (int32_T)sizeof(int32_T));
    i = r0->size[0];
    i0 = idx->size[0] * idx->size[1];
    idx->size[1] = i;
    emxEnsureCapacity((emxArray__common *)idx, i0, (int32_T)sizeof(int32_T));
    j = r0->size[0] - 1;
    for (i0 = 0; i0 <= j; i0++) {
      idx->data[i0] = r0->data[i0];
    }

    emxFree_int32_T(&r0);
    if (idx->size[1] == 1) {
      i = c->size[1] - 1;
      for (j = idx->data[0]; j <= i; j++) {
        c->data[c->size[0] * (j - 1)] = c->data[c->size[0] * j];
      }
    } else {
      emxInit_boolean_T(&c_b, 2);
      i0 = c_b->size[0] * c_b->size[1];
      c_b->size[0] = 1;
      emxEnsureCapacity((emxArray__common *)c_b, i0, (int32_T)sizeof(boolean_T));
      i = c->size[1];
      i0 = c_b->size[0] * c_b->size[1];
      c_b->size[1] = i;
      emxEnsureCapacity((emxArray__common *)c_b, i0, (int32_T)sizeof(boolean_T));
      j = c->size[1] - 1;
      for (i0 = 0; i0 <= j; i0++) {
        c_b->data[i0] = FALSE;
      }

      for (c_k = 1; c_k <= idx->size[1]; c_k++) {
        c_b->data[idx->data[c_k - 1] - 1] = TRUE;
      }

      i = 0;
      for (c_k = 1; c_k <= c_b->size[1]; c_k++) {
        b = (int32_T)c_b->data[c_k - 1];
        i += b;
      }

      i = c->size[1] - i;
      ixstart = c_b->size[1];
      j = 0;
      i0 = c->size[1];
      for (c_k = 1; c_k <= i0; c_k++) {
        if ((c_k > ixstart) || (!c_b->data[c_k - 1])) {
          c->data[c->size[0] * j] = c->data[c->size[0] * (c_k - 1)];
          j++;
        }
      }

      emxFree_boolean_T(&c_b);
    }

    emxFree_int32_T(&idx);
    if (1 > i) {
      i = 0;
    }

    emxInit_real_T(&b_c, 2);
    i0 = b_c->size[0] * b_c->size[1];
    b_c->size[0] = 1;
    b_c->size[1] = i;
    emxEnsureCapacity((emxArray__common *)b_c, i0, (int32_T)sizeof(real_T));
    j = i - 1;
    for (i0 = 0; i0 <= j; i0++) {
      b_c->data[b_c->size[0] * i0] = c->data[c->size[0] * i0];
    }

    i0 = c->size[0] * c->size[1];
    c->size[0] = 1;
    c->size[1] = b_c->size[1];
    emxEnsureCapacity((emxArray__common *)c, i0, (int32_T)sizeof(real_T));
    j = b_c->size[1] - 1;
    for (i0 = 0; i0 <= j; i0++) {
      c->data[c->size[0] * i0] = b_c->data[b_c->size[0] * i0];
    }

    emxFree_real_T(&b_c);
  } else {
    if (32 < nc) {
      emxInit_real_T(&b_c, 2);
      i = nc - 32;
      i0 = b_c->size[0] * b_c->size[1];
      b_c->size[0] = 1;
      b_c->size[1] = c->size[1] + i;
      emxEnsureCapacity((emxArray__common *)b_c, i0, (int32_T)sizeof(real_T));
      j = c->size[1] - 1;
      for (i0 = 0; i0 <= j; i0++) {
        b_c->data[b_c->size[0] * i0] = c->data[c->size[0] * i0];
      }

      j = i - 1;
      for (i0 = 0; i0 <= j; i0++) {
        b_c->data[b_c->size[0] * (i0 + c->size[1])] = 0.0;
      }

      i0 = c->size[0] * c->size[1];
      c->size[0] = 1;
      c->size[1] = b_c->size[1];
      emxEnsureCapacity((emxArray__common *)c, i0, (int32_T)sizeof(real_T));
      j = b_c->size[1] - 1;
      for (i0 = 0; i0 <= j; i0++) {
        c->data[c->size[0] * i0] = b_c->data[b_c->size[0] * i0];
      }

      emxFree_real_T(&b_c);
    }
  }

  i = c->size[1] - 1;
  for (j = 1; j <= i; j++) {
    c->data[c->size[0] * (j - 1)] = c->data[c->size[0] * j];
  }

  if (1 > i) {
    i = 0;
  }

  emxInit_real_T(&c_c, 2);
  i0 = c_c->size[0] * c_c->size[1];
  c_c->size[0] = 1;
  c_c->size[1] = i;
  emxEnsureCapacity((emxArray__common *)c_c, i0, (int32_T)sizeof(real_T));
  j = i - 1;
  for (i0 = 0; i0 <= j; i0++) {
    c_c->data[c_c->size[0] * i0] = c->data[c->size[0] * i0];
  }

  i0 = c->size[0] * c->size[1];
  c->size[0] = 1;
  c->size[1] = c_c->size[1];
  emxEnsureCapacity((emxArray__common *)c, i0, (int32_T)sizeof(real_T));
  j = c_c->size[1] - 1;
  for (i0 = 0; i0 <= j; i0++) {
    c->data[c->size[0] * i0] = c_c->data[c_c->size[0] * i0];
  }

  emxFree_real_T(&c_c);
}

/* End of code generation (a_melcepst.c) */
