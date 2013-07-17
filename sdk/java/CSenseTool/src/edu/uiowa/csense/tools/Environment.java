package edu.uiowa.csense.tools;

import java.io.File;

import compiler.CompilerException;




import project.configuration.ToolkitConfiguration;

public class Environment {
    private final ToolkitConfiguration sdkConfig;

    public Environment() throws CompilerException {
	sdkConfig = ToolkitConfiguration.loadConfiguration(new File("csense.xml"));

    }

    public ToolkitConfiguration getSdkConfig() {
	return sdkConfig;
    }
}
