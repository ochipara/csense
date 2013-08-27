package components.test;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class BenchmarkStatsC extends CSenseComponentC {
    public BaseTypeC benchmarkMessageType = TypeInfoC.newJavaMessage(BenchmarkMessage.class);

    public BenchmarkStatsC() throws CompilerException {
	super(BenchmarkStats.class);

	addInputPort(benchmarkMessageType, "in");
	addOutputPort(benchmarkMessageType, "out");
    }
}
