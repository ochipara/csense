package components.test;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class BenchmarkWorkerC extends CSenseComponentC {
    public BaseTypeC benchmarkMessageType = TypeInfoC.newJavaMessage(BenchmarkMessage.class);

    public BenchmarkWorkerC(long delay) throws CompilerException {
	super(BenchmarkWorker.class);

	addInputPort(benchmarkMessageType, "in");
	addOutputPort(benchmarkMessageType, "out");

	addArgument(new ArgumentC(delay));

	setThreadingOption(ThreadingOption.CSENSE);
    }
}
