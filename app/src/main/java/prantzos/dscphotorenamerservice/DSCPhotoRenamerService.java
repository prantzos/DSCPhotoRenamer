package prantzos.dscphotorenamerservice;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */

/**
 * adb shell
 * am broadcast -a prantzos.dscphotorenamerservice.autostart
 * am startservice -n prantzos.dscphotorenamerservice/prantzos.dscphotorenamerservice.DSCPhotoRenamerService
 */
public class DSCPhotoRenamerService extends Service
{
	private static final String ACTION_NEW_PICTURE = "android.hardware.action.NEW_PICTURE";
	private static final String ACTION_NEW_VIDEO = "android.hardware.action.NEW_VIDEO";
	private static final String AUTO_START = "prantzos.dscphotorenamerservice.autostart";

	CameraEventReceiver cameraEventReceiver;
	DSCPhotoRenamerServiceAutoStart dscPhotoRenamerServiceAutoStart;

	@Override
	public void onCreate()
	{
		super.onCreate();

		Toast.makeText(this, "DscPhotoRenamerService created", Toast.LENGTH_LONG).show();
		Log.i("DscPhotoRenamerService", "DscPhotoRenamerService created!");

		//for debugging the service
		//android.os.Debug.waitForDebugger();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		//TODO for communication return IBinder implementation
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Toast.makeText(this, "DscPhotoRenamerService started", Toast.LENGTH_LONG).show();
		Log.i("DscPhotoRenamerService", "DscPhotoRenamerService started!");

		// register the receivers for the Camera events and the AutoStart
		RegisterReceivers();

		// The starting of the service does not depend on the intent,
		// so every time it is started the receivers are registered
		// Android system can terminate the service but since we returned START_STICKY it gets restarted
		return Service.START_STICKY;
	}

	private void RegisterReceivers()
	{
		cameraEventReceiver = new CameraEventReceiver();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(ACTION_NEW_PICTURE);
		filter1.addAction(ACTION_NEW_VIDEO);

		registerReceiver(cameraEventReceiver, filter1);

		dscPhotoRenamerServiceAutoStart = new DSCPhotoRenamerServiceAutoStart();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(AUTO_START);

		registerReceiver(dscPhotoRenamerServiceAutoStart, filter2);

	}

	// Watch the camera directory for DSC files and rename them (based on metadata???)
	// TODO: Fix as a second solution (if the Service is automatically restarted!)
	private void WatchCameraDirectory()
	{
		String ANDRO_Directory="100ANDRO";
		// DCIM folder
		File DCIM_Directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
		Log.e("dsc2utc:DCIM_Directory", DCIM_Directory.getName());
		// initialize
		File cameraDirectory = DCIM_Directory;
		// files and folders in DCIM folder
		File[] files = DCIM_Directory.listFiles();
		// for every file there
		for (File currentFile : files)
		{
			// if it is a directory
			if (currentFile.isDirectory())
			{
				Log.e("dsc2utc:currentFile", currentFile.getName());
				if (currentFile.getName().equals(ANDRO_Directory))
				{
					cameraDirectory = currentFile;
				}
				break;
			}
		}

		//final String CompleteCameraFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + ANDRO_Directory;

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Toast.makeText(this, "DscPhotoRenamerService stopped", Toast.LENGTH_LONG).show();

		// Send broadcast for restarting service
		Intent intent = new Intent(AUTO_START);
		sendBroadcast(intent);
	}

}


