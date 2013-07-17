package tests.components;

import compiler.CompilerException;
import compiler.model.Project;
import compiler.types.BaseTypeC;
import components.math.DivideC;

import messages.fixed.DoubleVector;
import api.CSenseException;

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
