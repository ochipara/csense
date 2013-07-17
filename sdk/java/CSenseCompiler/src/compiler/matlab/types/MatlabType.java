package compiler.matlab.types;

/**
 * 
 * @author ochipara
 * 
 * @param <T>
 */

public abstract class MatlabType {
    public abstract String getStringValue();

    /**
     * 
     * @return the underlying matlab type
     */
    public abstract String getCodegenType();

    /**
     * 
     * @return the underlying c type
     */
    public abstract String getCType();

    /**
     * 
     * @return
     */
    public abstract int getNumberOfElements();

    /**
     * 
     * @return
     */
    public abstract String getNioType();

    public abstract int getNumberOfBytes();

    public abstract boolean isPrimitive();
}
