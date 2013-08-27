package components.matlab;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.matlab.MatlabPersistent;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.matlab.types.MatlabStruct;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.types.BaseTypeC;


public class VadC extends MatlabComponentC {
    public InputPortC in_gamma = null;
    public InputPortC in_active = null;
    public OutputPortC out_gamma = null;
    public OutputPortC out_active = null;

    public VadC(BaseTypeC gammaT, BaseTypeC activityT, int frameSize,
	    double freq) throws CompilerException {
	super("VadGroupM");

	addIOPort(gammaT, "gamma");
	addIOPort(activityT, "activity");

	// matlab specific configuration
	addMatlabInput(new MatlabParameter("gam_in", MatlabArgument.INPUT,
		getInputPort("gammaIn"), getOutputPort("gammaOut")));
	addMatlabOutput(new MatlabParameter("active", MatlabArgument.OUTPUT,
		getInputPort("activityIn"), getOutputPort("activityOut")));

	/*
	 * lggami: 0 nv: 0 gg: 0 xu: [1x257 double] fs: 16000
	 */
	MatlabStruct local = new MatlabStruct("VadLocalState");
	local.addValue("lggami", 0.0);
	local.addValue("nv", 0.0);
	local.addValue("gg", 0.0);
	local.addValue("xu",
		MLDoubleMatrix.ones(1, gammaT.getNumberOfElements()));
	local.addValue("fs", freq);
	System.out.println("VadLocalState:");
	local.display();

	MatlabPersistent localPersistent = new MatlabPersistent("local", local);
	addMatlabInput(localPersistent);
	addMatlabOutput(localPersistent);

	/*
	 * of: 2 pr: 0.7000 ts: 0.1000 tn: 0.0500 ti: 0.0320 ri: 1 ta: 0.3960
	 * gx: 1000 gz: 1.0000e-04 xn: 0 fs: 16000 ni: 512
	 */

	MatlabStruct params = new MatlabStruct("VadParams");
	params.addValue("of", 2);
	params.addValue("pr", .7);
	params.addValue("ts", 0.1);
	params.addValue("tn", 0.0500);
	params.addValue("ti", frameSize / freq);
	params.addValue("ri", 1);
	params.addValue("ta", 0.3960);
	params.addValue("gx", 1000);
	params.addValue("gz", 1e-4);
	params.addValue("xn", 0);
	params.addValue("fs", freq);
	params.addValue("ni", frameSize);
	System.out.println("VadParams:");
	params.display();
	addMatlabInput(new edu.uiowa.csense.compiler.matlab.MatlabConstant("vad_params",
		params));
    }

}
