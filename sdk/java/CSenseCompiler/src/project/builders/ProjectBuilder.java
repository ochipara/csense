package project.builders;

import compiler.CompilerException;

public abstract class ProjectBuilder {
    public static String INSTALL	= "install";
    public static String UNINSTALL 	= "uninstall";
    public static String RELEASE 	= "release";
    public static String DEBUG 		= "debug";
    public static String CLEAN 		= "clean";
    public static String TEST 		= "test";
    
    public void build() throws CompilerException {
	build(false);
    }
    
    public void build(boolean release) throws CompilerException {
	if(release) make(RELEASE); 
	else make(DEBUG);
    }
    
    public void install() throws CompilerException {
	make(INSTALL);
    }
    
    public void uninstall() throws CompilerException {
	make(UNINSTALL);
    }
    
    public void clean() throws CompilerException {
	make(CLEAN);
    }
    
    public void test() throws CompilerException {
	make(TEST);
    }
    
    public void make(String... targets) throws CompilerException {
	StringBuilder target = new StringBuilder();
	for(String s: targets) target.append(s).append(" ");
	make(target.toString());
    }
    
    public abstract void make(String target) throws CompilerException;
}
