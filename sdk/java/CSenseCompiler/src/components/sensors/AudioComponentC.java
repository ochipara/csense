package components.sensors;

import messages.fixed.ShortVector;
import project.targets.DesktopTarget;
import project.targets.android.AndroidTarget;
import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.model.Project;
import compiler.types.FrameTypeC;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.GreaterEqual;


public class AudioComponentC extends CSenseSourceC {
    protected static int minBuffer = 1000;
    public static FrameTypeC audioType = null;

    public AudioComponentC(FrameTypeC audioType, int rate) throws CompilerException {
	super(audioType);
	
	// TODO: better api to allow for the handling of constraints
	boolean addConstraint = true;
	for (Constraint constraint : audioType.getConstraints()) {
	    if (constraint.getDimension() == Constraint.COLUMN_DIMENSION) {
		if (constraint instanceof GreaterEqual) {
		    if (constraint.getValue() >= minBuffer) {
			addConstraint = false;
			break;
		    }
		} 
	    }
	}
	
	if (addConstraint) {
	    audioType.setConstraint(new GreaterEqual(minBuffer));
	}
	
	
	String platform = Project.getPlatform();
	if (AndroidTarget.PLATFORM.equals(platform)) {
	    if (audioType.getMessageType() != ShortVector.class) throw new CompilerException("Unsupported audioType");
	      
	    setComponent("components.sensors.android.AudioComponent");
	    addArgument(new ArgumentC(rate));	    
	    addOutputPort(audioType, "audio");
	    
	    addPermission("android.permission.RECORD_AUDIO");
	    setThreadingOption(ThreadingOption.ANDROID);
	} else if (DesktopTarget.PLATFORM.equals(platform)) {
	    throw new UnsupportedOperationException();
	    //	    setComponent(AudioComponent.class);
	    //
	    //	    addArgument(new ArgumentC(rate));
	    //	    addArgument(new ArgumentC(samplesPerFrame));
	    //	    addOutputPort(TypeInfoC.newDoubleVector(samplesPerFrame), "audio");
	    //
	    //	    // will execute in an android thread
	    //	    setThreadingOption(ThreadingOption.ANDROID);
	} else
	    throw new CompilerException("Could not instantated object for platform [" + platform + "]");
    }
}