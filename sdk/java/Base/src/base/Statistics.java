package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import api.CSenseException;

public class Statistics {
	private String _prefix;
	private String _filename;
	private Properties _properties;
	
	public Statistics() {
		_properties = new Properties();
	}
	
	public Statistics(String prefix) {
		this();
		_prefix = prefix;
	}
	
	public Statistics(Statistics stat) {
		_properties = new Properties(stat.getProperties());
		_prefix = stat.getPrefix();
	}
	
	private Properties getProperties() {
		return _properties;
	}
	
	private String key(String name) {
		return _prefix == null || _prefix.equals("") ? name : Utility.toString(_prefix, ".", name);
	}
	
	public String getPrefix() {
		return _prefix;
	}
	
	public void setPrefix(String prefix) {
		_prefix = prefix;
	}
	
	public void remove(String name) {
		_properties.remove(name);
	}

	public void set(String name, String value) {
		_properties.setProperty(key(name), value);
	}
	
	public void set(String name, boolean value) {
		set(name, String.valueOf(value));
	}
	
	public void set(String name, int value) {
		set(name, String.valueOf(value));
	}
	
	public void set(String name, long value) {
		set(name, String.valueOf(value));
	}
	
	public void set(String name, double value) {
		set(name, String.valueOf(value));
	}
	
	public String get(String name) {
		return _properties.getProperty(key(name));
	}
	
	public Boolean getBoolean(String name) {
		String value = _properties.getProperty(key(name));
		return value == null ? null : Boolean.valueOf(value);
	}
	
	public Integer getInt(String name) {
		String value = _properties.getProperty(key(name));
		return value == null ? null : Integer.valueOf(value);
	}
	
	public Long getLong(String name) {
		String value = _properties.getProperty(key(name));
		return value == null ? null : Long.valueOf(value);
	}
	
	public Double getDouble(String name) {
		String value = _properties.getProperty(key(name));
		return value == null ? null : Double.valueOf(value);
	}
	
	public void merge(Statistics stat) {
		Properties properties = stat.getProperties();
		for(String name: properties.stringPropertyNames())
			_properties.setProperty(name, properties.getProperty(name));
	}
	
	public void loadXML(String filename) throws CSenseException {
		try {
			FileInputStream fis = new FileInputStream(filename);
			_properties.loadFromXML(fis);
			_filename = filename;
			fis.close();
		} catch (FileNotFoundException e) {
			throw new CSenseException(filename + " does not exist", e); 
		} catch (InvalidPropertiesFormatException e) {
			throw new CSenseException(filename + " has incorrect file format", e); 
		} catch (IOException e) {
			throw new CSenseException("failed to access " + filename, e); 
		}
	}
	
	public void saveXML(String filename) throws CSenseException {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			_properties.storeToXML(fos, "");
			fos.close();
		} catch (FileNotFoundException e) {
			throw new CSenseException(filename + " cannot be created", e);
		} catch (IOException e) {
			throw new CSenseException("failed to write to " + filename, e); 
		}
	}
	
	public void saveXML() throws CSenseException {
		if(_filename == null || _filename.equals("")) throw new CSenseException("Output filename has to be specified");
		saveXML(_filename);
	}
}
