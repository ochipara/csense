package base.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uiowa.csense.profiler.Statistics;
import edu.uiowa.csense.runtime.api.CSenseException;

public class StatisticsTest {
    static private final String FILENAME = ".testStatistics.xml";
    static private final double DELTA = 1e-8;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	new File(FILENAME).delete();
    }

    @After
    public void tearDown() throws Exception {
	new File(FILENAME).delete();
    }

    @Test
    public void testMerge() {
	Statistics stat1 = new Statistics();
	stat1.set("name1", 123);
	stat1.set("name2", 123000L);
	stat1.set("name3", 123000.11234);

	Statistics stat2 = new Statistics("prefix");
	stat2.set("name1", 321);
	stat2.set("name2", 321000L);
	stat2.set("name3", 321000.11234);

	stat1.merge(stat2);
	stat1.setPrefix("prefix");
	assertEquals(321, stat1.getInt("name1").intValue());
	assertEquals(321000L, stat1.getLong("name2").longValue());
	assertEquals(321000.11234, stat1.getDouble("name3"), DELTA);
    }

    @Test
    public void testLoadSave() {
	Statistics stat = new Statistics();
	try {
	    stat.loadXML(".non-exist-filename");
	    fail("loadXML() should fail due to the absent file");
	} catch (CSenseException e) {
	}

	try {
	    stat.saveXML();
	    fail("output filename should be provided");
	} catch (CSenseException e) {
	}
    }

    @Test
    public void testStatistics() {
	Statistics stat = new Statistics();
	stat.set("name1", 123);
	stat.set("name2", 123000L);
	stat.set("name3", 123000.11234);
	assertEquals(123, stat.getInt("name1").intValue());
	assertEquals(123000L, stat.getLong("name2").longValue());
	assertEquals(123000.11234, stat.getDouble("name3"), DELTA);

	stat.setPrefix("prefix");
	stat.set("name1", 123);
	stat.set("name2", 123000L);
	stat.set("name3", 123000.11234);
	assertEquals(123, stat.getInt("name1").intValue());
	assertEquals(123000L, stat.getLong("name2").longValue());
	assertEquals(123000.11234, stat.getDouble("name3"), DELTA);

	stat.setPrefix(null);
	assertEquals(123, stat.getInt("name1").intValue());
	assertEquals(123000L, stat.getLong("name2").longValue());
	assertEquals(123000.11234, stat.getDouble("name3"), DELTA);
	assertEquals(123, stat.getInt("prefix.name1").intValue());
	assertEquals(123000L, stat.getLong("prefix.name2").longValue());
	assertEquals(123000.11234, stat.getDouble("prefix.name3"), DELTA);

	try {
	    stat.saveXML(FILENAME);
	} catch (CSenseException e) {
	    e.printStackTrace();
	    fail("exception should not be thrown");
	}

	Statistics stat2 = new Statistics("prefix");
	try {
	    stat2.loadXML(FILENAME);
	} catch (CSenseException e) {
	    e.printStackTrace();
	    fail("exception should not be thrown");
	}

	assertEquals(123, stat2.getInt("name1").intValue());
	assertEquals(123000L, stat2.getLong("name2").longValue());
	assertEquals(123000.11234, stat2.getDouble("name3"), DELTA);

	stat2.setPrefix(null);
	assertEquals(123, stat2.getInt("name1").intValue());
	assertEquals(123000L, stat2.getLong("name2").longValue());
	assertEquals(123000.11234, stat2.getDouble("name3"), DELTA);
	assertEquals(123, stat2.getInt("prefix.name1").intValue());
	assertEquals(123000L, stat2.getLong("prefix.name2").longValue());
	assertEquals(123000.11234, stat2.getDouble("prefix.name3"), DELTA);
    }

}
