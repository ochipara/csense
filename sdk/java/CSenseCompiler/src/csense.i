%module CSenseLib
%include "matlab.i"

%{
//import functions
extern void CSenseLib_initialize(void);
extern void CSenseLib_terminate(void);

#include "ctypes.h"
extern void int16_to_double(int16_t a[], double b[], int count);
extern void int16_to_float(int16_t a[], float b[], int count);

extern void CSenseLib_initialize(void);
extern void CSenseLib_terminate(void);
%}

extern void CSenseLib_initialize(void);
extern void CSenseLib_terminate(void);
extern void int16_to_double(int16_t a[], double b[], int count);
extern void int16_to_float(int16_t a[], float b[], int count);
