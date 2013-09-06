package edu.uiowa.csense.runtime.api.bindings;

import java.nio.channels.SelectionKey;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Feedback;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.concurrent.IState;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

public class Component implements IComponent {
    private final CSenseComponent component;

    public Component() {
	component = new CSenseComponent();
    }

    @Override
    public <T extends Frame> OutputPort<T> newOutputPort(IComponent owner,
	    String name) throws CSenseException {
	return component.newOutputPort(owner, name);
    }

    @Override
    public <T extends Frame> InputPort<T> newInputPort(IComponent owner,
	    String name) throws CSenseException {
	return component.newInputPort(owner, name);
    }

    @Override
    public InputPort<? extends Frame> getInputPort(String name)
	    throws CSenseException {
	return component.getInputPort(name);
    }

    @Override
    public OutputPort<? extends Frame> getOutputPort(String name)
	    throws CSenseException {
	return component.getOutputPort(name);
    }

    @Override
    public <T extends Frame> void link(InputPort<T> in, OutputPort<T> out)
	    throws CSenseException {
	component.link(in, out);
    }

    @Override
    public void setScheduler(IScheduler scheduler) {
	component.setScheduler(scheduler);
    }

    @Override
    public IScheduler getScheduler() {
	return component.getScheduler();
    }

    @Override
    public IState getState() {
	return component.getState();
    }

    @Override
    public void transitionTo(int newState) throws CSenseException {
	component.transitionTo(newState);
    }

    @Override
    public void onCreate() throws CSenseException {
	component.onCreate();
    }

    @Override
    public void onStart() throws CSenseException {
	component.onStart();
    }

    @Override
    public void onStop() throws CSenseException {
	component.onStop();
    }

    @Override
    public <T extends Frame> int onPush(InputPort<T> self, Frame frame) throws CSenseException {
	return component.onPush(self, frame);
    }

    @Override
    public void onInput() throws CSenseException {
	component.onInput();
    }

    @Override
    public void processInput(SelectionKey key) {
	component.processInput(key);
    }

    @Override
    public void onEvent(Event t) throws CSenseException {
	component.onEvent(t);
    }

    @Override
    public Event asTask() {
	return component.asTask();
    }

    @Override
    public String getName() {
	return component.getName();
    }

    @Override
    public void setName(String name) {
	component.setName(name);
    }

    @Override
    public int getId() {
	return component.getId();
    }

    @Override
    public void setId(int id) {
	component.setId(id);
    }

    @Override
    public void error(Object... args) {
	component.error(args);
    }

    @Override
    public void warn(Object... args) {
	component.warn(args);
    }

    @Override
    public void info(Object... args) {
	component.info(args);
    }

    @Override
    public void debug(Object... args) {
	component.debug(args);
    }

    @Override
    public boolean equals(Object obj) {
	return component.equals(obj);
    }

    @Override
    public int hashCode() {
	return component.hashCode();
    }

    @Override
    public void verbose(Object... args) {
	component.verbose(args);
    }

    @Override
    public String toString() {
	return component.toString();
    }

    @Override
    public boolean transition(int expectedState, int newState) {
	return component.transition(expectedState, newState);
    }

    @Override
    public Frame onPoll(OutputPort<? extends Frame> port)
	    throws CSenseException {
	return component.onPoll(port);
    }

    @Override
    public void setMultiplier(int m) {
	component.setMultiplier(m);
    }

    @Override
    public void feedback(int category, Feedback<?> feedback) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void registerFeedback(int category, IComponent source) {
	// TODO Auto-generated method stub
	
    }
}
