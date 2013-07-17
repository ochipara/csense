package compiler.types;

import compiler.CompilerException;

public class PrimitiveType extends BaseTypeC {
    public static final int PRIMITIVE_CHAR = 1;
    public static final int PRIMITIVE_BYTE = 2;
    public static final int PRIMITIVE_INT = 3;
    public static final int PRIMITIVE_SHORT = 4;
    public static final int PRIMITIVE_FLOAT = 5;
    public static final int PRIMITIVE_DOUBLE = 6;

    private final int type;

    public PrimitiveType(int type) {
	super();
	this.type = type;
	this._columns = 1;
	this._rows = 1;
    }
    
    @Override
    public PrimitiveType clone() {
	return new PrimitiveType(this.type);
    }

    public int getType() {
	return type;
    }

    @Override
    public int getNumberOfElements() throws CompilerException {
	return 1;
    }

    @Override
    public void setColumns(int cols) {
	if (cols != 1) throw new IllegalArgumentException();
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
    

    @Override
    public String toString() {
	switch(type) {
	case PRIMITIVE_BYTE:
	    return "byte";
	case PRIMITIVE_CHAR:
	    return "char";
	case PRIMITIVE_DOUBLE:
	    return "double";
	case PRIMITIVE_FLOAT:
	    return "float";
	case PRIMITIVE_INT:
	    return "int";
	case PRIMITIVE_SHORT:
	    return "short";
	default:
	    return "unkown";
	}
    }

    @Override
    public int getMultipler(int dimension) {
	return 1;
    }
}


