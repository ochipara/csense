import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.ByteOrder;


public class test {	
	static {
		System.out.println("java.library.path=" + System.getProperty("java.library.path"));
		System.out.println("trying to load library");
		System.loadLibrary("csense-native");
		System.out.println("loaded library");		
	}
	
	
	public static DoubleBuffer zeros(int r, int c) {
		ByteBuffer bytes = ByteBuffer.allocateDirect(r * c * 8);
	    bytes.order(ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer samples = bytes.asDoubleBuffer();
        for (int i = 0; i < r * c; i ++) samples.put(i, 0);
        
        return samples;
	}
	
	public static ByteBuffer falses(int r, int c) {
		ByteBuffer bytes = ByteBuffer.allocateDirect(r * c * 8);
	    bytes.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < r * c; i ++) {
	        byte b = 0;
        	bytes.put(i, b);
        }

		return bytes;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		// MATLAB definition:
		/// 
		// JNI definition:
		// public static void a_melcepst(double[] s, double fs, int nc, emxArray_real_T c) 
	
		// copy and paste your data into textwrangler
		// use the following regexp to fix the formatting \ *[-|0-9]\r\ *[-|0-9]

		LocalState local = new LocalState();
		local.setP(zeros(1, 257));
		local.setAc(0.0);
		local.setSn2(zeros(1, 257));
		local.setPb(zeros(1, 257));
		local.setPb2(zeros(1, 257));
		local.setActmin(zeros(1, 257));
		local.setActminsub(zeros(1, 257));
		local.setSubwc(0.0);
		local.setActminsub(zeros(8, 257));
		local.setIbuf(0);
		local.setLminflag(falses(1, 257));
		local.setNrcum(0);
		local.setTinc(0);		
       
       	DoubleBuffer yf = zeros(1, 257);
        DoubleBuffer x = zeros(1, 257);       
        
        System.out.println("init\n");
        EstimateNoiseMW.EstimateNoiseW_initialize();
		System.out.println("samples\n");
        EstimateNoiseMW.EstimateNoiseW(yf, local, x);
		System.out.println("terminate\n");
        EstimateNoiseMW.EstimateNoiseW_terminate();	
        
        double s = 0;
        for (int i = 0; i < 257; i++) {
        	double d = x.get(i);
        	s = s + d;
        	
        	System.out.println(i + " " + d);
        };
        s = s / 512.0;
        System.out.println("mean=" + s);        	
	}

}
