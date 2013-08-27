package edu.uiowa.csense.compiler.utils;

public class MatlabCoder extends Coder {
    protected boolean newline = true;

    @Override
    public void code(String line) {
	int n = line.length();

	for (int i = 0; i < n; i++) {
	    char c = line.charAt(i);
	    if (newline && c != '}')
		indent();
	    if (isWhiteSpace(c) == false)
		newline = false;

	    switch (c) {
	    case '\n':
	    case '\t':
		newline = true;
		break;

	    case '\r':
		_code.append(c);
		newline = true;
		break;
	    case ';':
		_code.append(";");

		i = advance(line, i + 1, n);
		_code.append("\r");
		newline = true;
		break;
	    default:
		_code.append(c);
	    }
	}
    }

    protected int advance(String buf, int pos, int n) {
	while (pos < n) {
	    if (isWhiteSpace(buf.charAt(pos)))
		pos++;
	    else
		break;
	}

	return pos - 1;
    }
}
