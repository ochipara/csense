package compiler.matlab.types;

public class NameValuePair {
    protected String _name;
    protected MatlabType _type;

    public NameValuePair(String name, MatlabType type) {
	_name = name;
	_type = type;
    }

    public String getName() {
	return _name;
    }

    public MatlabType getType() {
	return _type;
    }
}