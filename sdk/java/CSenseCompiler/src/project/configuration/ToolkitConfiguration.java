package project.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import compiler.CompilerException;



@XmlRootElement(name = "csense")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolkitConfiguration {
    List<Tool> tools = new LinkedList<Tool>();

    private void addTool(Tool tool) {
	tools.add(tool);
    }

    public static void print(ToolkitConfiguration config) throws JAXBException {
	JAXBContext context = JAXBContext
		.newInstance(ToolkitConfiguration.class);
	Marshaller m = context.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	m.marshal(config, System.out);
    }

    public static ToolkitConfiguration loadConfiguration(File file)
	    throws CompilerException {
	JAXBContext context;
	try {
	    context = JAXBContext.newInstance(ToolkitConfiguration.class);
	    Unmarshaller u = context.createUnmarshaller();
	    ToolkitConfiguration config = (ToolkitConfiguration) u
		    .unmarshal(file);
	    return config;
	} catch (JAXBException e) {
	    throw new CompilerException(e);
	}
    }

    public static void main(String[] args) throws JAXBException {
	ToolkitConfiguration sdk = new ToolkitConfiguration();

	AndroidTool androidTool = new AndroidTool();
	androidTool.ndkBuild = new File(
		"/Users/ochipara/android-sdks/android-ndk/ndk-build");
	androidTool.sdkTools = new File("/Users/ochipara/android-sdks/tools");
	androidTool.setTarget("android-15");
	sdk.addTool(androidTool);

	SwigTool swigTool = new SwigTool();
	swigTool.swig = new File("/opt/local/bin/swig");
	sdk.addTool(swigTool);

	CompilerTool compilerTool = new CompilerTool();
	compilerTool
		.setDirectory("/Users/ochipara/Working/CSense/svn/trunk/src/CSenseCompiler");
	sdk.addTool(compilerTool);

	ToolkitConfiguration.print(sdk);
    }

    public Tool getTool(String name) {
	for (Tool tool : tools) {
	    if (name.equals(tool.getName()))
		return tool;
	}
	return null;
    }
}
