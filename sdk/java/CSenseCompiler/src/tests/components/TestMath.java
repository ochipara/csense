package tests.components;

import compiler.CompilerException;
import compiler.model.Project;
import compiler.types.BaseTypeC;
import components.math.DivideC;
import components.math.LimitInputC;

import messages.fixed.DoubleVector;

public class TestMath {
    public static void main(String[] args) throws CompilerException {
	Project project = new DesktopProject("TestMath");

	int numElements = 16;
	BaseTypeC frameT = project
		.templateType(DoubleVector.class, numElements);
	frameT.mapToJNI(new DoubleMatrix(numElements, 1));

	project.addComponent("divide", new DivideC(frameT));
	project.addComponent("limit", new LimitInputC(frameT, -1, 1));

	project.compile();

    }

}
