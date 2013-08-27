package components.sensors.android;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.location.Location;

public class GPSMessage extends RawFrame {
	protected double longitude;
	protected double latitude;
	protected double accuracy;
	
	public static final TypeInfo<GPSMessage> type = new TypeInfo<GPSMessage>(GPSMessage.class);
	
	public GPSMessage(FramePool<? extends RawFrame> pool, TypeInfo<? extends RawFrame> type) throws CSenseException {
		super(pool, type);
	}
	
	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

	@Override
	public void initialize() {
		super.initialize();
		longitude = 0;
		latitude = 0;
		accuracy = 0;
	}

	public static TypeInfo<GPSMessage> type() {
		return type;
	}

	public void update(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		accuracy = location.getAccuracy();				
	}

}
