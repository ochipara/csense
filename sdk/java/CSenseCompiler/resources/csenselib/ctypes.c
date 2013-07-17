#include <stdint.h>

void int16_to_double(int16_t a[], double b[], int count) {
	int j = 0;
	int i = 0;
	for (; i < count; i = i + 1) {
		double d = a[i] / 32768.0;
		b[j] = d;
		j++; 
	}
}

void int16_to_float(int16_t a[], float b[], int count) {
	int j = 0;
	int i = 0;
	for (; i < count; i = i + 1) {
		float d = a[i] / 32768.0;
		b[j] = d;
		j++; 
	}
}