package edu.uiowa.csense.compiler.matlab;

import java.util.Comparator;

public class MatlabArgumentComparator implements Comparator<MatlabArgument> {

    @Override
    public int compare(MatlabArgument x, MatlabArgument y) {
	if (x.getOutputType() < y.getOutputType()) {
	    return -1;
	} else if (x.getOutputType() > y.getOutputType()) {
	    return 1;
	} else {
	    return 0;
	}
    }

}
