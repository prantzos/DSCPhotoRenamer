package prantzos.dscphotorenamerservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bill on 28/6/2015.
 */
public class CameraEventReceiver extends BroadcastReceiver
{
	private static final String ACTION_NEW_PICTURE = "android.hardware.action.NEW_PICTURE";
	private static final String ACTION_NEW_VIDEO = "android.hardware.action.NEW_VIDEO";
	//private static final String ACTION_NEW_PICTURE2 = "com.android.camera.NEW_PICTURE";
	//private static final String ACTION_NEW_VIDEO2 = "com.android.camera.NEW_VIDEO";
	private static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			// get action from the incoming intent
			final String action = intent.getAction();

			if (ACTION_NEW_PICTURE.equals(action)
				|| ACTION_NEW_VIDEO.equals(action)
				|| ACTION_CAMERA_BUTTON.equals(intent.getAction()))
			{
				// get image path from the camera intent
				Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
				cursor.moveToFirst();
				String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
				cursor.close();

				Uri imagePathUri = new Uri.Builder().path(imagePath).build();
				intent.setData(imagePathUri);

				String suffix = "jpg";
				if (ACTION_NEW_PICTURE.equals(intent.getAction()))
					suffix = "jpg";
				else if (ACTION_NEW_VIDEO.equals(intent.getAction()))
					suffix = "mp4";

				RenamePicture(context, intent, suffix);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e("onReceive", ex.getMessage());
		}
	}

	private void RenamePicture(Context context, Intent intent, String suffix)
	{
		try
		{
			// get image path from intent
			String imagePath = intent.getDataString();
			//Log.d("RenamePicture:imagePath", imagePath);

			// create a uri from the image path
			Uri imagePathUri = new Uri.Builder().path(imagePath).build();
			// get the filename from the full image path
			String imageFilename = imagePathUri.getLastPathSegment();
			// get the folder the image is in. (Remove the image file name and the folder is left)
			File camDirectory = new File(imagePath.replace(imageFilename, ""));

			if (!camDirectory.exists())
				return;

			// create a File for the existing file name
			File from = new File(camDirectory, imageFilename);

			// new filename
			String newFileName = GetFormattedDateTime(null) + "." + suffix;

			// create a File for the new filename
			File to = new File(camDirectory, newFileName);

			// if the source file exists, then rename it to the target file
			if (from.exists())
			{
				//boolean isWritable = from.setWritable(true);
				boolean success = from.renameTo(to);
				if (success)
				{
					Toast.makeText(context, imageFilename + " renamed to " + newFileName, Toast.LENGTH_LONG).show();
					Log.d("RenamePicture", imageFilename + " renamed to " + newFileName);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e("RenamePicture", ex.getMessage());
		}
	}

	public static String GetFormattedDateTime(Date date)
	{
		try
		{
			DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", new Locale("en-GB"));

			// if the parameter is null, we use the current Date
			Date dt = new Date();
			if (date != null)
				dt = date;

			return df.format(dt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e("GetFormattedDateTime", ex.getMessage());
			return "";
		}
	}
}
