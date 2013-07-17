package components.test;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

public class BenchmarkSourceC extends CSenseComponentC {
    public BaseTypeC benchmarkMessageType = TypeInfoC.newJavaMessage(BenchmarkMessage.class);

    public BenchmarkSourceC(long delay, int burst) throws CompilerException {
	super(BenchmarkSource.class);

	addOutputPort(benchmarkMessageType, "out");

	addArgument(new ArgumentC(delay));
	addArgument(new ArgumentC(burst));

	setThreadingOption(ThreadingOption.CSENSE);
    }

    public BaseTypeC getType() {	
	return benchmarkMessageType;
    }

}
