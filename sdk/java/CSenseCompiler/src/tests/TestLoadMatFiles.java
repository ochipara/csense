package tests;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;

public class TestLoadMatFiles {

    public static void test1() throws IOException {
	File f = new File(
		"/Users/ochipara/Working/EgoSense/trunk/csense/apps/egosense/filter.mat");

	MatFileFilter filter = new MatFileFilter();
	filter.addArrayName("filter");

	MatFileReader reader = new MatFileReader(f, filter);
	Map<String, MLArray> data = reader.getContent();

	Set<String> vars = data.keySet();

	for (Iterator<String> var = vars.iterator(); var.hasNext();) {
	    String varName = var.next();
	    MLArray varData = data.get(varName);
	    System.out.println("name: " + varName);
	    System.out.println("data: " + varData);
	}
    }

    public static void main(String[] args) throws IOException {
	File f = new File(
		"/Users/ochipara/Working/CSense/apps/egonsese/desktop/EgoSenseApp/configuration.mat");

	MatFileFilter filter = new MatFileFilter();
	filter.addArrayName("en_local");

	MatFileReader reader = new MatFileReader(f);
	Map<String, MLArray> data = reader.getContent();
	Set<String> vars = data.keySet();

	for (Iterator<String> var = vars.iterator(); var.hasNext();) {
	    String varName = var.next();
	    MLArray varData = data.get(varName);
	    System.out.println("name: " + varName);
	    System.out.println("data: " + varData);
	}

    }
}
