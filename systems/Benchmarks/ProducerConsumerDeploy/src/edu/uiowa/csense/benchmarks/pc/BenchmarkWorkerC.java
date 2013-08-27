package edu.uiowa.csense.benchmarks.pc;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;


public class BenchmarkWorkerC extends CSenseComponentC {
    public BenchmarkWorkerC(BaseTypeC type, long delay) throws CompilerException {
	super(BenchmarkWorker.class);
	addIOPort(type, "data");
	
	// you can add a port as an argument in this case you will get the type of the power
	addArgument(new ArgumentC(getInputPort("dataIn")));
	addArgument(new ArgumentC(delay));
	setThreadingOption(ThreadingOption.CSENSE);
    }
}
