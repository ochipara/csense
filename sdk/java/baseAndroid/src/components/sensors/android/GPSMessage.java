package components.sensors.android;

import android.location.Location;
import api.CSenseException;
import api.IMessagePool;
import messages.RawMessage;
import messages.TypeInfo;

public class GPSMessage extends RawMessage {
	protected double longitude;
	protected double latitude;
	protected double accuracy;
	
	public static final TypeInfo<GPSMessage> type = new TypeInfo<GPSMessage>(GPSMessage.class);
	
	public GPSMessage(IMessagePool<? extends RawMessage> pool, TypeInfo<? extends RawMessage> type) throws CSenseException {
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
