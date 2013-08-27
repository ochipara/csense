package edu.uiowa.csense.compiler.models.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uiowa.csense.compiler.CompilerException;

public class AndroidManifest {
    private Document document;
    private Element root;
    private Element application;
    private String packageName;
    private String mainActivity;

    public AndroidManifest(File manifest) throws CompilerException {
	try {
	    document = AndroidManifest.parse(manifest);
	    root = document.getDocumentElement();	    
	    application = (Element) root.getElementsByTagName("application").item(0);
	    if (application.hasAttribute("android:allowBackup") == false) {
		application.setAttribute("android:allowBackup", "false");
	    }
	    
	    boolean done = false;
	    NodeList activities = application.getElementsByTagName("activity");
	    for(int i = 0; !done && i < activities.getLength(); i++) {
		Element activity = (Element) activities.item(i);
		NodeList filters = activity.getElementsByTagName("intent-filter");
		if(filters.getLength() == 0) continue;
		for(int j = 0; !done && j < filters.getLength(); j++) {
		    Element filter = (Element)filters.item(j);
		    NodeList actions = filter.getElementsByTagName("action");
		    if(actions.getLength() == 0) break;
		    for(int k = 0; k < actions.getLength(); k++) {
			Element action = (Element)actions.item(k);
			if(action.getAttribute("android:name").equals("android.intent.action.MAIN")) {
			    mainActivity = activity.getAttribute("android:name");
			    done = true;
			    break;
			}
		    }
		}
	    }
	    packageName = root.getAttribute("package");
	} catch (Exception e) {
	    throw new CompilerException(e);
	}
    }
    
    public String getPackage() {
	return packageName;
    }
    
    public String getMainActivity() {
	return mainActivity;
    }

    public boolean hasPermission(String permission) {
	NodeList nodes = root.getElementsByTagName("uses-permission");
	for (int i = 0; i < nodes.getLength(); i++) {
	    Element node = (Element) nodes.item(i);
	    Attr nodePermission = node.getAttributeNode("android:name");
	    if (nodePermission.getValue().equals(permission)) {
		return true;
	    }
	}

	return false;
    }

    public void setVersion(int minSdkVersion, int targetSdkVersion, int maxSdkVersion) {
	Element sdk = (Element) root.getElementsByTagName("uses-sdk").item(0);
	if (sdk == null) {
	    sdk = document.createElement("uses-sdk");
	}
	sdk.setAttribute("android:minSdkVersion", Integer.toString(minSdkVersion));
	sdk.setAttribute("android:targetSdkVersion", Integer.toString(targetSdkVersion));
	sdk.setAttribute("android:maxSdkVersion", Integer.toString(maxSdkVersion));

	root.insertBefore(sdk, root.getFirstChild());
    }

    public void addPermission(String permission) {
	if (hasPermission(permission))
	    return;

	Element permissionElement = document.createElement("uses-permission");
	permissionElement.setAttribute("android:name", permission);

	root.insertBefore(permissionElement, application);
	//root.appendChild(permissionElement);
    }

    public Element getApplication() throws CompilerException {	
	return application;
    }

    public Element newActivity(String name) throws CompilerException {
	Element application = getApplication();
	Element activity = document.createElement("activity");
	activity.setAttribute("android:name", name);
	application.appendChild(activity);

	return activity;
    }

    public void newMainActivity(String name) throws CompilerException {
	mainActivity = name;
	Element activity = newActivity(name);
	Element intentFilter = document.createElement("intent-filter");
	activity.appendChild(intentFilter);

	Element action = document.createElement("action");
	action.setAttribute("android:name", "android.intent.action.MAIN");
	intentFilter.appendChild(action);

	Element category = document.createElement("category");
	category.setAttribute("android:name", "android.intent.category.LAUNCHER");
	intentFilter.appendChild(category);
    }

    public Element newService(String name) throws CompilerException {
	Element app = getApplication();
	Element service = document.createElement("service");
	service.setAttribute("android:name", name);

	app.appendChild(service);
	return service;
    }

    public String converToString() throws TransformerException {
	TransformerFactory tf = TransformerFactory.newInstance();
	Transformer transformer = tf.newTransformer();
	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	transformer.setOutputProperty(
		"{http://xml.apache.org/xslt}indent-amount", "4");

	StringWriter out = new StringWriter();

	transformer.transform(new DOMSource(document), new StreamResult(out));
	return out.getBuffer().toString();
    }

    public void write(File manifest) throws CompilerException {
	try {
	    FileWriter manifestFile;
	    manifestFile = new FileWriter(manifest);
	    manifestFile.write(converToString());
	    manifestFile.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	} catch (TransformerException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}

    }

    private static Document parse(File manifest)
	    throws ParserConfigurationException, FileNotFoundException,
	    SAXException, IOException {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory
		.newInstance();
	DocumentBuilder builder = builderFactory.newDocumentBuilder();

	Document document = builder.parse(new FileInputStream(manifest));
	return document;
    }

    public boolean removeActivity(String name) throws CompilerException {
	Element application = getApplication();
	NodeList nodes = application.getElementsByTagName("activity");
	for (int i = 0; i < nodes.getLength();  i++) {
	    Element activity = (Element)nodes.item(i);
	    if(activity.getAttribute("android:name").equals(name)) {
		application.removeChild(activity);
		return true;
	    }
	}
	return false;
    }

    public static void main(String[] args) throws CompilerException,
    TransformerException {
	File f = new File(
		"/Users/ochipara/Working/CSense/svn/trunk/csense/systems/Audiology/gen/AndroidManifest.xml");
	AndroidManifest manifest = new AndroidManifest(f);
	manifest.addPermission("foo");
	System.out.println(manifest.converToString());
    }

}
