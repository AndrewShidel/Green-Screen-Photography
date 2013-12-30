package andrew.green;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import tucker.shidel.greenscreen.R;

public class GetBackground extends Activity
{
	SharedPreferences preferences;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		


		//	String uri=preferences.getString("uri","empty");

//		Toast message = Toast.makeText(MainActivity.this,uri, Toast.LENGTH_SHORT);
//		message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
//						   message.getYOffset() / 2);
//		message.show(); // display the Toast
//     
    }

	public void takePicture(View view){

		Log.d("a","clicked");
		Intent intent=new Intent(GetBackground.this,PictureTaker.class);
		Log.d("a","intent made");
		intent.putExtra("a",1);

		startActivity(intent); 
		Log.d("a","activity started");


	}
	public void getImage(View view){
		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");

        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
		intent.putExtra("from",1);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

//alternatively you can set file filter //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		//	startActivityForResult(intent, 0);
		startActivity(intent);
	}












	String mCurrentPhotoPath;

	private Uri createImageFile() throws IOException {
		String fileName = "temp1" + System.currentTimeMillis();
	    // create a ContentValues and configure new image's data
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, fileName);
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpg");
			

		// get a Uri for the location to save the file
		return getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	}


	static final in REQUEST_TAKE_PHOTO = 1;

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
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    photoFile);
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefs = getBaseContext().getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
		prefs.edit().putString("pic1", photoFile.toString()).commit();
		Intent intent=new Intent(GetBackground.this,MainActivity.class);
		Log.d("a","intent made");
					//message.show();
		intent.putExtra("uri",photoFile.toString());

		startActivity(intent); 
		finish();
	}

	
}






}
