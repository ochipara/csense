package compiler.model;

import java.util.Comparator;

public class PathComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
	return -compare2(o1, o2);
    }

    public int compare2(Path o1, Path o2) {
	if (o1.numThreadedComponents() < o2.numThreadedComponents()) {
	    return -1;
	} else if (o1.numThreadedComponents() == o2.numThreadedComponents()) {
	    if (o1.size() < o2.size()) {
		return -1;
	    } else if (o1.size() == o2.size()) {
		return 0;
	    } else {
		return 1;
	    }
	} else {
	    return 1;
	}
    }

}
