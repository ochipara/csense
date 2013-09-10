package edu.uiowa.csense.runtime.api;

public class Feedback<T> extends Event {
    protected final Frame frame;
    protected final T data;
    protected int category;

    public Feedback(Frame frame, T data) {
	this.frame = frame;
	this.data = data;
    }

    public Frame getFrame() {
	return frame;
    }

    public T getData() {
	return data;
    }

    public void setCategory(int category) {
	this.category = category;
    }    

    public int getCategory() {
	return category;
    }

    public boolean isFeedback() {
	return true;
    }
}
