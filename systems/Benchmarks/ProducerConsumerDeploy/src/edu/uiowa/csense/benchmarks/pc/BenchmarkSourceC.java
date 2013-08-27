package edu.uiowa.csense.benchmarks.pc;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;


public class BenchmarkSourceC extends CSenseSourceC {
    public BenchmarkSourceC(BaseTypeC type, long delay, long burst) throws CompilerException {
	super(BenchmarkSource.class, type);
	addOutputPort(type, "out");
	
	addArgument(new ArgumentC(delay));
	addArgument(new ArgumentC(burst));
	setThreadingOption(ThreadingOption.CSENSE);
	
	
	addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
    }
}
