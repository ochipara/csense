package compiler.matlab;

public class MatlabType {
    protected String _type;
    protected int _size;

    public MatlabType(String type, int size) {
	this._type = type;
	this._size = size;
    }

    public MatlabType(String type) {
	this._type = type;
	this._size = 0;
    }
}
