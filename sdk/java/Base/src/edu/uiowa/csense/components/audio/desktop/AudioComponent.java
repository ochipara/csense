package edu.uiowa.csense.components.audio.desktop;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class AudioComponent extends CSenseSource<DoubleVector> {
    // Ports
    public OutputPort<DoubleVector> audio = newOutputPort(this, "audio");
    
    // automatically retrieved
    protected final int nFrameSize;
    protected final int _numSamples;
    protected AudioFormat audioFormat;
    protected SimpleAudioRecorder recorder = null;

    /**
     * The SimpleAudioRecord will record obtain the sound from the sound card
     * and push it out the audio port
     * 
     * @author ochipara
     * 
     */
    class SimpleAudioRecorder extends Thread {
	private TargetDataLine m_line = null;
	private AudioInputStream m_audioInputStream = null;

	public SimpleAudioRecorder(TargetDataLine line) {
	    m_line = line;
	    m_audioInputStream = new AudioInputStream(line);
	}

	@Override
	public synchronized void start() {
	    m_line.start();
	    super.start();
	}

	@Override
	public void run() {
	    final byte[] buffer = new byte[_numSamples * 2];
	    
	    while (true) {
		int offset = 0;		
		if (m_line.available() == m_line.getBufferSize()) {
		    System.err.println("Buffer overrun on the line");
		}
		
		while (offset < buffer.length) {
		    int r = m_line.read(buffer, offset, buffer.length - offset);
		    offset += r;		    
		}
			
				
		DoubleVector vector = getNextMessageToWriteInto();	
		
		assert(vector.position() == 0);
		
		offset = 0;			

		while (vector.remaining() > 0) { 
		    double sample = (  (buffer[offset + 0] & 0xFF) | (buffer[offset + 1] << 8) ) / 32768.0;
		    offset = offset + 2;
		    vector.put(sample);
		}
		
		assert(offset == buffer.length);
		assert(vector.remaining() == 0);
		
		vector.position(0);

		// push the vector down the next components
		try {
		    audio.push(vector);
		} catch (CSenseException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

	public synchronized void stopRecording() {
	    m_line.stop();
	}
    }

    // TODO: I do not like how this is setup. In a perfect world, the numSample
    // is defined by the type of the port, without having to pass arguments
    public AudioComponent(double frequency, int numSamples)
	    throws CSenseException {
	super(TypeInfo.newDoubleVector(numSamples));
	audio.setSupportPull(true);

	_numSamples = numSamples;
	final int nBitsPerSample = 16;
	final int nChannels = 1;
	nFrameSize = (nBitsPerSample / 8) * nChannels;
	
//	frequency = 16000;
	audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
		(float) frequency, nBitsPerSample, nChannels, nFrameSize, (float) frequency, false);

	DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
	TargetDataLine targetDataLine = null;

	try {
	    Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
	    for (int i = 0; i < aInfos.length; i++) {
		//System.out.println(aInfos[i].getName());
		if ("Built-in Microph".equals(aInfos[i].getName())) {
		    System.out.println("Found built-in mixer");
		    Mixer mixer = AudioSystem.getMixer(aInfos[i]);
		    targetDataLine = (TargetDataLine) mixer.getLine(info);
		    targetDataLine.open(audioFormat);
		    
		    recorder = new SimpleAudioRecorder(targetDataLine);
		    return;
		}
	    }
	    
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR);
	} catch (LineUnavailableException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// TODO: the compiler should actually infer how the buffers are setup.
	// It is unclear what the performance trade-offs are
	// in using a direct memory access buffer as opposed to one without
	// direct access.
    }

    @Override
    public void onStart() throws CSenseException {
	recorder.start();
    }

    @Override
    public void onStop() throws CSenseException {
	recorder.stopRecording();
    }
}
