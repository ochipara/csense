package components.sensors.android;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class GPSComponentC extends CSenseSourceC {
    public final static BaseTypeC gpsType = TypeInfoC.newJavaMessage(GPSMessage.class);

    public GPSComponentC() throws CompilerException {
	super("components.sensors.android.GPSComponent", gpsType);

	addOutputPort(gpsType, "out");
	addArgument(ArgumentC.self());
	addPermission("android.permission.ACCESS_FINE_LOCATION");
	setThreadingOption(ThreadingOption.CSENSE);
    }

}
