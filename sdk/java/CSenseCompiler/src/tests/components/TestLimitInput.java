package tests.components;

import components.math.DivideC;
import components.math.LimitInputC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.runtime.types.DoubleVector;

public class TestLimitInput {
    public static void main(String[] args) throws CompilerException {
	Project project = new DesktopProject("TestLimitInput");

	int numElements = 16;
	BaseTypeC frameT = project
		.templateType(DoubleVector.class, numElements);
	frameT.mapToJNI(new DoubleMatrix(numElements, 1));

	project.addComponent("limit", new LimitInputC(frameT, -1, 1));

	project.compile();

    }

}
