package components.test;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

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
