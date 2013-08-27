/**
 *
 * The MIT License
 *
 * Copyright (c) 2011 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package base.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uiowa.csense.profiler.Utility;

/**
 *
 * @author Julien Chastang (julien.c.chastang at gmail dot com)
 */
public class LineChartTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
    }
    
    //@Test
    public void testBoxPlot() throws IOException {    	
    	double[][] series1 = new double[][]{
    			new double[]{5,10,3,2},
    			new double[]{25,30,27,24},
    			new double[]{40,45,47,39},
    			new double[]{55,63,59,80},
    			new double[]{30,40,35,30},
    			new double[]{30,36,32,46},
    			new double[]{3.59,5.72,8.57,10.73},
    	};
    	
    	double[][] series2 = new double[][]{
    			new double[]{31969.309 ,40703.354 ,40976.889 ,66063.289 ,42004.452 ,209907.641 ,356009.983},
    			new double[]{144711.256,595526.110,464832.515,586127.100,738330.911,762604.179 ,743117.340},
    			new double[]{939893.213,860080.305,886133.293,943028.762,940541.256,1014993.484,975113.705},
    			new double[]{966838.039,947670.016,939648.709,995855.090,973934.933,1056237.239,978555.652},
    			new double[]{778395.348,788417.652,687839.218,763929.174,880754.724,980369.046 ,761225.320},
    			new double[]{894118.365,841397.198,800193.109,836410.155,885698.719,940894.080 ,846371.462},
    	};
    	
    	double[][] series3 = new double[][]{
    			new double[]{57956.567 ,651678.071 ,609544.244 ,757581.497, 875915.222},
    			new double[]{432675.666,704840.847,691845.220,946595.003,951048.579},
    			new double[]{666178.136,796711.176,763032.597,1006133.389,1003774.191},
    			new double[]{764993.880,798237.492,789908.134,1007682.572,1004741.375},
    			new double[]{473476.297,732021.692,719446.633,963221.759,971652.638},
    			new double[]{465417.551,727385.421,711243.124,949269.750,963848.449},
    	};
    	
    	String xCaption = "Number of Messages";
    	String[] xLabels1 = new String[]{"500","100","1500","2000",};
    	String[] xLabels2 = new String[]{"100","500","1000","5000","10000","50000","100000"};
    	String[] xLabels3 = new String[]{"10000","50000","100000","500000","1000000"};
    	String yCaption = "Throughput(msgs/s)";
    	int yPrecision = 3;
    	
    	Plotter plotter = new Plotter("Throughput vs Messages");
    	String url = plotter.drawBoxChart(1, series1, xCaption, xLabels1, yCaption, yPrecision);
    	Utility.browse(url);
    }
    
    @Test
    public void example2() throws IOException {
        // EXAMPLE CODE START
        // Defining Line
        final double[] sp500 = { 62.960, 74.560, 84.300, 92.200, 95.890, 103.800, 91.600, 92.270, 96.870, 116.930, 97.540, 67.160, 89.770, 106.880, 94.750, 96.280, 107.840, 135.330, 122.300, 140.340, 164.860, 166.260, 210.680, 243.370,
                247.840, 277.080, 350.680, 328.710, 415.140, 438.820, 468.660, 460.920, 614.120, 753.850, 970.840, 1231.93, 1464.47, 1334.22, 1161.02, 879.390, 1109.64, 1213.55, 1258.17, 1424.71, 1475.25 };
        final double INFLATION = 0.035;

        final double[] inflation = new double[sp500.length];
        inflation[0] = sp500[0];
        for (int i = 1; i < inflation.length; i++) {
            inflation[i] = inflation[i-1] *INFLATION + inflation[i-1];
        }

        Line line1 = Plots.newLine(DataUtil.scaleWithinRange(0,1500,sp500), YELLOW, "S & P 500");
        line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        line1.addShapeMarkers(Shape.CIRCLE, YELLOW, 10);
        line1.addShapeMarkers(Shape.CIRCLE, BLACK, 7);
        line1.addShapeMarker(Shape.VERTICAL_LINE_PARTIAL, BLUE,3,8);
        line1.addShapeMarker(Shape.VERTICAL_LINE_PARTIAL, BLUE,3,17);
        line1.addShapeMarker(Shape.VERTICAL_LINE_PARTIAL, BLUE,3,24);
        line1.addShapeMarker(Shape.VERTICAL_LINE_PARTIAL, BLUE,3,40);
        line1.setFillAreaColor(LIGHTYELLOW);

        Line line2 = Plots.newLine(DataUtil.scaleWithinRange(0,1500,inflation), LIMEGREEN, "Inflation");
        line2.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        line2.addShapeMarkers(Shape.CIRCLE, LIME, 10);
        line2.addShapeMarkers(Shape.CIRCLE, BLACK, 7);
        line2.setFillAreaColor(LIGHTGREEN);


        // Defining chart.
        LineChart chart = GCharts.newLineChart(line1,line2);
        chart.setSize(600, 450);
        chart.setTitle("S & P 500|1962 - 2008", WHITE, 14);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels yAxis = AxisLabelsFactory.newNumericRangeAxisLabels(0, sp500[sp500.length-1]);
        yAxis.setAxisStyle(axisStyle);
        AxisLabels xAxis1 = AxisLabelsFactory.newAxisLabels(Arrays.asList("Fed Chiefs:","Burns","Volcker","Greenspan","Bernanke"), Arrays.asList(5,18,39,55,92));
        xAxis1.setAxisStyle(axisStyle);
        AxisLabels xAxis2 = AxisLabelsFactory.newNumericRangeAxisLabels(1962, 2008);
        xAxis2.setAxisStyle(axisStyle);
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Year", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));

        // Adding axis info to chart.
        chart.addYAxisLabels(yAxis);
        chart.addXAxisLabels(xAxis1);
        chart.addXAxisLabels(xAxis2);
        chart.addXAxisLabels(xAxis3);
        chart.setGrid(100, 6.78, 5, 0);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(BLACK));
        chart.setAreaFill(Fills.newSolidFill(Color.newColor("708090")));
        String url = chart.toURLString();
        Utility.browse(url);
    }
}
