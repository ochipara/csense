
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
		try {
			InitializeSystem();
			Toast.makeText(this, "EgoService created...", Toast.LENGTH_SHORT).show();	
		} catch (CSenseException e) { 
			e.printStackTrace();
			Toast.makeText(this, "EgoService WAS NOT CREATED...", Toast.LENGTH_SHORT).show();	
		}
		//Log.level(Log.DEBUG);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(_scheduler != null && !_scheduler.isActive()) _scheduler.start();
		return START_STICKY;
	}

	@Override
	public void onDestroy(){
		//Log.e("onDestroy");
		if(_scheduler != null && _scheduler.isActive()) _scheduler.stop();
		Toast.makeText(this, "EgoService destroyed...", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	/**
	 * This function initializes the system that the toolkit user specified.
	 * It instantiates the scheduler and all of the components.
	 * Lastly, it initializes each component.
	 */
	private void InitializeSystem() throws CSenseException {  	
		//System.setErr ( new LogCatRedirect ( System.err ) );
		//System.setOut ( new LogCatRedirect ( System.out ) );



			