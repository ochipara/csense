package project.configuration;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tool")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyTool extends Tool {
    public static final String TOOL_NAME = "keytool";
    
    @XmlElement(name = "keystore")
    String _keystore;
    @XmlElement(name = "keystorePassword")
    String _passwd1;
    @XmlElement(name = "alias")
    String _alias;
    @XmlElement(name = "aliasPassword")
    String _passwd2;

    public KeyTool() {
	setName(TOOL_NAME);
    }

    public String getKeystore() {
	return new File(_keystore).getAbsolutePath();
    }
    
    public String getKeystorePassword() {
	return _passwd1;
    }
    
    public String getAlias() {
	return _alias;
    }
    
    public String getAliasPassword() {
	return _passwd2;
    }
    
    public void setKeystore(String keystore) {
	_keystore = keystore == null ? "" : new File(keystore).getAbsolutePath();
    }
    
    public void setKeystorePassword(String passwd) {
	_passwd1 = passwd == null ? "" : passwd;
    }
    
    public void setAlias(String alias) {
	_alias = alias == null ? "" : alias;
    }
    
    public void setAliasPassword(String passwd) {
	_passwd2 = passwd == null ? "" : passwd;
    }
}
