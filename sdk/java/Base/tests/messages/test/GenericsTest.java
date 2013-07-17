package messages.test;

import java.util.ArrayList;
import java.util.List;

public class GenericsTest {

    public static void main(String[] args) {
	List<Number> l = new ArrayList<Number>();

	Long long1 = Long.valueOf(0);
	l.add(long1);
    }
}
