package prantzos.dscphotorenamerservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Bill on 28/6/2015.
 */
public class DSCPhotoRenamerServiceAutoStart extends BroadcastReceiver
{
	//private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private static final String AUTO_START = "prantzos.dscphotorenamerservice.autostart";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
			|| Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(intent.getAction())
			|| AUTO_START.equals(intent.getAction()))
		{
			Intent photoRenamerIntent = new Intent(context, DSCPhotoRenamerService.class);
			ComponentName dSCPhotoRenamerService = context.startService(photoRenamerIntent);
			Log.i("AutoStart", "DSCPhotoRenamerService starting...");
		}
	}
}