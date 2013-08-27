package edu.uiowa.csense.compiler.model;

import java.util.Iterator;
import java.util.Stack;






import edu.uiowa.csense.compiler.CSenseComponentC.ThreadingOption;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class Path implements Cloneable, Iterable<IComponentC> {
    protected Stack<IComponentC> _path = null;

    public Path(Stack<IComponentC> path) {
	_path = path;
    }

    public Path() {
	_path = new Stack<IComponentC>();
    }

    public void push(IComponentC component) {
	_path.push(component);
    }

    @Override
    public Object clone() {
	Stack<IComponentC> newpath = (Stack<IComponentC>) _path.clone();
	return new Path(newpath);
    }

    public IComponentC pop() {
	IComponentC component = _path.pop();
	return component;
    }

    public IComponentC get(int index) {
	return _path.get(index);
    }

    public int size() {
	return _path.size();
    }

    @Override
    public Iterator<IComponentC> iterator() {
	return _path.iterator();
    }

    @Override
    public String toString() {
	String str = "";
	for (IComponentC c : _path) {
	    str += c.getVariableName() + " ";
	}

	return str;
    }

    public int numThreadedComponents() {
	int _numThreaded = 0;
	for (IComponentC component : _path) {
	    if (component.getThreadType() != ThreadingOption.NONE) {
		_numThreaded = _numThreaded + 1;
	    }
	}
	return _numThreaded;
    }

}
