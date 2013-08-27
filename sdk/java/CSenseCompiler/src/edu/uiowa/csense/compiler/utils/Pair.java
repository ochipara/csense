package edu.uiowa.csense.compiler.utils;

public class Pair<T1, T2> {
    T1 name;
    T2 value;

    public Pair(T1 name, T2 value) {
	this.name = name;
	this.value = value;
    }

    public T1 getName() {
	return name;
    }

    public T2 getValue() {
	return value;
    }
}
