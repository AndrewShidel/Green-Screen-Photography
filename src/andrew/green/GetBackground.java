package andrew.green;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.util.*;
import android.view.*;
import java.io.*;
import tucker.shidel.greenscreenphotography.R;

public class GetBackground extends Activity {
	SharedPreferences preferences;
	Uri photoFile;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		Log.d("resource folder: ", ""+metrics.densityDpi);
	}
	
	@Override
	protected void onResume() {
    		// TODO Auto-generated method stub
    		super.onResume();
	}


	@Override
	protected void onPause() {
    		// TODO Auto-generated method stub
    		super.onPause();
	}

	public void takePicture(View view) {
		dispatchTakePictureIntent();
	}

	public void getImage(View view) {
		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");

		intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
		intent.putExtra("from", 1);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		startActivity(intent);
	}

	String mCurrentPhotoPath;

	private Uri createImageFile() throws IOException {
		String fileName = "temp1" + System.currentTimeMillis();
		ContentValues values = new ContentValues();
		values.put(MediaColumns.TITLE, fileName);
		values.put(MediaColumns.DATE_ADDED, System.currentTimeMillis());
		values.put(MediaColumns.MIME_TYPE, "image/jpg");

		return getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI,
				values);
	}

	static final int REQUEST_TAKE_PHOTO = 1;

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefs = getBaseContext().getSharedPreferences(
				"com.example.app", Context.MODE_PRIVATE);
		prefs.edit().putString("pic1", photoFile.toString()).commit();
		Intent intent = new Intent(GetBackground.this, MainActivity.class);
		Log.d("a", "intent made");
		// message.show();
		intent.putExtra("uri", photoFile.toString());

		startActivity(intent);
		finish();
	}

}
