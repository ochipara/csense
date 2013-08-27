package edu.uiowa.csense.compiler.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Coder {
    protected int indent_level = 0;
    protected StringBuffer _code = new StringBuffer();

    public Coder() {
    }

    public void line(String line) {
	_code.append(line + "\n");
    }

    public void append(String line) {
	_code.append(line);
    }

    public void code(String line) {
	_code.append(line);
    }

    public void newline() {
	_code.append("\r");
    }

    public void indent() {
	for (int t = 0; t < indent_level; t++)
	    _code.append("\t");
    }

    public boolean isWhiteSpace(char c) {
	switch (c) {
	case ' ':
	case '\t':
	case '\r':
	case '\n':
	    return true;
	default:
	    return false;
	}
    }

    public void saveToFile(File f) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(f));
	writer.write(_code.toString());
	writer.close();
    }

    @Override
    public String toString() {
	return _code.toString();
    }

    public static String list2string(List<String> args) {
	StringBuffer sb = new StringBuffer();

	boolean first = true;
	for (String arg : args) {
	    if (first == true) {
		sb.append(arg);
		first = false;
	    } else {
		sb.append(", " + arg);
	    }
	}

	return sb.toString();
    }

    public static String objects2string(List<Object> args) {
	StringBuffer sb = new StringBuffer();

	boolean first = true;
	for (Object arg : args) {
	    if (first == true) {
		sb.append(arg);
		first = false;
	    } else {
		sb.append(", " + arg);
	    }
	}

	return sb.toString();
    }

}
