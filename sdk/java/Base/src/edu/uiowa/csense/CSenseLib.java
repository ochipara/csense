package edu.uiowa.csense;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * This is here just to get rid of compilation errors in modules that use the library
 * The actual code will be generated via swig
 * 
 * @author ochipara
 *
 */
public class CSenseLib {

    public static void int16_to_double(ShortBuffer buffer, DoubleBuffer buffer2, int capacity) {
	throw new UnsupportedOperationException();
    }

    public static void CSenseLib_terminate() {
	throw new UnsupportedOperationException();
    }

    public static void int16_to_floats(ShortBuffer buffer, FloatBuffer buffer2, int capacity) {
	throw new UnsupportedOperationException();	
    }
    
    
}
