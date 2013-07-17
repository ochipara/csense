package project.targets;

public class DesktopTarget extends Target {
    public static final String PLATFORM = "desktop";
    
    public DesktopTarget() {
	super();
	this.platform = PLATFORM;
    }
    
    public DesktopTarget(String targetName, String targetDir) {
	super(targetName);
	this.platform = PLATFORM;
	this.baseStr = targetDir;
    }
}
