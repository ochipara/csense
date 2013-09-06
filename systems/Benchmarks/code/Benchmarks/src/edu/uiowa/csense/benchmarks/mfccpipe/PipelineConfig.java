package edu.uiowa.csense.benchmarks.mfccpipe;

import edu.uiowa.csense.compiler.model.Project;

public class PipelineConfig {
    protected int frequency = 44100;
    //protected int frequency = 16000;
    protected int frameSize = 128;
    protected int melComponents = 12;
    protected int audioBufferSize;
    //protected int frameLength = 16000;
    
    public PipelineConfig() {
	//audioBufferSize = frequency;
	audioBufferSize = 25 * 128;
    }
    
    public int getFrequency() {	
	return frequency;
    }

    public int melComponents() {
	return melComponents;
    }

    public String location() {
	String location = Project.getProject().getProjectDirectory() + "/code/matlab/octav/data/config.mat";
	return location;
    }

    public int getFftLength() {
        int fftLength = (int) (1 + Math.floor(getFrameSize() / 2.0));
        return fftLength;
    }


    public int getNumFilters() {
	int numFilters = 1;
	while (numFilters < Math.floor(3*Math.log(frequency))) {
		numFilters = numFilters * 2;
	}

	return numFilters;
    }

    public int getFrameSize() {
	return frameSize;
    }

    public int getAudioBufferSize() {
	return audioBufferSize;
    }
    
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("frequency:    " + frequency + "\n");
	sb.append("frameSize:    " + frameSize + "\n");
	sb.append("melComponents:" + melComponents + "\n");
	sb.append("audioBufferSize: " + audioBufferSize + "\n");	
	
	return sb.toString();
    }

    public void setFrequency(int frequency) {
	this.frequency = frequency;
    }

    public void setAudioBufferSize(int minAudioBuffer) {
	this.audioBufferSize = minAudioBuffer;
    }
}
