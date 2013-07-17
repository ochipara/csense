%module CSenseLib
// this is necessary for buffer conversions 
%include "matlab.i"

%{
#include "ctypes.h"
extern void int16_to_double(int16_t a[], double b[], int count);
%}

extern void int16_to_double(int16_t a[], double b[], int count);
