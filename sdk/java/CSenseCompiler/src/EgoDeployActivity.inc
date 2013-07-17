import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CSenseDeployActivity extends Activity
{
    private static final String TAG = "CSenseActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);	
    }

    public void start(View view) {
	Intent _serviceIntent = new Intent(this, CSenseService.class);
	Log.i(TAG, "start ACTIVITY");
	startService(_serviceIntent);
    }

    public void stop(View view) {
	Intent _serviceIntent = new Intent(this, CSenseService.class);
	Log.i(TAG, "stop ACTIVITY");
	stopService(_serviceIntent);
    }

    public void pause(View view) {
	Toast.makeText(view.getContext(), "Pause (unimplemented)", Toast.LENGTH_SHORT).show();		
    }

}
