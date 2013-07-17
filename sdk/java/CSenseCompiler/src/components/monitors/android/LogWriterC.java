package components.monitors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.types.JavaTypeC;
import compiler.types.TypeInfoC;

public class LogWriterC extends CSenseSourceC {
    public static final JavaTypeC logWriterT = TypeInfoC.newJavaMessage(LogMessage.class);
    public LogWriterC() throws CompilerException {
	super(LogWriter.class, logWriterT);
	addOutputPort(logWriterT, "out");	
	setThreadingOption(ThreadingOption.CSENSE);
    }
}