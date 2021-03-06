#include <stdio.h>
#include <stdlib.h>
#include "a_melcepst.h"
#include "a_melcepst_initialize.h"
#include "a_melcepst_terminate.h"
#include "a_melcepst_emxAPI.h"



	//a_melcepst(const real_T s[512], real_T fs, int32_T nc, emxArray_real_T *c)
	/*
	typedef struct emxArray_real_T
	{
    real_T *data;
    int32_T *size;
    int32_T allocatedSize;
    int32_T numDimensions;
    boolean_T canFreeData;
	} emxArray_real_T;
*/
//emxArray_real_T *emxCreate_real_T(int32_T rows, int32_T cols)
	
	//emxArray_real_T *emxCreate_real_T(int32_T rows, int32_T cols);
	
int main() {
	real_T data[] = {0.000,.002,.000,.001,.003,.006,.003,.001,.000,.002,.002,.000,.000,.002,.003,.001,0.000,.002,.001,0.000,0.000,0.000,.000,.002,.003,.002,0.000,0.000,.001,.000,.000,.002,.002,.001,.002,.003,.000,.000,.000,.001,.001,.001,.001,.001,.002,.002,0.000,0.000,.003,.003,0.000,0.000,.002,.002,.001,0.001,0.000,.004,.005,.002,0.000,.004,.006,.001,0.000,.000,.005,.002,0.000,.000,.004,.003,.002,.003,.004,.005,.003,.000,0.000,.001,.003,.002,.001,.001,.005,.003,.002,0.000,.001,.003,.002,.002,.002,.003,.003,.002,.001,.000,.001,0.000,.002,.005,.005,.002,.001,.003,.005,.002,0.000,0.001,.003,.003,.001,.000,.002,.003,.001,0.000,.001,.002,0.000,0.001,0.001,.001,.001,0.002,0.002,.001,.001,0.000,0.000,0.000,0.001,0.001,0.001,0.000,.000,0.000,0.001,0.001,0.001,0.004,0.002,0.001,.000,.000,.000,0.000,0.000,0.000,0.002,0.001,0.003,0.001,.003,.002,.000,0.001,0.000,0.000,.000,0.000,0.000,0.000,.000,.000,.001,.000,0.000,0.002,0.003,0.003,0.002,0.003,0.004,0.002,0.001,0.002,0.004,0.005,0.005,0.003,0.005,0.005,0.005,0.004,0.001,0.005,0.004,0.006,0.006,0.005,0.004,0.003,0.005,0.004,0.004,0.002,0.002,0.005,0.009,0.003,0.002,0.003,0.007,0.002,.000,0.003,0.005,0.004,0.002,0.001,0.004,0.003,0.002,0.000,0.002,0.004,0.003,0.001,0.002,0.005,0.005,0.000,0.001,0.002,0.004,0.003,0.002,0.003,0.003,0.004,0.003,0.001,0.002,0.004,0.002,0.001,0.002,0.002,0.002,.000,.000,0.003,0.003,0.002,.000,0.001,0.004,0.001,.000,0.000,0.002,0.001,0.001,.002,0.000,0.001,0.003,0.001,.000,.000,.000,0.004,0.002,.000,0.000,.000,0.001,0.000,.001,.001,.001,0.002,0.002,.000,0.000,0.000,0.001,.000,0.001,0.000,.002,0.002,0.001,0.001,0.001,0.002,0.001,.000,.001,0.000,.001,.001,.000,0.001,0.002,.000,.001,.002,.002,.003,.000,.001,.000,0.000,.000,.000,0.002,0.001,0.000,.001,0.002,0.002,0.001,.000,.000,0.000,0.001,.000,0.000,0.002,0.001,0.003,0.002,0.002,0.000,0.000,0.002,0.001,.000,.001,0.000,0.000,0.000,0.000,.001,.000,0.000,0.001,0.000,.002,.000,0.000,.000,.001,.000,.001,.002,.004,.002,0.000,.001,.003,.001,0.000,0.002,.003,.003,.001,0.000,.001,.001,0.000,0.003,0.000,.002,.001,0.000,0.000,.001,.000,0.001,0.005,0.002,0.002,0.003,0.000,0.001,0.000,0.001,0.002,0.002,0.002,0.003,0.000,0.000,0.003,0.002,0.000,.001,.000,0.003,0.002,0.001,.001,.000,0.001,0.000,.000,.001,0.001,.001,.002,0.001,0.003,.000,.001,0.001,0.004,0.001,.002,.002,0.001,0.002,.002,.002,0.003,0.001,.003,.002,0.001,0.004,.001,.002,.000,0.004,0.002,.002,.003,0.002,0.005,0.000,.004,0.002,0.005,0.002,.001,0.001,0.004,0.006,0.001,.002,0.001,0.005,0.001,0.000,.000,0.000,0.003,0.002,.001,.001,0.001,0.001,.000,0.002,0.003,0.002,.000,0.002,0.003,0.001,.000,0.001,0.003,.000,.002,0.001,0.003,0.000,.000,0.002,0.004,0.003,0.001,.000,0.002,0.004,0.001,0.000,0.004,0.005,0.002,0.001,0.002,0.003,0.001,0.001,0.003,0.004,0.005,0.002,0.003,0.004,0.001,0.000,.000,0.000,0.002,0.004,0.002,0.001,.000,.000,.001,.001,.002,0.002,0.004,0.004,0.0012};
	double fs = 16000;
	int nc = 32;	
    int i = 0;
    double* features = calloc(sizeof(double), 64);
	emxArray_real_T *r = NULL;
	
	r = emxCreate_real_T(0, 0);
	
	//printf("%p\n", r);
    free(r->data);
    r->size[0] = 1;
    r->size[1] = 32;
    r->allocatedSize = 64;
    r->data = features;
    r->canFreeData = FALSE;
    printf("number of samples: %lu\n", sizeof(data) / sizeof(data[0]));
	printf("num_dim %d\n", r->numDimensions);
	printf("allocatedSize %d\n", r->allocatedSize);
	printf("size %dx%d\n", r->size[0], r->size[1]);
	a_melcepst_initialize();
	a_melcepst(data, fs, nc, r);
	a_melcepst_terminate();
	
	//print out informaiton
    printf("number of samples: %lu\n", sizeof(data) / sizeof(data[0]));
	printf("num_dim %d\n", r->numDimensions);
	printf("allocatedSize %d\n", r->allocatedSize);
	printf("size %dx%d\n", r->size[0], r->size[1]);
	
    for(i = 0; i < 64; i++) {
        printf("r->data[%d] %f\n", i, r->data[i]);
    }
	emxDestroyArray_real_T(r);
	printf("done!\n");
	
	
	return 0;
}
