package components.monitors.android;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.android.monitors.LogMessage;
import edu.uiowa.csense.components.android.monitors.LogWriter;

public class LogWriterC extends CSenseSourceC {
    public static final JavaTypeC logWriterT = TypeInfoC.newJavaMessage(LogMessage.class);
    public LogWriterC() throws CompilerException {
	super(LogWriter.class, logWriterT);
	addOutputPort(logWriterT, "out");	
	setThreadingOption(ThreadingOption.CSENSE);
    }
}