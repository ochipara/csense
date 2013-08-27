package edu.uiowa.csense.compiler.matlab.types;

/**
 * This will be the base class for the matlab primitive types
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public abstract class MatlabPrimitive<T> extends MatlabType {
    protected T _value;

    public MatlabPrimitive() {
    }

    public MatlabPrimitive(T value) {
	_value = value;
    }

    public abstract boolean hasZeros();
}
