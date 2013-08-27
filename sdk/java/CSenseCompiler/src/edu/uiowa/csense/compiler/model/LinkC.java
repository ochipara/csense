package edu.uiowa.csense.compiler.model;

public class LinkC {
    protected OutputPortC _source = null;
    protected InputPortC _destination = null;

    public LinkC(OutputPortC source, InputPortC destination) {
        _source = source;
        _destination = destination;
    }

    public OutputPortC getSource() {
        return _source;
    }

    public InputPortC getDestination() {
        return _destination;
    }

    @Override
    public String toString() {
        return _source.toString() + "=>" + _destination.toString();
    }
}
