package base.workspace;

import java.util.HashMap;
import java.util.Map;

public class Workspace {
    /**
     * The workspace is used by the toolkit as a shared data structure. 
     * Common uses include
     *   - initialization of components 
     * 
     */
    protected final Map<String, Object> values = new HashMap<String, Object>();
    protected final static Workspace workspace = new Workspace();

    synchronized public void setValue(Variable var, Object value) {
	values.put(var.getName(), value);
    }

    synchronized public void setValue(String variableName, Object value) {
	values.put(variableName, value);		
    }

    synchronized public Object getValue(Variable var) {
	return values.get(var.getName());
    }

    synchronized public boolean hasVariable(Variable var) {
	return values.containsKey(var.getName());
    }

    public static Workspace getWorkspace() {
	return workspace;
    }

    public Object getValue(String string) {
	return values.get(string);
    }
}
