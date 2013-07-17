package project.builders;

import java.io.File;

import compiler.CompilerException;
import compiler.RuntimeCompilerException;
import compiler.model.Project;
import compiler.utils.Shell;

public class AntBuilder extends ProjectBuilder {
    private Project _project;
    private File _wd;
    private boolean _release;
    private String _keystore, _passwd1;
    private String _alias, _passwd2;


    public AntBuilder(Project project) {
	this(project, "", "", "", "");
    }
    
    public AntBuilder(Project project, String keystore, String passwd1, String alias, String passwd2) {
	_project = project;
	_wd = _project.getTarget().getDirectory();
	_keystore = keystore;
	_alias = alias;
	_passwd1 = passwd1;
	_passwd2 = passwd2;
    }

    @Override
    public void make(String target) {
	String command = "ant " + target;
	try {
	    if(Shell.exec(command, _wd) != Shell.EXIT_SUCCESS) throw new RuntimeCompilerException("failed to make " + target);
	} catch (CompilerException e) {
	    throw new RuntimeCompilerException(e);
	}	
    }

    @Override
    public void build(boolean release) throws CompilerException {
	if(_release = release) make(RELEASE, "-Dkey.store=" + _keystore, "-Dkey.store.password=" + _passwd1, "-Dkey.alias=" + _alias, "-Dkey.alias.password=" + _passwd2);
	else make(DEBUG);
    }
    
    @Override
    public void install() throws CompilerException {
	if(_release) make("installr");
	else make("installd");
    }
}
