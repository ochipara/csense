package edu.uiowa.csense.compiler.model;

import java.io.File;

public class Configuration {
    public static final String ANDROID_NDK_BUILD = new File("../../../android/ndk/ndk-build").getAbsolutePath();
    public static final String ANDROID_TOOLS = new File("../../../android/sdks/tools").getAbsolutePath();
    public static final String ANDROID = new File(ANDROID_TOOLS, "android").getAbsolutePath();
    public static final String SWIG = new File("/opt/local/bin/swig").getAbsolutePath();
    public static final String EGOSERVICE_LOCATION = "edu/uiowa/csense/EgoService.java";
    public static final String TEMPLATE_NAME = "EgoDeploy";
    public static final File BASE = new File("../");
}
