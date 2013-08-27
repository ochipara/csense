package edu.uiowa.csense.compiler.utils;

public class JavaCoder extends Coder {
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

	    case '{':
		_code.append(c);
		indent_level += 1;

		i = advance(line, i + 1, n);
		_code.append("\r");
		newline = true;
		break;
	    case '}':
		indent_level -= 1;
		i = advance(line, i + 1, n);
		if (line.startsWith("catch", i + 1)) {
		    indent();
		    _code.append("} catch");
		    i = i + 5;
		} else {
		    indent();
		    _code.append(c + "\r");
		    newline = true;
		}
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

    public void setIndent(int i) {
	indent_level = i;
    }

    public void comment(String string) {
	indent();
	_code.append("// " + string + "\n");
	newline = true;
    }

    public void annotation(String string) {
	indent();
	_code.append(string + "\n");
	newline = true;
    }
}
