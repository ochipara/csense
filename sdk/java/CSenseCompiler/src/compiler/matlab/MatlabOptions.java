package compiler.matlab;

public class MatlabOptions {
    public static boolean generateAssertions = true;
    public static boolean printPostResults = true;
    public static boolean printPreResults = true;

    public static void debugOff() {
	printPostResults = false;
	printPreResults = false;
    }
}
