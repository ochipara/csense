#include <stdio.h>
#include <stdlib.h>
#include "EstimateNoiseW.h"
#include "EstimateNoiseW_initialize.h"
#include "EstimateNoiseW_terminate.h"

void print(real_T x[257]) {
	int i;
	for (i = 0; i < 257; i++) {
		printf(" %e", x[i]);
	}
	printf("\n");
}

int main() {
	EstimateNoiseW_initialize();
	const real_T yf1[257] = { 0.002113,  0.0082676,  0.0038951,   0.028005,  0.0043572,  0.0014372, 0.00042385, 0.00015058,  0.0022199,   0.012005,   0.010101,  0.0024266,   0.001894,  0.0044002,   0.012359,  0.0053171, 0.00050559, 0.00038539, 0.00011129,  2.717e-05, 0.00012007, 0.00010769, 0.00053832, 0.00029209, 1.9091e-05, 3.7303e-05, 9.1129e-05, 0.00012941,  4.411e-05, 2.6104e-05, 3.9965e-05, 6.8651e-05,  0.0001684, 1.2609e-05, 3.2096e-05, 1.0132e-05, 3.1877e-05, 0.00021583, 0.00029002, 0.00017001, 2.8273e-05, 0.00035971, 0.00047689, 0.00087715, 0.00064244, 0.00022186, 6.2118e-05, 8.7034e-07, 0.00010708, 9.5294e-05, 8.3822e-05, 5.0662e-05, 3.5184e-05, 7.1579e-06, 6.5115e-05, 2.2109e-05, 6.7094e-06, 1.2468e-05, 8.9151e-06, 4.9113e-05, 5.1092e-05, 4.3087e-06, 3.4703e-06, 8.0329e-06, 1.4723e-05,  2.027e-05, 1.5519e-05, 4.0156e-05, 8.0487e-05, 0.00011388, 0.00022318,  0.0001708,   0.000156, 8.4449e-05, 4.9184e-05, 3.9171e-05, 1.9405e-05, 0.00013176, 4.4832e-05, 7.4366e-06, 2.4311e-07, 2.5363e-05, 1.4784e-05, 9.4175e-06, 9.1938e-06, 5.6333e-06, 8.5146e-06, 2.9089e-05, 3.3456e-05,  1.331e-05, 2.7097e-05, 1.7926e-05, 8.8506e-06, 2.5358e-06, 1.0675e-05,  3.362e-05, 2.2925e-05, 4.8007e-05, 1.2547e-05, 3.4093e-06, 4.5598e-07, 2.2842e-06, 2.1192e-05,  4.933e-06, 3.4536e-05, 1.7017e-08, 3.0696e-05, 2.5701e-05, 2.8584e-06, 7.4265e-06, 4.0991e-05, 4.8525e-05, 0.00010571, 9.5686e-05, 9.0193e-06, 2.4889e-05, 5.7539e-05, 2.6774e-05, 1.4355e-05, 3.1066e-05, 1.2006e-05, 1.1126e-05, 1.3195e-05, 5.3364e-05, 5.0034e-05, 1.1607e-05, 5.6045e-06, 8.3177e-06, 1.7862e-05,  2.912e-05, 4.7522e-05, 6.3647e-05, 5.9523e-05, 9.9335e-05, 0.00010068, 2.8925e-05, 9.2586e-05, 5.7587e-05, 3.5915e-05, 3.3346e-05, 3.1002e-05, 5.7596e-05, 2.6943e-05, 4.5344e-05, 4.5485e-06, 8.9956e-06, 1.2717e-05, 1.7833e-06, 7.6925e-06, 1.0205e-05, 2.4801e-05, 3.6668e-05, 3.1927e-05, 1.1362e-05, 3.8621e-05, 0.00011019, 9.7592e-05, 4.4724e-05, 3.9859e-06, 5.3008e-06, 4.9361e-05, 6.4373e-05, 1.5875e-05, 7.4445e-06, 1.0043e-05, 1.7348e-05, 3.3758e-06, 1.0706e-05, 1.3957e-05, 2.1804e-06, 4.4099e-06, 2.6888e-05,  2.037e-05, 1.5639e-05,  3.558e-06, 7.3473e-06,  2.058e-05, 1.0325e-05, 2.3397e-05, 1.8162e-05, 4.2428e-05, 1.3186e-06,  3.224e-05, 1.8109e-06, 8.6003e-06, 3.6756e-05, 5.2253e-06,  5.668e-06, 1.2481e-07, 1.6503e-06, 3.1237e-05, 3.7128e-05, 4.2887e-05, 5.3183e-05, 1.1128e-05, 6.3119e-05, 8.5777e-05, 2.8289e-05, 1.1392e-05, 2.5598e-05, 4.7619e-07, 2.6088e-05, 3.0478e-05, 2.0915e-05, 2.7032e-05, 6.3531e-05, 7.8766e-05, 1.2093e-06, 2.5284e-05, 3.1486e-05, 4.0581e-05, 1.1568e-05, 2.4097e-05, 1.1781e-05, 4.6867e-07, 6.2379e-07, 7.5083e-06, 1.1592e-05, 8.0539e-06, 5.2733e-05, 0.00013917, 7.9912e-05, 8.4166e-07, 1.4205e-05,  2.331e-05,  5.841e-05, 4.7766e-05, 3.2281e-05, 1.4504e-06, 9.9749e-06, 8.3948e-06, 2.8307e-05, 2.1579e-06,  6.901e-06, 1.4923e-06, 3.2477e-06, 1.7436e-05, 1.5099e-05, 1.3944e-05, 3.7922e-06, 1.4661e-05, 2.3577e-05, 1.7845e-05, 1.8506e-05, 2.3286e-05, 3.5051e-06, 1.1275e-05, 4.2359e-06, 7.4497e-06, 1.0657e-06, 3.8257e-05, 0.00010568,  8.921e-05, 6.3053e-06, 1.5329e-05, 5.1522e-06, 2.2477e-06};
	const real_T yf2[257] = {0.00091615,    0.24321,    0.19866,  0.0019349,  0.0047083,  0.0014869,  0.0024765, 0.00023101,  0.0070007,  0.0088918,  0.0053178,  0.0018136, 0.00029873,  0.0019924,  0.0046188, 5.3713e-05,  0.0011704, 0.00024954,  7.778e-05, 2.5328e-05, 2.4148e-05, 0.00011174, 0.00010995, 0.00019417, 6.3326e-05, 6.4756e-06, 4.1094e-05, 0.00019586, 3.1899e-05, 0.00062036, 0.00018843, 5.0131e-05, 1.5461e-05, 1.2968e-06, 2.5093e-05, 0.00011845, 0.00034806, 3.9906e-05, 1.2853e-05, 0.00015774, 9.9125e-05,  4.985e-06, 5.0769e-05, 0.00017422, 1.7236e-05, 4.5888e-05, 7.7911e-05, 6.9081e-05, 5.0913e-05, 0.00026452, 3.0685e-05, 8.4289e-05, 6.6381e-05, 2.5023e-05, 0.00024096, 0.00011284, 4.0044e-05, 3.9083e-05, 1.9441e-05, 9.5044e-05, 5.4295e-05, 1.3541e-05,  3.879e-05, 7.8357e-05, 5.8348e-05, 4.8786e-05, 3.2248e-05, 1.5047e-05, 3.8148e-05,  7.532e-06, 5.0144e-06, 5.7794e-06, 4.3873e-05, 6.3228e-05,  0.0002347, 0.00023212,   5.35e-05, 8.3074e-06, 3.0144e-05, 7.6965e-06, 1.2879e-05, 6.4446e-05, 2.0102e-05, 3.1938e-05, 1.1906e-05, 8.8002e-06, 3.5223e-05, 2.0517e-05, 9.6973e-05, 6.1324e-06, 3.7597e-05, 2.6504e-05, 5.4493e-05, 0.00012837, 4.8255e-05, 3.2058e-05, 9.7455e-06, 1.4278e-05, 2.0827e-05, 8.7511e-06,   5.77e-06, 3.0049e-05, 1.7122e-05, 2.9828e-05, 8.3737e-05, 2.0781e-05, 2.1553e-05, 3.4199e-05, 3.6851e-05, 3.1004e-05, 2.1582e-05, 1.1192e-05, 1.2645e-05,  1.974e-06, 1.1593e-06, 3.5452e-05, 4.1568e-05, 5.8341e-06, 4.6939e-05, 4.0369e-05, 1.7313e-05, 1.8148e-05, 1.4002e-05, 6.6896e-05, 3.4452e-06,  4.796e-06, 2.0058e-05, 1.2287e-05, 4.5641e-05,  4.225e-05, 1.2748e-05,  1.814e-05, 3.1738e-05, 1.0846e-05, 8.9801e-07,  2.956e-06, 2.2826e-06, 1.6199e-06, 1.8051e-05, 3.2703e-06, 4.2632e-07, 5.0964e-05, 7.1655e-05,  9.648e-06, 6.4718e-06, 3.0459e-05, 1.6362e-05, 3.4255e-05, 1.3913e-05, 4.4584e-07, 1.0251e-05, 4.3366e-05,  5.786e-06, 1.2761e-05, 1.0657e-05, 3.5015e-05, 4.6549e-05, 4.0648e-06, 2.3189e-06, 1.5515e-06, 3.8166e-05, 5.9976e-05,  1.534e-05, 1.0196e-05, 5.4092e-06, 1.9824e-06, 9.8612e-06, 3.7892e-05, 7.0834e-05, 7.1262e-05, 6.5081e-05, 8.6005e-05, 1.7508e-05,  0.0001112, 0.00016757, 1.7389e-05, 1.1567e-05, 2.3558e-05, 9.9724e-06, 1.6119e-05, 1.6453e-05, 5.6786e-05, 8.3855e-05, 7.0372e-06, 1.7723e-05, 4.2678e-06, 4.4902e-06, 2.7793e-05, 5.5081e-06, 2.1613e-05, 1.4281e-05, 0.00010634, 7.0365e-05, 1.3727e-05, 3.0085e-06,  2.182e-05, 1.5366e-05, 2.0737e-06, 1.5138e-06, 1.4443e-05, 2.9547e-05, 8.5366e-06, 9.9725e-07, 7.3565e-06, 1.2665e-05, 1.9405e-05, 1.0845e-05, 1.1819e-05, 2.0188e-06,  1.052e-06, 2.0888e-05, 7.1472e-05, 5.2898e-05, 2.7331e-05, 5.9114e-06, 7.4189e-06, 1.8824e-05, 3.5071e-05, 6.0316e-06, 2.6555e-05,  1.094e-05, 2.1085e-05, 5.4594e-05, 1.0334e-05, 1.0903e-05, 1.7618e-05, 3.1585e-05,  6.235e-06, 9.3409e-07, 5.0269e-06,  5.503e-06, 4.4977e-06, 8.6886e-06, 2.9423e-05,  1.986e-06, 1.5986e-05, 9.0351e-06, 1.5091e-05, 0.00010055, 0.00012808, 6.4922e-07, 2.0933e-05, 5.0002e-06, 8.8822e-06, 3.9991e-05, 3.9478e-05, 1.0352e-05, 1.5985e-06, 2.3341e-05, 1.7013e-05, 5.2651e-05,  0.0002255, 2.0353e-05,  2.267e-05, 1.6625e-05,  1.001e-05, 2.2486e-05};
	const real_T yf3[257] = {3.9845e-06,    0.14861,    0.28012,   0.029674,   0.010496,  0.0035715,  0.0014876,  0.0012302, 0.00033649, 0.00022483,  0.0010521, 0.00013482, 0.00021624,  0.0089428,  0.0040756, 0.00020124, 0.00025824, 0.00026444, 0.00053163, 0.00047456, 0.00014363, 9.4575e-05, 0.00021068, 0.00024372,  9.885e-05, 4.6466e-06, 5.7702e-05,  0.0001102, 2.8096e-05, 0.00035064, 4.7635e-05, 5.0977e-05, 7.4204e-05, 1.8061e-05, 1.0959e-06, 2.6044e-06, 3.1623e-05, 0.00010309, 0.00014482,  4.402e-05, 6.9728e-05, 0.00042478, 0.00048588, 3.4244e-05,  9.823e-06, 1.3733e-06, 3.8806e-05, 5.3899e-05, 2.6456e-05, 6.0122e-06, 6.5482e-05, 0.00014227, 0.00024217, 0.00033105, 0.00021876, 5.5135e-05, 9.4492e-05, 5.7283e-05, 0.00017146, 0.00024136, 0.00022185, 1.5543e-05, 9.1695e-06, 2.4932e-06, 2.6737e-06, 2.6765e-05, 4.5365e-05, 1.4662e-05, 4.4834e-05, 4.7826e-05,  4.481e-05, 1.2714e-05, 2.4311e-07, 9.6233e-05, 1.0422e-05, 3.3215e-07,  4.559e-05, 4.5209e-05, 1.3839e-05, 6.1431e-05, 5.1058e-07, 2.5324e-05, 5.7495e-06, 2.0069e-05, 8.5654e-06, 1.0952e-05, 4.6552e-06, 5.0615e-06, 2.8465e-05, 9.1135e-06, 2.9076e-06, 6.5028e-06, 9.4634e-06, 4.1777e-07, 5.6952e-06, 1.6333e-05, 9.0231e-06, 6.3986e-06, 4.3299e-06, 4.9448e-06,  1.726e-05,  1.646e-05,  3.965e-05, 1.4271e-05, 1.2093e-06, 1.2861e-05,  3.671e-05, 0.00010148, 0.00010876, 4.0864e-05, 4.8074e-05, 1.1584e-05,  1.017e-06, 8.9054e-06, 9.2483e-06, 2.0422e-05, 8.8385e-06, 1.0824e-05,  1.415e-05, 2.6508e-05, 6.5482e-07, 5.6733e-05, 3.3136e-05, 1.4484e-05, 7.5261e-05, 5.2058e-05, 1.9671e-05, 1.0244e-05, 2.3044e-05,  3.536e-05, 5.7519e-05, 4.3829e-05, 8.0999e-06, 8.7249e-06, 5.5652e-05, 8.6153e-05, 3.7022e-05,  6.719e-06, 2.2304e-05, 4.8016e-06, 3.8446e-06, 4.0627e-05, 1.1079e-05, 2.3311e-05, 4.9547e-06, 1.9989e-05, 5.3499e-05, 4.6676e-06, 3.4385e-07, 3.3066e-06, 1.3437e-05, 1.1258e-05, 9.2749e-06, 3.8559e-06,  8.847e-06,   7.92e-06, 1.0303e-05, 1.6379e-05, 4.1732e-06, 0.00010349, 6.4223e-05, 1.8957e-05,  3.926e-05,  5.267e-05, 3.0164e-05, 7.6002e-06, 1.2843e-06, 1.5482e-05,   1.22e-05, 1.1368e-05, 1.7868e-05, 1.0659e-05, 1.6602e-05, 8.7176e-06, 4.6477e-06, 2.3103e-05, 4.9563e-05,  5.367e-05, 6.5392e-05, 2.9119e-05, 1.3278e-05, 4.0659e-05, 4.5285e-05, 1.4683e-05, 9.4259e-06, 5.5625e-05,  6.241e-06, 8.6859e-06, 2.7525e-05, 2.5139e-06, 1.2549e-05, 1.4395e-06, 7.1357e-06, 1.7392e-06, 2.3222e-06, 1.2931e-05, 7.1631e-05,  5.595e-05, 3.7339e-05, 4.7819e-05, 4.9011e-05, 6.1624e-07, 1.5558e-06, 6.0549e-06, 1.1284e-05, 4.9111e-05, 4.6669e-05, 6.7062e-05, 3.8144e-05, 1.6096e-05, 2.9711e-05, 2.1645e-05, 3.1016e-06, 8.2641e-07,  2.148e-05, 7.8285e-06, 1.0025e-05, 1.3053e-05, 3.5539e-05, 9.5936e-05, 4.8244e-05, 3.7372e-06, 1.9618e-05, 4.7553e-06, 4.6394e-05, 7.4168e-05, 1.1938e-05, 1.1825e-05, 9.0234e-06, 4.2639e-05, 6.2767e-05, 4.1448e-05, 3.0215e-06, 3.2465e-06,  8.802e-06,  2.015e-06, 4.6773e-06, 2.5722e-07, 2.1536e-05, 4.4395e-05, 5.0818e-05, 3.0493e-05, 1.3924e-05, 5.8361e-06, 1.2456e-05, 1.8762e-05, 6.8339e-07, 3.4288e-05, 2.8443e-05, 9.1814e-06, 0.00010898, 0.00012284, 4.6025e-05, 8.8549e-06, 6.1204e-06,   5.26e-06, 1.1988e-06};
	
	LocalState *local = (LocalState *) calloc(1, sizeof(LocalState));
	local->tinc = 0.0320;
	real_T x[257];
	
	EstimateNoiseW(yf1, local, x);
	EstimateNoiseW(yf2, local, x);
	EstimateNoiseW(yf3, local, x);
	print(x);
	
	EstimateNoiseW_terminate(); 	
	return 0;
}