package edu.uiowa.csense.runtime.api;

/**
 * Creates a frame pool
 * 
 * @author ochipara
 *
 */
public interface FramePool {
    public void put(Frame m);
    public Frame get();
    public Frame getAndBlock() throws InterruptedException;
    public int getAvailable();
    public void setSource(ISource source);
}
