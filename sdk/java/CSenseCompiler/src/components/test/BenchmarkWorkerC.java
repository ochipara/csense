package components.test;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

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
