package components.sensors.android;

import java.nio.ByteBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Command;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.ByteVector;
import edu.uiowa.csense.runtime.types.ShortVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


/**
 * Responsible for recording uncompressed audio 
 * Performance note:
 * 	 
 * The AudioReader.read does not fire exactly at 16KHz:
 * The mean duration between starting and ending.
 *	- mean: 999.7854ms 
 *	- std:   25.1431ms 
 *	- min:  970.7031ms
 *	- max: 1153.5034ms
 *  
 * @author Austin
 *
 */
public class AudioComponent extends CSenseSource<ShortVector> {	
    public final OutputPort<ShortVector> audio = newOutputPort(this, "audio");

    public enum State { 
	INITIALIZING, 	// recorder is initializing
	READY, 			// recorder has been initialized, recorder not yet started
	RECORDING, 		// recording
	ERROR, 			// reconstruction needed
	STOPPED			// reset needed
    };

    public static final int AUDIO_BLOCK_SIZE_IN_SAMPLES = 512;	
    public static final int FRAME_DURATION = (1000 * AUDIO_BLOCK_SIZE_IN_SAMPLES / 16000) * 8;

    // Recorder used for uncompressed recording
    private AudioRecord 	 _audioRecorder = null;

    // Recorder state; see State
    private static State			 _state;

    // Number of channels, sample rate, sample size(size in bits), buffer size, audio source, sample size(see AudioFormat)

    private static final short _numChannels = 1; // Assumes -> AudioFormat.CHANNEL_CONFIGURATION_MONO
    private static int _sRate;
    private static short  _sampleSizeInBits = 16; // we need 16 bit values for uncompressed audio!
    private static int			_bufferSize;
    private static int			_audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    // Number of frames written to file on each output(only in uncompressed mode)

    private ShortVector 			_msg = null;
    private static AudioComponentReader    	_recorderThread = null;

    class AudioComponentReader extends CSenseInnerThread {
	private static final String TAG = "Audio";

	public AudioComponentReader() {
	    super("AudioComponentReader");
	}

	@Override
	public void doRun() {
	    while(_state == AudioComponent.State.RECORDING) {				
		_msg = getNextMessageToWriteInto();
		if (_msg != null ) {   
		    ByteBuffer buffer = _msg.getBuffer();
		    do {
			int bytes = _audioRecorder.read(buffer, buffer.remaining());
			buffer.position(buffer.position() + bytes);
		    } while((buffer.remaining() > 0) && (_state == AudioComponent.State.RECORDING));

//		    Log.d(TAG, "remaining " + getAvailableMessages());
		    if (_state == AudioComponent.State.RECORDING) {
			try {
			    _msg.position(0);
			    audio.push(_msg);
			} catch (CSenseException e) {				
			    e.printStackTrace();
			}					
		    }
		} else {
		    Log.e(getName(), "component is starved");
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {			
			e.printStackTrace();
		    }
		}
	    }

	}

    }

    /**
     * Constructor that sets up our audio recording object for uncompressed recording.
     * @param numOutputComponents
     */
    public AudioComponent(TypeInfo audioType, int sampleRate) throws CSenseException {
	super(audioType);
	_sRate = sampleRate;
	_bufferSize = audioType.getNumberOfElements() * audioType.getElementSize(); //frameSizeInSamples * _sampleSizeInBits * _numChannels / 8;
	if (_bufferSize < AudioRecord.getMinBufferSize(_sRate, AudioFormat.CHANNEL_IN_MONO, _audioFormat)){ 				
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "Minumum buffer size below requirement");
	}		
    }

    @Override
    /**
     * 
     * Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and the file path was not set
     * the recorder is set to the ERROR state, which makes a reconstruction necessary.
     * In case uncompressed recording is toggled, the header of the wave file is written.
     * In case of an exception, the state is changed to ERROR
     * 	 
     */
    public void onCreate() throws CSenseException {
	super.onCreate();
	_audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 
		_sRate, 
		AudioFormat.CHANNEL_IN_MONO, 
		AudioFormat.ENCODING_PCM_16BIT, 
		_bufferSize);
	if (_audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
	    System.err.println("rate=" + _sRate);
	    throw new RuntimeException("AudioRecord initialization failed");
	}
	_state = State.READY;
    }

    @Override
    /**
     * Starts the recording, and sets the state to RECORDING.
     * Call after prepare().
     */
    public void onStart() throws CSenseException {
	super.onStart();
	_recorderThread = new AudioComponentReader();	
	_state = State.RECORDING;
	_audioRecorder.startRecording();
	_recorderThread.start();		
    }

    @Override
    /**
     * Stops the recording, and sets the state to STOPPED. In case of further usage, a reset is needed.
     * Also finalizes the wave file in case of uncompressed recording.
     */
    public void onStop() throws CSenseException {
	try {
	    if (State.RECORDING == _state) {
		_state = State.STOPPED;
		_recorderThread.join();
		_audioRecorder.stop();
	    }	
	    if(_audioRecorder != null)  _audioRecorder.release();
	    _audioRecorder = null;
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	super.onStop();
    }
}
