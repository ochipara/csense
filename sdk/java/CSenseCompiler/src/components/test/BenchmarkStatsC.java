package components.test;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

public class BenchmarkStatsC extends CSenseComponentC {
    public BaseTypeC benchmarkMessageType = TypeInfoC.newJavaMessage(BenchmarkMessage.class);

    public BenchmarkStatsC() throws CompilerException {
	super(BenchmarkStats.class);

	addInputPort(benchmarkMessageType, "in");
	addOutputPort(benchmarkMessageType, "out");
    }
}
