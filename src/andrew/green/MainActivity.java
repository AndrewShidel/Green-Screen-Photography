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
import android.widget.*;
import java.io.*;
import tucker.shidel.greenscreen.R;

public class MainActivity extends Activity {
	SharedPreferences preferences;
	String s;
	Uri photoFile;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button tb = (Button) findViewById(R.id.tb);
		TextView t1 = (TextView) findViewById(R.id.t1);
		TextView t2 = (TextView) findViewById(R.id.t2);
		t1.setText(getResources().getString(R.string.title2));
		t2.setText(getResources().getString(R.string.text2));

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			s = extras.getString("uri");
		}
	}

	public void takePicture(View view) {

		dispatchTakePictureIntent();

	}

	public void getImage(View view) {
		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");

		intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
		intent.putExtra("from", 2);
		intent.putExtra("uri", s);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		startActivity(intent);
	}

	String mCurrentPhotoPath;

	private Uri createImageFile() throws IOException {
		String fileName = "temp2" + System.currentTimeMillis();
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
		Intent intent = new Intent(MainActivity.this, Pic1View.class);
		Log.d("a", "intent made");
		intent.putExtra("uri", photoFile.toString());
		intent.putExtra("source", 2);
		startActivity(intent);
		finish();
	}

}
