package compiler.types;

import compiler.CompilerException;
import compiler.matlab.types.MatlabType;
import api.Message;

public class JavaTypeC extends BaseTypeC {
    public JavaTypeC(Class<? extends Message> messageType) {
	super(messageType);
	_elementSize = 1;
    }

    @Override
    public JavaTypeC clone() {
	return new JavaTypeC(_messageType);
    }

    @Override
    public String getSimpleName() {
	return _messageType.getSimpleName();
    }

    @Override
    public MatlabType getMatlabType() {
	return null;
    }

    @Override
    public int getNumberOfElements() throws CompilerException {
	return 1;
    }

    @Override
    public void setColumns(int cols) {
	if (cols != 1) {
	    throw new IllegalArgumentException();
	}
    }

    @Override
    public void setRows(int rows) {
	if (rows != 1) throw new IllegalArgumentException();	
    }

    @Override
    public void setMultiplier(int rows, int cols) {
	if (rows != 1) throw new IllegalArgumentException();
	if (cols != 1) throw new IllegalArgumentException();
    }
    
//    @Override
//    public void addConstraint(Constraint constraint) {
//	throw new RuntimeCompilerException("Cannot add constraints to java types");
//    }

    @Override
    public int getMultipler(int dimension) {
	return 1;
    }

    @Override
    public String toString() {
	return "JavaType " + _messageType.getSimpleName(); 
    }
}
