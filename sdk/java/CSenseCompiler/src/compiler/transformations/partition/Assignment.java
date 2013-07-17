package compiler.transformations.partition;

public class Assignment {
    int _domain;

    public Assignment(int domain) {
	_domain = domain;
    }

    public int getDomain() {
	return _domain;
    }

}
