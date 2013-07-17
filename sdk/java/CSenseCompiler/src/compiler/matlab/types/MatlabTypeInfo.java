package compiler.matlab.types;

import java.nio.Buffer;

/**
 * Stores all the information regarding the type metadata to be used during code
 * generation this includes - rows, columns - codegenType -- the underlying
 * matlab type - nioType -- the underlying NIO type to be used
 * 
 * 
 * @author ochipara
 * 
 */
public class MatlabTypeInfo {
    protected int _rows;
    protected int _columns;
    protected String _underlyingMatlabType;
    protected Class<? extends Buffer> _nioType;

    public MatlabTypeInfo(int rows, int columns, String underlyingMatlabType,
	    Class<? extends Buffer> nioType) {
	_rows = rows;
	_columns = columns;
	_underlyingMatlabType = underlyingMatlabType;
	_nioType = nioType;
    }

}
