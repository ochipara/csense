import base.melcepst.*;

public class test {	
	static {
		System.out.println("trying to load library");
		System.loadLibrary("a_melcepst");
		System.out.println("loaded library");		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Foo");
		
		// MATLAB definition:
		/// function c=a_melcepst(s,fs,nc)
		// JNI definition:
		// public static void a_melcepst(double[] s, double fs, int nc, emxArray_real_T c) 
	
		// copy and paste your data into textwrangler
		// use the following regexp to fix the formatting \ *[-|0-9]\r\ *[-|0-9]
		double data[] = {0.000,.002,.000,.001,.003,.006,.003,.001,.000,.002,.002,.000,.000,.002,.003,.001,0.000,.002,.001,0.000,0.000,0.000,.000,.002,.003,.002,0.000,0.000,.001,.000,.000,.002,.002,.001,.002,.003,.000,.000,.000,.001,.001,.001,.001,.001,.002,.002,0.000,0.000,.003,.003,0.000,0.000,.002,.002,.001,0.001,0.000,.004,.005,.002,0.000,.004,.006,.001,0.000,.000,.005,.002,0.000,.000,.004,.003,.002,.003,.004,.005,.003,.000,0.000,.001,.003,.002,.001,.001,.005,.003,.002,0.000,.001,.003,.002,.002,.002,.003,.003,.002,.001,.000,.001,0.000,.002,.005,.005,.002,.001,.003,.005,.002,0.000,0.001,.003,.003,.001,.000,.002,.003,.001,0.000,.001,.002,0.000,0.001,0.001,.001,.001,0.002,0.002,.001,.001,0.000,0.000,0.000,0.001,0.001,0.001,0.000,.000,0.000,0.001,0.001,0.001,0.004,0.002,0.001,.000,.000,.000,0.000,0.000,0.000,0.002,0.001,0.003,0.001,.003,.002,.000,0.001,0.000,0.000,.000,0.000,0.000,0.000,.000,.000,.001,.000,0.000,0.002,0.003,0.003,0.002,0.003,0.004,0.002,0.001,0.002,0.004,0.005,0.005,0.003,0.005,0.005,0.005,0.004,0.001,0.005,0.004,0.006,0.006,0.005,0.004,0.003,0.005,0.004,0.004,0.002,0.002,0.005,0.009,0.003,0.002,0.003,0.007,0.002,.000,0.003,0.005,0.004,0.002,0.001,0.004,0.003,0.002,0.000,0.002,0.004,0.003,0.001,0.002,0.005,0.005,0.000,0.001,0.002,0.004,0.003,0.002,0.003,0.003,0.004,0.003,0.001,0.002,0.004,0.002,0.001,0.002,0.002,0.002,.000,.000,0.003,0.003,0.002,.000,0.001,0.004,0.001,.000,0.000,0.002,0.001,0.001,.002,0.000,0.001,0.003,0.001,.000,.000,.000,0.004,0.002,.000,0.000,.000,0.001,0.000,.001,.001,.001,0.002,0.002,.000,0.000,0.000,0.001,.000,0.001,0.000,.002,0.002,0.001,0.001,0.001,0.002,0.001,.000,.001,0.000,.001,.001,.000,0.001,0.002,.000,.001,.002,.002,.003,.000,.001,.000,0.000,.000,.000,0.002,0.001,0.000,.001,0.002,0.002,0.001,.000,.000,0.000,0.001,.000,0.000,0.002,0.001,0.003,0.002,0.002,0.000,0.000,0.002,0.001,.000,.001,0.000,0.000,0.000,0.000,.001,.000,0.000,0.001,0.000,.002,.000,0.000,.000,.001,.000,.001,.002,.004,.002,0.000,.001,.003,.001,0.000,0.002,.003,.003,.001,0.000,.001,.001,0.000,0.003,0.000,.002,.001,0.000,0.000,.001,.000,0.001,0.005,0.002,0.002,0.003,0.000,0.001,0.000,0.001,0.002,0.002,0.002,0.003,0.000,0.000,0.003,0.002,0.000,.001,.000,0.003,0.002,0.001,.001,.000,0.001,0.000,.000,.001,0.001,.001,.002,0.001,0.003,.000,.001,0.001,0.004,0.001,.002,.002,0.001,0.002,.002,.002,0.003,0.001,.003,.002,0.001,0.004,.001,.002,.000,0.004,0.002,.002,.003,0.002,0.005,0.000,.004,0.002,0.005,0.002,.001,0.001,0.004,0.006,0.001,.002,0.001,0.005,0.001,0.000,.000,0.000,0.003,0.002,.001,.001,0.001,0.001,.000,0.002,0.003,0.002,.000,0.002,0.003,0.001,.000,0.001,0.003,.000,.002,0.001,0.003,0.000,.000,0.002,0.004,0.003,0.001,.000,0.002,0.004,0.001,0.000,0.004,0.005,0.002,0.001,0.002,0.003,0.001,0.001,0.003,0.004,0.005,0.002,0.003,0.004,0.001,0.000,.000,0.000,0.002,0.004,0.002,0.001,.000,.000,.001,.001,.002,0.002,0.004,0.004,0.0012};
		double fs = 16000;
		int nc = 32;
		emxArray_real_T c = a_melcepst.emxCreate_real_T(0, 0);

        DoubleBuffer samples = ByteBuffer.allocateDirect(512 * 8).asDoubleBuffer();
		samples.put(data);
        DoubleBuffer r = ByteBuffer.allocateDirect(36 * 8).asDoubleBuffer();

		a_melcepst.a_melcepst_initialize();
		a_melcepst.a_melcepst_wrap(samples, fs, nc, c, r);
		
		int dims = c.getNumDimensions();
		int[] size = new int[2];
		size[0] = a_melcepst.intArray_getitem(c.getSize(), 0);
		size[1] = a_melcepst.intArray_getitem(c.getSize(), 1);
		System.out.printf("size: %dx%d, dim: %d, allocatedSize: %d\n", size[0], size[1], dims, c.getAllocatedSize());
		
        for(int i = 0; i < r.length; i++)
            System.out.printf("feature[%d] %f\n", i, r[i]);

		/*
		SWIGTYPE_p_double mfcc = c.getData();
		int idx = 0;
        for(int dim = 0; dim < r.getNumDimensions(); dim++) {
        	for(int i = 0; i < size[dim]; i++, idx++) {
        		double feature = a_melcepst.doubleArray_getitem(mfcc, idx);
        		System.out.println("MFCC" + "feature[" + dim + "][" + i + "] idx" + idx + ": " + feature);
        	}
        }
		*/
		a_melcepst.a_melcepst_terminate();
		
	}

}
