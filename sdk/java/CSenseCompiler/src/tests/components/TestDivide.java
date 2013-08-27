package tests.components;

import components.math.DivideC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.types.DoubleVector;

public class TestDivide {
    public static void main(String[] args) throws CSenseException,
	    CompilerException {
	Project project = new DesktopProject("TestDivide");

	int numElements = 16;
	BaseTypeC frameT = project
		.templateType(DoubleVector.class, numElements);
	frameT.mapToJNI(new DoubleMatrix(numElements, 1));

	project.addComponent("divide", new DivideC(frameT));

	project.compile();

    }

}
