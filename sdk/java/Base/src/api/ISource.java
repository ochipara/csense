package api;

public interface ISource<T extends Message> extends IComponent {
    //public void setupMessagePoolFromTypeInfo(CSenseSource<T> source) throws CSenseException;
    public T getNextMessageToWriteInto();
    public T getNextMessageToWriteIntoAndBlock() throws InterruptedException;   	
    public int getAvailableMessages();
//    public boolean startMessageTrace(String traceName);
//    public boolean startMessageTrace(String traceName, int traceSize);
//    public void stopMessageTrace();
//    public void trace(T msg);
}
