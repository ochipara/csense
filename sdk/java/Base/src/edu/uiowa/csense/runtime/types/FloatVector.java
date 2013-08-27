package edu.uiowa.csense.runtime.types;

import java.nio.FloatBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class FloatVector extends Vector<Float> {
    protected final TypeInfo<FloatVector> _type;
    private final FloatBuffer _floatBuffer;

    public FloatVector(FramePool pool, TypeInfo<FloatVector> type)
	    throws CSenseException {
	super(pool, type);
	_type = type;
	_floatBuffer = buffer.asFloatBuffer();
    }

    @Override
    public TypeInfo<FloatVector> getTypeInfo() {
	return _type;
    }

    @Override
    public String toString() {
	return "FloatVector[cap=" + getBuffer().capacity() + "] "
		+ super.toString();
    }

    public FloatBuffer getFloatBuffer() {
	return _floatBuffer;
    }

}
