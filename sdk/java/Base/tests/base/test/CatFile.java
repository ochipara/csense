package base.test;

import components.basic.DisplayFrame;
import components.basic.TapComponent;
//import components.storage.FromDiskComponent;

import api.CSense;
import api.CSenseException;
import api.IScheduler;
import messages.TypeInfo;
import messages.fixed.CharVector;

public class CatFile {
    private IScheduler _scheduler;

    protected void InitializeSystem() throws CSenseException {
	int size = 1024;
	_scheduler = new CSense("v0").newScheduler();

	// instantiate components
	DisplayFrame<CharVector> displayC = new DisplayFrame<CharVector>(
		TypeInfo.newCharVector(1024));
	_scheduler.addComponent(displayC);
	// FromDiskComponent fromDiskC = new FromDiskComponent("/etc/passwd",
	// size);
	// _scheduler.addComponent(fromDiskC);
	TapComponent<CharVector> tapC = new TapComponent<CharVector>();
	_scheduler.addComponent(tapC);

	// link components
	// fromDiskC.getOutputPort("out").link(displayC.getInputPort("in"));
	displayC.getOutputPort("out").link(tapC.getInputPort("in"));
	_scheduler.start();
    }

    public static void main(String[] args) throws CSenseException {
	CatFile service = new CatFile();
	service.InitializeSystem();
    }
}
