package edu.uiowa.csense.compiler.targets;


public class DesktopTarget extends Target {
    public static final String PLATFORM = "desktop";

    public DesktopTarget() {
    }

    public DesktopTarget(String targetName, String dir) {
	platform = DesktopTarget.PLATFORM;
	baseStr = dir;
	name = targetName;
    }
}
