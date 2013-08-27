package api;

import java.util.HashMap;
import java.util.Map;

public class Command {
	protected ICommandHandler _source;
	protected String _command;
	protected Map<String, Object> arguments = new HashMap<String, Object>();

	public Command(ICommandHandler source, String command) {
		_source = source;
		_command = command;
	}

	public String getName() {
		return _command;
	}

	public ICommandHandler getSource() {
		return _source;
	}
	
	public void put(String argName, Object value) {
		arguments.put(argName, value);
	}
	
	public Object get(String argName) {
		return arguments.get(argName);
	}
}
