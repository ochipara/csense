package api;

public interface IMessageInternal extends IMessage {
    public void setUnderlyingMessage(Message msg);
}
