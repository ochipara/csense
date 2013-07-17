package api;

import api.concurrent.IComponentState;

public interface IComponentInternal extends IComponent {
    public IComponentInternal getUnderlyingComponent();

    // internal state management
    @Override
    public IComponentState getState();

    // component life-cycle management
    /**
     * Transitions to ready and releases all locks on input ports
     */
    public void ready() throws CSenseException;

    /**
     * Transitions to busy
     */
    public void running() throws CSenseException;

    public boolean isEof();
}
